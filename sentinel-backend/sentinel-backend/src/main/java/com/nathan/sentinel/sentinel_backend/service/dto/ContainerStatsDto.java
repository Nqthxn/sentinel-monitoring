package com.nathan.sentinel.sentinel_backend.service.dto;

public record ContainerStatsDto(
    String id, 
    String name, 
    String status, 
    String image, 
    CpuStatsDto cpu, 
    MemoryStatsDto memory, 
    NetworkStatsDto network 
){
    public record CpuStatsDto(double usagePercent){}

    public record MemoryStatsDto(long usageBytes, long limitBytes){}

    public record NetworkStatsDto(long rxBytes, long txBytes){}
}