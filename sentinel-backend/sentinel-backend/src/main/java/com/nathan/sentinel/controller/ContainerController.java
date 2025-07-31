package com.nathan.sentinel.controller;

import java.util.List;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.nathan.sentinel.entity.ContainerStat;
import com.nathan.sentinel.service.DockerStatsService;
import com.nathan.sentinel.service.dto.ContainerStatsDto;

@RestController
@RequestMapping("/api/v1/containers")
@CrossOrigin(origins = "http://localhost:4200")
public class ContainerController {
    private final DockerStatsService dockerStatsService;

    public ContainerController(DockerStatsService dockerStatsService){
        this.dockerStatsService = dockerStatsService;
    }

    @GetMapping
    public List<ContainerStatsDto> getRunningContainers(){
        return dockerStatsService.getRunningContainers();
    }

    @GetMapping("/{containerId}/history")
    public List<ContainerStat> getContainerHistory(
        @PathVariable String containerId, 
        @RequestParam(defaultValue="1") int hours){
            return dockerStatsService.getHistoricalStats(containerId, hours);
        }
    
}
