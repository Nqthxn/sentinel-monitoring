package com.nathan.sentinel.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.model.Container;
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
        var cpuStats = new ContainerStatsDto.CpuStatsDto(0.0);
        var memStats = new ContainerStatsDto.MemoryStatsDto(0L, 0L);
        var netStats = new ContainerStatsDto.NetworkStatsDto(0L, 0L);

        String name = container.getId().substring(0, 12);

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
}
