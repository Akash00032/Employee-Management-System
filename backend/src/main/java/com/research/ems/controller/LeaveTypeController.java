package com.research.ems.controller;

import com.research.ems.dto.leavetype.LeaveTypeRequestDto;
import com.research.ems.dto.leavetype.LeaveTypeResponseDto;
import com.research.ems.service.LeaveTypeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/leave-types")
@RequiredArgsConstructor
public class LeaveTypeController {

    private final LeaveTypeService leaveTypeService;

    @PostMapping
    public ResponseEntity<LeaveTypeResponseDto> createLeaveType(
            @Valid @RequestBody LeaveTypeRequestDto requestDto) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(leaveTypeService.createLeaveType(requestDto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<LeaveTypeResponseDto> getLeaveTypeById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                leaveTypeService.getLeaveTypeById(id)
        );
    }

    @GetMapping
    public ResponseEntity<List<LeaveTypeResponseDto>> getAllLeaveTypes() {

        return ResponseEntity.ok(
                leaveTypeService.getAllLeaveTypes()
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<LeaveTypeResponseDto> updateLeaveType(
            @PathVariable Long id,
            @Valid @RequestBody LeaveTypeRequestDto requestDto) {

        return ResponseEntity.ok(
                leaveTypeService.updateLeaveType(id, requestDto)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLeaveType(
            @PathVariable Long id) {

        leaveTypeService.deleteLeaveType(id);
        return ResponseEntity.noContent().build();
    }
}