package com.nathan.sentinel.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "container_stats")
@Getter
@Setter
public class ContainerStat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; 

    private String containerId;
    private LocalDateTime timestamp;
    private double cpuUsagePercent;
    private long memoryUsageBytes;
    private long memoryLimitBytes;
    private long networkRxBytes;
    private long networkTxBytes;

    public ContainerStat(){}

    public ContainerStat(String containerId, LocalDateTime timestamp, double cpuUsagePercent, long memoryUsageBytes, long memoryLimitBytes, long networkRxBytes, long networkTxBytes){
        this.containerId = containerId;
        this.timestamp = timestamp;
        this.cpuUsagePercent = cpuUsagePercent;
        this.memoryUsageBytes = memoryUsageBytes;
        this.memoryLimitBytes = memoryLimitBytes;
        this.networkRxBytes = networkRxBytes;
        this.networkTxBytes = networkTxBytes;
    }
}
