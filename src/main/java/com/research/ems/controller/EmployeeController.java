package com.research.ems.controller;

import com.research.ems.dto.employee.EmployeeRequestDto;
import com.research.ems.dto.employee.EmployeeResponseDto;
import com.research.ems.service.EmployeeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/employees")
@RequiredArgsConstructor
public class EmployeeController {

    private final EmployeeService employeeService;

    @GetMapping("/me")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR', 'MANAGER', 'EMPLOYEE')")
    public ResponseEntity<EmployeeResponseDto> getMyProfile(
            Authentication authentication) {

        return ResponseEntity.ok(
                employeeService.getMyProfile(authentication.getName())
        );
    }

    @GetMapping("/team")
    public ResponseEntity<List<EmployeeResponseDto>> getMyTeam(
            Authentication authentication,
            @RequestParam(required = false) String keyword) {
        return ResponseEntity.ok(
                employeeService.getMyTeam(authentication.getName(), keyword));
    }

    @GetMapping("/team/{employeeId}")
    public ResponseEntity<EmployeeResponseDto> getMyTeamMember(
            Authentication authentication,
            @PathVariable Long employeeId) {
        return ResponseEntity.ok(
                employeeService.getMyTeamMember(
                        authentication.getName(), employeeId));
    }

    @GetMapping("/directory")
    public ResponseEntity<Page<EmployeeResponseDto>> getEmployeeDirectory(
            @RequestParam(required = false, defaultValue = "") String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sort) {
        return ResponseEntity.ok(
                employeeService.getEmployeeDirectory(
                        keyword, page, size, sort));
    }

    @PostMapping
    public ResponseEntity<EmployeeResponseDto> createEmployee(
            @Valid @RequestBody EmployeeRequestDto requestDto) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(employeeService.createEmployee(requestDto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<EmployeeResponseDto> getEmployeeById(
            @PathVariable Long id) {

        return ResponseEntity.ok(employeeService.getEmployeeById(id));
    }

    @GetMapping
    public ResponseEntity<List<EmployeeResponseDto>> getAllEmployees() {

        return ResponseEntity.ok(employeeService.getAllEmployees());
    }

    @PutMapping("/{id}")
    public ResponseEntity<EmployeeResponseDto> updateEmployee(
            @PathVariable Long id,
            @Valid @RequestBody EmployeeRequestDto requestDto) {

        return ResponseEntity.ok(
                employeeService.updateEmployee(id, requestDto)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEmployee(
            @PathVariable Long id) {

        employeeService.deleteEmployee(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    public ResponseEntity<List<EmployeeResponseDto>> searchEmployees(
            @RequestParam String keyword) {

        return ResponseEntity.ok(
                employeeService.searchEmployees(keyword)
        );
    }

    @GetMapping("/designation")
    public ResponseEntity<Page<EmployeeResponseDto>> getEmployeesByDesignation(
            @RequestParam String designation,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        return ResponseEntity.ok(
                employeeService.getEmployeesByDesignation(
                        designation, page, size
                )
        );
    }
}