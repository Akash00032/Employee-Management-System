package com.research.ems.controller;

import com.research.ems.dto.salarycomponent.SalaryComponentRequestDto;
import com.research.ems.dto.salarycomponent.SalaryComponentResponseDto;
import com.research.ems.entity.enums.SalaryComponentType;
import com.research.ems.service.SalaryComponentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/salary-components")
@RequiredArgsConstructor
public class SalaryComponentController {

    private final SalaryComponentService salaryComponentService;

    @PostMapping
    public ResponseEntity<SalaryComponentResponseDto> createSalaryComponent(
            @Valid @RequestBody SalaryComponentRequestDto requestDto) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(salaryComponentService.createSalaryComponent(requestDto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<SalaryComponentResponseDto> getSalaryComponentById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                salaryComponentService.getSalaryComponentById(id)
        );
    }

    @GetMapping
    public ResponseEntity<List<SalaryComponentResponseDto>> getAllSalaryComponents() {

        return ResponseEntity.ok(
                salaryComponentService.getAllSalaryComponents()
        );
    }

    @GetMapping("/type/{type}")
    public ResponseEntity<List<SalaryComponentResponseDto>> getByType(
            @PathVariable SalaryComponentType type) {

        return ResponseEntity.ok(
                salaryComponentService.getSalaryComponentsByType(type)
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<SalaryComponentResponseDto> updateSalaryComponent(
            @PathVariable Long id,
            @Valid @RequestBody SalaryComponentRequestDto requestDto) {

        return ResponseEntity.ok(
                salaryComponentService.updateSalaryComponent(id, requestDto)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSalaryComponent(
            @PathVariable Long id) {

        salaryComponentService.deleteSalaryComponent(id);
        return ResponseEntity.noContent().build();
    }
}