package com.nathan.sentinel.service.dto;

public record ContainerSummaryDto (
    long runningContainers,
    long stoppedContainers,
    long totalImages,
    long totalDataSaved
) {
    
}
