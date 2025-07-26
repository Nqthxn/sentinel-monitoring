package com.nathan.sentinel.service;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.async.ResultCallback;
import com.github.dockerjava.api.model.Container;
import com.github.dockerjava.api.model.Statistics;
import com.nathan.sentinel.service.dto.ContainerStatsDto;


@Service
public class DockerStatsService {
    private final DockerClient dockerClient;

    public DockerStatsService(DockerClient dockerClient){
        this.dockerClient = dockerClient;
    }

    public List<ContainerStatsDto> getRunningContainers(){
        List<Container> containers = dockerClient.listContainersCmd()
            .withShowAll(false)
            .exec();

        return containers.stream()
            .map(this::mapToDto)
            .collect(Collectors.toList());
    }


    public ContainerStatsDto mapToDto(Container container){
        Statistics stats = getLatestStats(container.getId());

        long usageBytes = 0;
        long limitBytes = 0;

        if(stats.getMemoryStats() != null){
            usageBytes = stats.getMemoryStats().getUsage();
            limitBytes = stats.getMemoryStats().getLimit();
        }

        var memStats = new ContainerStatsDto.MemoryStatsDto(usageBytes, limitBytes);

        double cpuUsagePercent = 0.0;
        if (stats.getPreCpuStats() != null && stats.getCpuStats() != null &&
            stats.getPreCpuStats().getSystemCpuUsage() != null && stats.getCpuStats().getSystemCpuUsage() != null &&
            stats.getCpuStats().getCpuUsage() != null && stats.getPreCpuStats().getCpuUsage() != null) {

            long cpuDelta = stats.getCpuStats().getCpuUsage().getTotalUsage() - stats.getPreCpuStats().getCpuUsage().getTotalUsage();
            long systemCpuDelta = stats.getCpuStats().getSystemCpuUsage() - stats.getPreCpuStats().getSystemCpuUsage();

            if (systemCpuDelta > 0 && cpuDelta > 0) {
                cpuUsagePercent = ((double) cpuDelta / (double) systemCpuDelta) * stats.getCpuStats().getOnlineCpus() * 100.0;
            }
        }
        var cpuStats = new ContainerStatsDto.CpuStatsDto(cpuUsagePercent);

        long rxBytes = 0L;
        long txBytes = 0L;
        if (stats.getNetworks() != null) {
            rxBytes = stats.getNetworks().values().stream().mapToLong(net -> net.getRxBytes()).sum();
            txBytes = stats.getNetworks().values().stream().mapToLong(net -> net.getTxBytes()).sum();
        }
        var netStats = new ContainerStatsDto.NetworkStatsDto(rxBytes, txBytes);

        String name = (container.getNames() != null && container.getNames().length > 0) ? container.getNames()[0].substring(1) : container.getId().substring(0, 12);


        return new ContainerStatsDto(
            container.getId(),
            name, 
            container.getStatus(),
            container.getImage(),
            cpuStats,
            memStats,
            netStats
        );
    }

    
    private Statistics getLatestStats(String containerId) {
        final Statistics[] stats = new Statistics[1];
        final CountDownLatch latch = new CountDownLatch(1);

        ResultCallback.Adapter<Statistics> callback = new ResultCallback.Adapter<>() {
            @Override
            public void onNext(Statistics statistics) {
                stats[0] = statistics;
                latch.countDown();
                try {
                    close();
                } catch (IOException e) {
                }
            }
        };

        try {
            dockerClient.statsCmd(containerId).exec(callback);
            latch.await(2, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        return stats[0] != null ? stats[0] : new Statistics();
    }

}
