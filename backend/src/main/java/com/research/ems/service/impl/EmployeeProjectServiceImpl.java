package com.research.ems.service.impl;

import com.research.ems.entity.Employee;
import com.research.ems.entity.EmployeeProject;
import com.research.ems.entity.Project;

import com.research.ems.dto.employeeproject.EmployeeProjectRequestDto;
import com.research.ems.dto.employeeproject.EmployeeProjectResponseDto;
import com.research.ems.mapper.EmployeeProjectMapper;
import com.research.ems.repository.EmployeeProjectRepository;
import com.research.ems.repository.EmployeeRepository;
import com.research.ems.repository.ProjectRepository;
import com.research.ems.service.EmployeeProjectService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.time.LocalDate;

@Service
@Transactional
public class EmployeeProjectServiceImpl
        implements EmployeeProjectService {

    private final EmployeeProjectRepository repository;
    private final EmployeeRepository employeeRepository;
    private final ProjectRepository projectRepository;
    private final EmployeeProjectMapper mapper;

    public EmployeeProjectServiceImpl(
            EmployeeProjectRepository repository,
            EmployeeRepository employeeRepository,
            ProjectRepository projectRepository,
            EmployeeProjectMapper mapper) {

        this.repository = repository;
        this.employeeRepository = employeeRepository;
        this.projectRepository = projectRepository;
        this.mapper = mapper;
    }

    @Override
    public EmployeeProjectResponseDto assignEmployeeToProject(
            EmployeeProjectRequestDto dto) {

        if (repository.existsByEmployeeIdAndProjectId(
                dto.getEmployeeId(),
                dto.getProjectId())) {

            throw new IllegalArgumentException(
                    "Employee is already assigned to this project");
        }

        Employee employee = findEmployee(dto.getEmployeeId());
        Project project = findProject(dto.getProjectId());

        EmployeeProject assignment = mapper.toEntity(dto);

        assignment.setEmployee(employee);
        assignment.setProject(project);

        return mapper.toResponseDto(
                repository.save(assignment));
    }

    @Override
    @Transactional(readOnly = true)
    public EmployeeProjectResponseDto getEmployeeProjectById(
            Long id) {

        return mapper.toResponseDto(find(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<EmployeeProjectResponseDto>
    getAllEmployeeProjects() {

        return repository.findAll()
                .stream()
                .map(mapper::toResponseDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<EmployeeProjectResponseDto>
    getEmployeeProjects(Long employeeId) {

        findEmployee(employeeId);

        return repository.findByEmployeeId(employeeId)
                .stream()
                .map(mapper::toResponseDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<EmployeeProjectResponseDto> getActiveEmployeeProjects(
            Long employeeId) {
        findEmployee(employeeId);
        return repository.findByEmployeeIdAndActiveTrue(employeeId)
                .stream().map(mapper::toResponseDto).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<EmployeeProjectResponseDto> getProjectEmployees(
            Long projectId) {
        findProject(projectId);
        return repository.findByProjectId(projectId)
                .stream().map(mapper::toResponseDto).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<EmployeeProjectResponseDto> getActiveProjectEmployees(
            Long projectId) {
        findProject(projectId);
        return repository.findByProjectIdAndActiveTrue(projectId)
                .stream().map(mapper::toResponseDto).toList();
    }

    @Override
    public EmployeeProjectResponseDto updateEmployeeProject(
            Long id,
            EmployeeProjectRequestDto dto) {

        EmployeeProject assignment = find(id);

        Employee employee =
                findEmployee(dto.getEmployeeId());

        Project project =
                findProject(dto.getProjectId());

        mapper.updateEntity(assignment, dto);

        assignment.setEmployee(employee);
        assignment.setProject(project);

        return mapper.toResponseDto(
                repository.save(assignment));
    }

    @Override
    public EmployeeProjectResponseDto releaseEmployeeFromProject(
            Long id,
            LocalDate releasedDate) {
        EmployeeProject assignment = find(id);
        if (!Boolean.TRUE.equals(assignment.getActive())) {
            throw new IllegalArgumentException("Project assignment is already released");
        }
        assignment.setActive(false);
        assignment.setReleasedDate(releasedDate);
        return mapper.toResponseDto(repository.save(assignment));
    }

    @Override
    public void deleteEmployeeProject(Long id) {

        repository.delete(find(id));
    }

    private EmployeeProject find(Long id) {

        return repository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Employee project assignment not found"));
    }

    private Employee findEmployee(Long id) {

        return employeeRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Employee not found with id: " + id));
    }

    private Project findProject(Long id) {

        return projectRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Project not found with id: " + id));
    }
}