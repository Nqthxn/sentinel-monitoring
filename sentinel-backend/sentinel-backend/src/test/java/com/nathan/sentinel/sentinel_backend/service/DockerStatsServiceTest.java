package com.nathan.sentinel.sentinel_backend.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.model.CpuStatsConfig;
import com.github.dockerjava.api.model.CpuUsageConfig;
import com.github.dockerjava.api.model.Statistics;
import com.nathan.sentinel.repository.ContainerStatRepository; 
import com.nathan.sentinel.service.DockerStatsService;

@ExtendWith(MockitoExtension.class)
class DockerStatsServiceTest {

    @Mock
    private DockerClient dockerClient;
    @Mock
    private ContainerStatRepository containerStatRepository;

    @Mock
    private Statistics stats;
    @Mock
    private CpuStatsConfig preCpuStats;
    @Mock
    private CpuUsageConfig preCpuUsage;
    @Mock
    private CpuStatsConfig cpuStats;
    @Mock
    private CpuUsageConfig cpuUsage;

    private DockerStatsService dockerStatsService;

    @BeforeEach
    void setUp() {
        dockerStatsService = new DockerStatsService(dockerClient, containerStatRepository);
    }

    @Test
    void whenCpuStatsAreValid_thenCalculateCpuPercentageCorrectly() {
        when(stats.getCpuStats()).thenReturn(cpuStats);
        when(cpuStats.getSystemCpuUsage()).thenReturn(4000L);
        when(cpuStats.getCpuUsage()).thenReturn(cpuUsage);
        when(cpuUsage.getTotalUsage()).thenReturn(2000L);
        when(cpuStats.getOnlineCpus()).thenReturn(2L);

        when(stats.getPreCpuStats()).thenReturn(preCpuStats);
        when(preCpuStats.getSystemCpuUsage()).thenReturn(2000L);
        when(preCpuStats.getCpuUsage()).thenReturn(preCpuUsage);
        when(preCpuUsage.getTotalUsage()).thenReturn(1000L);

        double result = dockerStatsService.calculateCpuPercentage(stats);

        assertEquals(100.0, result, 0.01);
    }
}