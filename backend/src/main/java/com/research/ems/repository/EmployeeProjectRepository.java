package com.research.ems.repository;

import com.research.ems.entity.EmployeeProject;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EmployeeProjectRepository
        extends JpaRepository<EmployeeProject, Long> {

    Optional<EmployeeProject> findByEmployeeIdAndProjectId(
            Long employeeId,
            Long projectId
    );

    List<EmployeeProject> findByEmployeeId(
            Long employeeId
    );

    List<EmployeeProject> findByProjectId(
            Long projectId
    );

    List<EmployeeProject> findByEmployeeIdAndActiveTrue(
            Long employeeId
    );

    List<EmployeeProject> findByProjectIdAndActiveTrue(
            Long projectId
    );

    boolean existsByEmployeeIdAndProjectId(
            Long employeeId,
            Long projectId
    );

    long countByProjectIdAndActiveTrue(
            Long projectId
    );
}
