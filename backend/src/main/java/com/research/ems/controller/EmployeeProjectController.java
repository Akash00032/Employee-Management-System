package com.research.ems.controller;

import com.research.ems.dto.employeeproject.EmployeeProjectRequestDto;
import com.research.ems.dto.employeeproject.EmployeeProjectResponseDto;
import com.research.ems.service.EmployeeProjectService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/employee-projects")
@RequiredArgsConstructor
public class EmployeeProjectController {

    private final EmployeeProjectService employeeProjectService;

    @PostMapping
    public ResponseEntity<EmployeeProjectResponseDto> assignEmployeeToProject(
            @Valid @RequestBody EmployeeProjectRequestDto requestDto) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(employeeProjectService.assignEmployeeToProject(requestDto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<EmployeeProjectResponseDto> getEmployeeProjectById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                employeeProjectService.getEmployeeProjectById(id)
        );
    }

    @GetMapping
    public ResponseEntity<List<EmployeeProjectResponseDto>> getAllEmployeeProjects() {

        return ResponseEntity.ok(
                employeeProjectService.getAllEmployeeProjects()
        );
    }

    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<List<EmployeeProjectResponseDto>> getEmployeeProjects(
            @PathVariable Long employeeId) {

        return ResponseEntity.ok(
                employeeProjectService.getEmployeeProjects(employeeId)
        );
    }

    @GetMapping("/project/{projectId}")
    public ResponseEntity<List<EmployeeProjectResponseDto>> getProjectEmployees(
            @PathVariable Long projectId) {

        return ResponseEntity.ok(
                employeeProjectService.getProjectEmployees(projectId)
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<EmployeeProjectResponseDto> updateEmployeeProject(
            @PathVariable Long id,
            @Valid @RequestBody EmployeeProjectRequestDto requestDto) {

        return ResponseEntity.ok(
                employeeProjectService.updateEmployeeProject(
                        id, requestDto
                )
        );
    }

    @PatchMapping("/{id}/release")
    public ResponseEntity<EmployeeProjectResponseDto> releaseEmployeeFromProject(
            @PathVariable Long id,
            @RequestParam java.time.LocalDate releasedDate) {

        return ResponseEntity.ok(
                employeeProjectService.releaseEmployeeFromProject(
                        id, releasedDate
                )
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEmployeeProject(
            @PathVariable Long id) {

        employeeProjectService.deleteEmployeeProject(id);
        return ResponseEntity.noContent().build();
    }
}