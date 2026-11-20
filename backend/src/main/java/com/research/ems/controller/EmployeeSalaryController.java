package com.research.ems.controller;

import com.research.ems.dto.employeesalary.EmployeeSalaryRequestDto;
import com.research.ems.dto.employeesalary.EmployeeSalaryResponseDto;
import com.research.ems.service.EmployeeSalaryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/employee-salaries")
@RequiredArgsConstructor
public class EmployeeSalaryController {

    private final EmployeeSalaryService employeeSalaryService;

    @PostMapping
    public ResponseEntity<EmployeeSalaryResponseDto> createEmployeeSalary(
            @Valid @RequestBody EmployeeSalaryRequestDto requestDto) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(employeeSalaryService.createEmployeeSalary(requestDto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<EmployeeSalaryResponseDto> getEmployeeSalaryById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                employeeSalaryService.getEmployeeSalaryById(id)
        );
    }

    @GetMapping
    public ResponseEntity<List<EmployeeSalaryResponseDto>> getAllEmployeeSalaries() {

        return ResponseEntity.ok(
                employeeSalaryService.getAllEmployeeSalaries()
        );
    }

    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<List<EmployeeSalaryResponseDto>> getEmployeeSalaries(
            @PathVariable Long employeeId) {

        return ResponseEntity.ok(
                employeeSalaryService.getEmployeeSalaries(employeeId)
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<EmployeeSalaryResponseDto> updateEmployeeSalary(
            @PathVariable Long id,
            @Valid @RequestBody EmployeeSalaryRequestDto requestDto) {

        return ResponseEntity.ok(
                employeeSalaryService.updateEmployeeSalary(id, requestDto)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEmployeeSalary(
            @PathVariable Long id) {

        employeeSalaryService.deleteEmployeeSalary(id);
        return ResponseEntity.noContent().build();
    }
}