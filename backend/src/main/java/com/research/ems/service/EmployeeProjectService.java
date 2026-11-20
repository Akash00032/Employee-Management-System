package com.research.ems.service;

import com.research.ems.dto.employeeproject.*;
import java.time.LocalDate;
import java.util.List;

public interface EmployeeProjectService {

    EmployeeProjectResponseDto assignEmployeeToProject(
            EmployeeProjectRequestDto requestDto
    );

    EmployeeProjectResponseDto getEmployeeProjectById(Long id);

    List<EmployeeProjectResponseDto> getAllEmployeeProjects();

    List<EmployeeProjectResponseDto> getEmployeeProjects(
            Long employeeId
    );

    List<EmployeeProjectResponseDto> getActiveEmployeeProjects(
            Long employeeId
    );

    List<EmployeeProjectResponseDto> getProjectEmployees(
            Long projectId
    );

    List<EmployeeProjectResponseDto> getActiveProjectEmployees(
            Long projectId
    );

    EmployeeProjectResponseDto updateEmployeeProject(
            Long id,
            EmployeeProjectRequestDto requestDto
    );

    EmployeeProjectResponseDto releaseEmployeeFromProject(
            Long id,
            LocalDate releasedDate
    );

    void deleteEmployeeProject(Long id);
}
