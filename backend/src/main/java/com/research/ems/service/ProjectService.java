package com.research.ems.service;

import com.research.ems.dto.project.*;
import com.research.ems.entity.enums.ProjectStatus;
import org.springframework.data.domain.Page;

public interface ProjectService {

    ProjectResponseDto createProject(
            ProjectRequestDto requestDto
    );

    ProjectResponseDto getProjectById(Long id);

    Page<ProjectResponseDto> getAllProjects(
            int page,
            int size
    );

    Page<ProjectResponseDto> getProjectsByStatus(
            ProjectStatus status,
            int page,
            int size
    );

    Page<ProjectResponseDto> getProjectsByManager(
            Long managerId,
            int page,
            int size
    );

    ProjectResponseDto updateProject(
            Long id,
            ProjectRequestDto requestDto
    );

    void deleteProject(Long id);
}
