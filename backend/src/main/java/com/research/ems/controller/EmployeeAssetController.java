package com.research.ems.controller;

import com.research.ems.dto.employeeasset.EmployeeAssetRequestDto;
import com.research.ems.dto.employeeasset.EmployeeAssetResponseDto;
import com.research.ems.entity.enums.AssetStatus;
import com.research.ems.service.EmployeeAssetService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/employee-assets")
@RequiredArgsConstructor
public class EmployeeAssetController {

    private final EmployeeAssetService employeeAssetService;

    @PostMapping
    public ResponseEntity<EmployeeAssetResponseDto> assignAsset(
            @Valid @RequestBody EmployeeAssetRequestDto requestDto) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(employeeAssetService.assignAsset(requestDto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<EmployeeAssetResponseDto> getAssetById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                employeeAssetService.getAssetById(id)
        );
    }

    @GetMapping
    public ResponseEntity<List<EmployeeAssetResponseDto>> getAllAssets() {

        return ResponseEntity.ok(
                employeeAssetService.getAllAssets()
        );
    }

    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<List<EmployeeAssetResponseDto>> getEmployeeAssets(
            @PathVariable Long employeeId) {

        return ResponseEntity.ok(
                employeeAssetService.getEmployeeAssets(employeeId)
        );
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<EmployeeAssetResponseDto>> getAssetsByStatus(
            @PathVariable AssetStatus status) {

        return ResponseEntity.ok(
                employeeAssetService.getAssetsByStatus(status)
        );
    }

    @PatchMapping("/{id}/return")
    public ResponseEntity<EmployeeAssetResponseDto> returnAsset(
            @PathVariable Long id,
            @RequestParam java.time.LocalDate returnDate) {

        return ResponseEntity.ok(
                employeeAssetService.returnAsset(id, returnDate)
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<EmployeeAssetResponseDto> updateAsset(
            @PathVariable Long id,
            @Valid @RequestBody EmployeeAssetRequestDto requestDto) {

        return ResponseEntity.ok(
                employeeAssetService.updateAsset(id, requestDto)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAsset(
            @PathVariable Long id) {

        employeeAssetService.deleteAsset(id);
        return ResponseEntity.noContent().build();
    }
}