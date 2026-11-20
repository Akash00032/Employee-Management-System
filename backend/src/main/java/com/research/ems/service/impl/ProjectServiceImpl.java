package com.research.ems.service.impl;

import com.research.ems.entity.Employee;
import com.research.ems.entity.Project;

import com.research.ems.dto.project.ProjectRequestDto;
import com.research.ems.dto.project.ProjectResponseDto;
import com.research.ems.mapper.ProjectMapper;
import com.research.ems.repository.EmployeeRepository;
import com.research.ems.repository.ProjectRepository;
import com.research.ems.service.ProjectService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ProjectServiceImpl implements ProjectService {

    private final ProjectRepository projectRepository;
    private final EmployeeRepository employeeRepository;
    private final ProjectMapper projectMapper;

    public ProjectServiceImpl(
            ProjectRepository projectRepository,
            EmployeeRepository employeeRepository,
            ProjectMapper projectMapper) {

        this.projectRepository = projectRepository;
        this.employeeRepository = employeeRepository;
        this.projectMapper = projectMapper;
    }

    @Override
    public ProjectResponseDto createProject(
            ProjectRequestDto dto) {

        if (projectRepository.existsByCodeIgnoreCase(dto.getCode())) {
            throw new IllegalArgumentException(
                    "Project code already exists: " + dto.getCode());
        }

        Project project = projectMapper.toEntity(dto);

        if (dto.getProjectManagerId() != null) {
            project.setProjectManager(
                    findEmployee(dto.getProjectManagerId()));
        }

        return projectMapper.toResponseDto(
                projectRepository.save(project));
    }

    @Override
    @Transactional(readOnly = true)
    public ProjectResponseDto getProjectById(Long id) {

        return projectMapper.toResponseDto(find(id));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProjectResponseDto> getAllProjects(int page, int size) {

        Pageable pageable = PageRequest.of(page, size);
        return projectRepository.findAll(pageable)
                .map(projectMapper::toResponseDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProjectResponseDto> getProjectsByStatus(
            com.research.ems.entity.enums.ProjectStatus status,
            int page,
            int size) {
        return projectRepository.findByStatus(status, PageRequest.of(page, size))
                .map(projectMapper::toResponseDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProjectResponseDto> getProjectsByManager(
            Long managerId,
            int page,
            int size) {
        return projectRepository.findByProjectManagerId(
                        managerId, PageRequest.of(page, size))
                .map(projectMapper::toResponseDto);
    }

    @Override
    public ProjectResponseDto updateProject(
            Long id,
            ProjectRequestDto dto) {

        Project project = find(id);

        if (projectRepository.existsByCodeIgnoreCaseAndIdNot(
                dto.getCode(), id)) {
            throw new IllegalArgumentException(
                    "Project code already exists");
        }

        projectMapper.updateEntity(project, dto);

        if (dto.getProjectManagerId() != null) {
            project.setProjectManager(
                    findEmployee(dto.getProjectManagerId()));
        } else {
            project.setProjectManager(null);
        }

        return projectMapper.toResponseDto(
                projectRepository.save(project));
    }

    @Override
    public void deleteProject(Long id) {

        projectRepository.delete(find(id));
    }

    private Project find(Long id) {

        return projectRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Project not found with id: " + id));
    }

    private Employee findEmployee(Long id) {

        return employeeRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Employee not found with id: " + id));
    }
}