package com.research.ems.repository;

import com.research.ems.entity.WorkShift;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface WorkShiftRepository
        extends JpaRepository<WorkShift, Long> {

    Optional<WorkShift> findByNameIgnoreCase(
            String name
    );

    boolean existsByNameIgnoreCase(
            String name
    );

    boolean existsByNameIgnoreCaseAndIdNot(
            String name,
            Long id
    );

    List<WorkShift> findByActiveTrue();
}