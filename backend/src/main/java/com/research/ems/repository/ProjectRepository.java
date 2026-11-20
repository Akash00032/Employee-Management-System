package com.research.ems.repository;

import com.research.ems.entity.Project;

import com.research.ems.entity.enums.ProjectStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProjectRepository
        extends JpaRepository<Project, Long> {

    Optional<Project> findByCodeIgnoreCase(
            String code
    );

    Optional<Project> findByNameIgnoreCase(
            String name
    );

    boolean existsByCodeIgnoreCase(
            String code
    );

    boolean existsByCodeIgnoreCaseAndIdNot(
            String code,
            Long id
    );

    Page<Project> findByStatus(
            ProjectStatus status,
            Pageable pageable
    );

    Page<Project> findByProjectManagerId(
            Long managerId,
            Pageable pageable
    );

    long countByStatus(ProjectStatus status);
}
