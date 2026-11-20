package com.research.ems.controller;

import com.research.ems.dto.project.ProjectRequestDto;
import com.research.ems.dto.project.ProjectResponseDto;
import com.research.ems.entity.enums.ProjectStatus;
import com.research.ems.service.ProjectService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/projects")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;

    @PostMapping
    public ResponseEntity<ProjectResponseDto> createProject(
            @Valid @RequestBody ProjectRequestDto requestDto) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(projectService.createProject(requestDto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProjectResponseDto> getProjectById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                projectService.getProjectById(id)
        );
    }

    @GetMapping
    public ResponseEntity<Page<ProjectResponseDto>> getAllProjects(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        return ResponseEntity.ok(
                projectService.getAllProjects(page, size)
        );
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<Page<ProjectResponseDto>> getProjectsByStatus(
            @PathVariable ProjectStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        return ResponseEntity.ok(
                projectService.getProjectsByStatus(status, page, size)
        );
    }

    @GetMapping("/manager/{managerId}")
    public ResponseEntity<Page<ProjectResponseDto>> getProjectsByManager(
            @PathVariable Long managerId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        return ResponseEntity.ok(
                projectService.getProjectsByManager(managerId, page, size)
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProjectResponseDto> updateProject(
            @PathVariable Long id,
            @Valid @RequestBody ProjectRequestDto requestDto) {

        return ResponseEntity.ok(
                projectService.updateProject(id, requestDto)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProject(
            @PathVariable Long id) {

        projectService.deleteProject(id);
        return ResponseEntity.noContent().build();
    }
}