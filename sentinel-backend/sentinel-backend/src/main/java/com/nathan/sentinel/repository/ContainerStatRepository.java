package com.nathan.sentinel.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.nathan.sentinel.entity.ContainerStat;

@Repository

public interface ContainerStatRepository extends JpaRepository<ContainerStat, Long>{
    List<ContainerStat> findByContainerIdAndTimestampAfter(String containerId, LocalDateTime timestamp);
    
}
