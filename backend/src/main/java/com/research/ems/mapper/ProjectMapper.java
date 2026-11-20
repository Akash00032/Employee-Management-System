package com.research.ems.mapper;

import com.research.ems.entity.Project;

import com.research.ems.dto.project.ProjectRequestDto;
import com.research.ems.dto.project.ProjectResponseDto;
import org.springframework.stereotype.Component;

@Component
public class ProjectMapper {

    public Project toEntity(ProjectRequestDto dto) {

        if (dto == null) {
            return null;
        }

        Project project = new Project();

        project.setCode(dto.getCode());
        project.setName(dto.getName());
        project.setDescription(dto.getDescription());
        project.setStartDate(dto.getStartDate());
        project.setEndDate(dto.getEndDate());
        project.setStatus(dto.getStatus());

        return project;
    }

    public void updateEntity(
            Project project,
            ProjectRequestDto dto) {

        if (project == null || dto == null) {
            return;
        }

        project.setCode(dto.getCode());
        project.setName(dto.getName());
        project.setDescription(dto.getDescription());
        project.setStartDate(dto.getStartDate());
        project.setEndDate(dto.getEndDate());
        project.setStatus(dto.getStatus());
    }

    public ProjectResponseDto toResponseDto(
            Project project) {

        if (project == null) {
            return null;
        }

        Long managerId = null;
        String managerName = null;

        if (project.getProjectManager() != null) {
            managerId = project.getProjectManager().getId();

            managerName = buildFullName(
                    project.getProjectManager().getFirstName(),
                    project.getProjectManager().getLastName()
            );
        }

        long employeeCount = project.getEmployeeProjects() == null
                ? 0
                : project.getEmployeeProjects().size();

        return new ProjectResponseDto(
                project.getId(),
                project.getCode(),
                project.getName(),
                project.getDescription(),
                project.getStartDate(),
                project.getEndDate(),
                project.getStatus(),
                managerId,
                managerName,
                employeeCount
        );
    }

    private String buildFullName(
            String firstName,
            String lastName) {

        if (firstName == null) {
            return lastName;
        }

        if (lastName == null) {
            return firstName;
        }

        return firstName + " " + lastName;
    }
}