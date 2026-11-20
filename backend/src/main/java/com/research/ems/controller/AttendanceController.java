package com.research.ems.controller;

import com.research.ems.dto.attendance.AttendanceRequestDto;
import com.research.ems.dto.attendance.AttendanceResponseDto;
import com.research.ems.entity.enums.AttendanceStatus;
import com.research.ems.service.AttendanceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/attendance")
@RequiredArgsConstructor
public class AttendanceController {

    private final AttendanceService attendanceService;

    @GetMapping("/me")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR', 'MANAGER', 'EMPLOYEE')")
    public ResponseEntity<List<AttendanceResponseDto>> getMyAttendance(
            Authentication authentication) {

        return ResponseEntity.ok(
                attendanceService.getMyAttendance(authentication.getName())
        );
    }

    @GetMapping("/team")
    public ResponseEntity<List<AttendanceResponseDto>> getTeamAttendance(
            Authentication authentication) {
        return ResponseEntity.ok(
                attendanceService.getTeamAttendance(authentication.getName()));
    }

    @PostMapping
    public ResponseEntity<AttendanceResponseDto> markAttendance(
            @Valid @RequestBody AttendanceRequestDto requestDto) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(attendanceService.markAttendance(requestDto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AttendanceResponseDto> getAttendanceById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                attendanceService.getAttendanceById(id)
        );
    }

    @GetMapping
    public ResponseEntity<List<AttendanceResponseDto>> getAllAttendance() {

        return ResponseEntity.ok(
                attendanceService.getAllAttendance()
        );
    }

    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<List<AttendanceResponseDto>> getEmployeeAttendance(
            @PathVariable Long employeeId) {

        return ResponseEntity.ok(
                attendanceService.getEmployeeAttendance(employeeId)
        );
    }

    @GetMapping("/employee/{employeeId}/between")
    public ResponseEntity<List<AttendanceResponseDto>>
    getEmployeeAttendanceBetweenDates(
            @PathVariable Long employeeId,
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate) {

        return ResponseEntity.ok(
                attendanceService.getEmployeeAttendanceBetweenDates(
                        employeeId, startDate, endDate
                )
        );
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<AttendanceResponseDto>> getAttendanceByStatus(
            @PathVariable AttendanceStatus status) {

        return ResponseEntity.ok(
                attendanceService.getAttendanceByStatus(status)
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<AttendanceResponseDto> updateAttendance(
            @PathVariable Long id,
            @Valid @RequestBody AttendanceRequestDto requestDto) {

        return ResponseEntity.ok(
                attendanceService.updateAttendance(id, requestDto)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAttendance(
            @PathVariable Long id) {

        attendanceService.deleteAttendance(id);
        return ResponseEntity.noContent().build();
    }
}