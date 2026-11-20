package com.research.ems.mapper;

import com.research.ems.entity.EmployeeProject;

import com.research.ems.dto.employeeproject.EmployeeProjectRequestDto;
import com.research.ems.dto.employeeproject.EmployeeProjectResponseDto;
import org.springframework.stereotype.Component;

@Component
public class EmployeeProjectMapper {

    public EmployeeProject toEntity(
            EmployeeProjectRequestDto dto) {

        if (dto == null) {
            return null;
        }

        EmployeeProject employeeProject =
                new EmployeeProject();

        employeeProject.setProjectRole(dto.getProjectRole());
        employeeProject.setAllocationPercentage(
                dto.getAllocationPercentage()
        );
        employeeProject.setAssignedDate(
                dto.getAssignedDate()
        );
        employeeProject.setReleasedDate(
                dto.getReleasedDate()
        );

        if (dto.getActive() != null) {
            employeeProject.setActive(dto.getActive());
        }

        return employeeProject;
    }

    public void updateEntity(
            EmployeeProject employeeProject,
            EmployeeProjectRequestDto dto) {

        if (employeeProject == null || dto == null) {
            return;
        }

        employeeProject.setProjectRole(dto.getProjectRole());
        employeeProject.setAllocationPercentage(
                dto.getAllocationPercentage()
        );
        employeeProject.setAssignedDate(
                dto.getAssignedDate()
        );
        employeeProject.setReleasedDate(
                dto.getReleasedDate()
        );

        if (dto.getActive() != null) {
            employeeProject.setActive(dto.getActive());
        }
    }

    public EmployeeProjectResponseDto toResponseDto(
            EmployeeProject entity) {

        if (entity == null) {
            return null;
        }

        Long employeeId = null;
        String employeeCode = null;
        String employeeName = null;

        if (entity.getEmployee() != null) {
            employeeId = entity.getEmployee().getId();
            employeeCode = entity.getEmployee().getEmployeeCode();

            employeeName = buildFullName(
                    entity.getEmployee().getFirstName(),
                    entity.getEmployee().getLastName()
            );
        }

        Long projectId = null;
        String projectCode = null;
        String projectName = null;

        if (entity.getProject() != null) {
            projectId = entity.getProject().getId();
            projectCode = entity.getProject().getCode();
            projectName = entity.getProject().getName();
        }

        return new EmployeeProjectResponseDto(
                entity.getId(),
                employeeId,
                employeeCode,
                employeeName,
                projectId,
                projectCode,
                projectName,
                entity.getProjectRole(),
                entity.getAllocationPercentage(),
                entity.getAssignedDate(),
                entity.getReleasedDate(),
                entity.getActive()
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