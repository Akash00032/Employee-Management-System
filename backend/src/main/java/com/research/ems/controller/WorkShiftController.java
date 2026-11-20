package com.research.ems.controller;

import com.research.ems.dto.workshift.WorkShiftRequestDto;
import com.research.ems.dto.workshift.WorkShiftResponseDto;
import com.research.ems.service.WorkShiftService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/work-shifts")
@RequiredArgsConstructor
public class WorkShiftController {

    private final WorkShiftService workShiftService;

    @PostMapping
    public ResponseEntity<WorkShiftResponseDto> createWorkShift(
            @Valid @RequestBody WorkShiftRequestDto requestDto) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(workShiftService.createWorkShift(requestDto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<WorkShiftResponseDto> getWorkShiftById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                workShiftService.getWorkShiftById(id)
        );
    }

    @GetMapping
    public ResponseEntity<List<WorkShiftResponseDto>> getAllWorkShifts() {

        return ResponseEntity.ok(
                workShiftService.getAllWorkShifts()
        );
    }

    @GetMapping("/active")
    public ResponseEntity<List<WorkShiftResponseDto>> getActiveWorkShifts() {

        return ResponseEntity.ok(
                workShiftService.getActiveWorkShifts()
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<WorkShiftResponseDto> updateWorkShift(
            @PathVariable Long id,
            @Valid @RequestBody WorkShiftRequestDto requestDto) {

        return ResponseEntity.ok(
                workShiftService.updateWorkShift(id, requestDto)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteWorkShift(
            @PathVariable Long id) {

        workShiftService.deleteWorkShift(id);
        return ResponseEntity.noContent().build();
    }
}