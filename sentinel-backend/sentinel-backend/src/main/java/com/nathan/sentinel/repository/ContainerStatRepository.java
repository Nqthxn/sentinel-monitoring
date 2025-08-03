package com.nathan.sentinel.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.nathan.sentinel.entity.ContainerStat;
import com.nathan.sentinel.service.dto.ContainerHistorySummaryDto;

@Repository

public interface ContainerStatRepository extends JpaRepository<ContainerStat, Long>{
    List<ContainerStat> findByContainerIdAndTimestampAfter(String containerId, LocalDateTime timestamp);

    @Query("""
        SELECT new com.nathan.sentinel.service.dto.ContainerHistorySummaryDto(
            COALESCE(COUNT(s.id), 0),
            COALESCE(AVG(s.cpuUsagePercent), 0.0),
            COALESCE(AVG(s.memoryUsageBytes), 0.0),
            COALESCE(MAX(s.cpuUsagePercent), 0.0),
            COALESCE(MAX(s.memoryUsageBytes), 0)
        )
        FROM ContainerStat s
        WHERE s.containerId = :containerId
    """)
    ContainerHistorySummaryDto getHistorySummaryForContainer(@Param("containerId") String containerId);
}
