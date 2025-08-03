package com.nathan.sentinel.service.dto;

public record ContainerHistorySummaryDto (
    long totalReadings,
    double averageCpuUsage,
    double averageMemoryUsage,
    double maxCpuUsage,
    long maxMemoryUsage
) {
    
}
