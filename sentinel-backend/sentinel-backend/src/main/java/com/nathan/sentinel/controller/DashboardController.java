package com.nathan.sentinel.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nathan.sentinel.service.DockerStatsService;
import com.nathan.sentinel.service.dto.ContainerSummaryDto;

@RestController
@RequestMapping("/api/v1/dashboard")
public class DashboardController {
    private final DockerStatsService dockerStatsService;

    public DashboardController(DockerStatsService dockerStatsService){
        this.dockerStatsService = dockerStatsService;
    }

    @GetMapping("/summary")
    public ContainerSummaryDto getDashboardSummary(){
        return dockerStatsService.getContainerSummary();
    }

}
