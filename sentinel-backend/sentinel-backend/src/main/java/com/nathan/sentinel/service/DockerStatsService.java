package com.nathan.sentinel.service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.async.ResultCallback;
import com.github.dockerjava.api.model.Container;
import com.github.dockerjava.api.model.Statistics;
import com.nathan.sentinel.entity.ContainerStat;
import com.nathan.sentinel.repository.ContainerStatRepository;
import com.nathan.sentinel.service.dto.ContainerHistorySummaryDto;
import com.nathan.sentinel.service.dto.ContainerStatsDto;
import com.nathan.sentinel.service.dto.ContainerSummaryDto;


@Service
public class DockerStatsService {
    private final DockerClient dockerClient;
    private final ContainerStatRepository containerStatRepository;
    
    public DockerStatsService(DockerClient dockerClient, ContainerStatRepository containerStatRepository){
        this.dockerClient = dockerClient;
        this.containerStatRepository = containerStatRepository;
    }

    public List<ContainerStatsDto> getRunningContainers(){
        List<Container> containers = dockerClient.listContainersCmd()
            .withShowAll(false)
            .exec();

        return containers.stream()
            .map(this::mapToDto)
            .collect(Collectors.toList());
    }

    public ContainerStatsDto mapToDto(Container container) {
        Statistics stats = getLatestStats(container.getId());

        long usageBytes = 0L;
        long limitBytes = 0L;
        if (stats.getMemoryStats() != null) {
            usageBytes = stats.getMemoryStats().getUsage() != null ? stats.getMemoryStats().getUsage() : 0L;
            limitBytes = stats.getMemoryStats().getLimit() != null ? stats.getMemoryStats().getLimit() : 0L;
        }
        var memStats = new ContainerStatsDto.MemoryStatsDto(usageBytes, limitBytes);

        double cpuUsagePercent = calculateCpuPercentage(stats);
        var cpuStatsDto = new ContainerStatsDto.CpuStatsDto(cpuUsagePercent);
        
        long rxBytes = 0L;
        long txBytes = 0L;
        if (stats.getNetworks() != null) {
            rxBytes = stats.getNetworks().values().stream().mapToLong(net -> net.getRxBytes() != null ? net.getRxBytes() : 0L).sum();
            txBytes = stats.getNetworks().values().stream().mapToLong(net -> net.getTxBytes() != null ? net.getTxBytes() : 0L).sum();
        }
        var netStats = new ContainerStatsDto.NetworkStatsDto(rxBytes, txBytes);

                ContainerStat statToSave = new ContainerStat(
                container.getId(),
                LocalDateTime.now(),
                cpuUsagePercent,
                usageBytes,
                limitBytes,
                rxBytes,
                txBytes
        );
        containerStatRepository.save(statToSave);


        String name = (container.getNames() != null && container.getNames().length > 0)
                ? container.getNames()[0].substring(1)
                : container.getId().substring(0, 12);

        return new ContainerStatsDto(
                container.getId(),
                name,
                container.getStatus(),
                container.getImage(),
                cpuStatsDto,
                memStats,
                netStats
        );
    }

    public double calculateCpuPercentage(Statistics stats) {
        if (stats == null || stats.getCpuStats() == null || stats.getPreCpuStats() == null) {
            return 0.0;
        }

        var cpuStats = stats.getCpuStats();
        var preCpuStats = stats.getPreCpuStats();

        if (cpuStats.getCpuUsage() == null || preCpuStats.getCpuUsage() == null ||
            cpuStats.getCpuUsage().getTotalUsage() == null || preCpuStats.getCpuUsage().getTotalUsage() == null ||
            cpuStats.getSystemCpuUsage() == null || preCpuStats.getSystemCpuUsage() == null) {
            return 0.0;
        }

        long cpuDelta = cpuStats.getCpuUsage().getTotalUsage() - preCpuStats.getCpuUsage().getTotalUsage();
        long systemCpuDelta = cpuStats.getSystemCpuUsage() - preCpuStats.getSystemCpuUsage();

        if (systemCpuDelta <= 0 || cpuDelta < 0) {
            return 0.0;
        }

        int numberOfCpus = getNumberOfCpus(cpuStats);
        
        double cpuUsagePercent = ((double) cpuDelta / (double) systemCpuDelta) * numberOfCpus * 100.0;
        
        return Math.min(cpuUsagePercent, 100.0);
    }

    private int getNumberOfCpus(com.github.dockerjava.api.model.CpuStatsConfig cpuStats) {
        if (cpuStats.getOnlineCpus() != null && cpuStats.getOnlineCpus() > 0) {
            return cpuStats.getOnlineCpus().intValue();
        }
        
        if (cpuStats.getCpuUsage() != null && cpuStats.getCpuUsage().getPercpuUsage() != null) {
            return cpuStats.getCpuUsage().getPercpuUsage().size();
        }
        return Runtime.getRuntime().availableProcessors();
    }
    
    private Statistics getLatestStats(String containerId) {
        final Statistics[] statsArray = new Statistics[2]; 
        final CountDownLatch latch = new CountDownLatch(2);

        ResultCallback.Adapter<Statistics> callback = new ResultCallback.Adapter<>() {
            private int count = 0;
            
            @Override
            public void onNext(Statistics statistics) {
                if (count < 2) {
                    statsArray[count] = statistics;
                    count++;
                    latch.countDown();
                    
                    if (count == 2) {
                        try {
                            close();
                        } catch (IOException e) {
                        }
                    }
                }
            }
        };

        try {
            dockerClient.statsCmd(containerId).exec(callback);
            boolean completed = latch.await(5, TimeUnit.SECONDS);
            
            if (!completed || statsArray[1] == null) {
                return statsArray[0] != null ? statsArray[0] : new Statistics();
            }
            
            return statsArray[1];
            
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return new Statistics();
        }
    }

    public List<ContainerStat> getHistoricalStats(String containerId, int hours){
        LocalDateTime startTime = LocalDateTime.now().minusHours(hours);

        return containerStatRepository.findByContainerIdAndTimestampAfter(containerId, startTime);
    }

    public ContainerSummaryDto getContainerSummary(){
        List<Container> allContainers = dockerClient.listContainersCmd().withShowAll(true).exec();

        long runningCount = allContainers.stream().filter(c -> c.getState().equals("running")).count();

        long stoppedCount = allContainers.size() - runningCount;

        long imageCount = dockerClient.listImagesCmd().exec().size();

        long totalDataSaved = containerStatRepository.count();

        return new ContainerSummaryDto(runningCount, stoppedCount, imageCount, totalDataSaved);
    }

    public ContainerHistorySummaryDto getHistorySummary(String containerId){
        return containerStatRepository.getHistorySummaryForContainer(containerId);
    }
}