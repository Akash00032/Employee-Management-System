package com.research.ems.repository;

import com.research.ems.entity.LeaveType;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.List;

public interface LeaveTypeRepository
        extends JpaRepository<LeaveType, Long> {

    Optional<LeaveType> findByCodeIgnoreCase(String code);

    Optional<LeaveType> findByNameIgnoreCase(String name);

    boolean existsByCodeIgnoreCase(String code);

    boolean existsByNameIgnoreCase(String name);

    boolean existsByNameIgnoreCaseAndIdNot(String name, Long id);

    boolean existsByCodeIgnoreCaseAndIdNot(
            String code,
            Long id
    );

    List<LeaveType> findByActiveTrue();
}
