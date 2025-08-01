package com.nathan.sentinel.service;

import java.util.List;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.nathan.sentinel.service.dto.ContainerStatsDto;

@Service
public class StatsPublisherService {
    private final DockerStatsService dockerStatsService;
    private final SimpMessagingTemplate messagingTemplate;

    public StatsPublisherService(DockerStatsService dockerStatsService, SimpMessagingTemplate messagingTemplate){
        this.dockerStatsService = dockerStatsService;
        this.messagingTemplate = messagingTemplate;
    }

    @Scheduled(fixedRate = 2000)
    public void publishContainerStats(){
        List<ContainerStatsDto> stats = dockerStatsService.getRunningContainers();
        messagingTemplate.convertAndSend("/topic/stats", stats);
    }
}
