package com.nathan.sentinel.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.nathan.sentinel.entity.ContainerStat;

@Repository

public interface ContainerStatRepository extends JpaRepository<ContainerStat, Long>{

}
