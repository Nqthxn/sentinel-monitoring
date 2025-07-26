package com.nathan.sentinel.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nathan.sentinel.service.DockerStatsService;
import com.nathan.sentinel.service.dto.ContainerStatsDto;

@RestController
@RequestMapping("/api/v1/containers")
public class ContainerController {
    private final DockerStatsService dockerStatsService;

    public ContainerController(DockerStatsService dockerStatsService){
        this.dockerStatsService = dockerStatsService;
    }

    @GetMapping
    public List<ContainerStatsDto> getRunningContainers(){
        return dockerStatsService.getRunningContainers();
    }

}
