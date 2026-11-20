package com.research.ems.controller;

import com.research.ems.dto.leaverequest.LeaveRequestDto;
import com.research.ems.dto.leaverequest.LeaveRequestResponseDto;
import com.research.ems.dto.leaverequest.MyLeaveRequestDto;
import com.research.ems.entity.enums.LeaveStatus;
import com.research.ems.service.LeaveRequestService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Validated
@RequestMapping("/api/v1/leave-requests")
@RequiredArgsConstructor
public class LeaveRequestController {

    private final LeaveRequestService leaveRequestService;

    @GetMapping("/me")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR', 'MANAGER', 'EMPLOYEE')")
    public ResponseEntity<List<LeaveRequestResponseDto>> getMyLeaveRequests(
            Authentication authentication) {

        return ResponseEntity.ok(
                leaveRequestService.getMyLeaveRequests(
                        authentication.getName()
                )
        );
    }

    @GetMapping("/team")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<List<LeaveRequestResponseDto>> getTeamLeaveRequests(
            Authentication authentication,
            @RequestParam(required = false) LeaveStatus status) {
        return ResponseEntity.ok(
                leaveRequestService.getTeamLeaveRequests(
                        authentication.getName(), status));
    }

    @PostMapping("/me")
    public ResponseEntity<LeaveRequestResponseDto> createMyLeaveRequest(
            Authentication authentication,
            @Valid @RequestBody MyLeaveRequestDto requestDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
                leaveRequestService.createMyLeaveRequest(
                        authentication.getName(), requestDto));
    }

    @DeleteMapping("/me/{id}")
    public ResponseEntity<Void> cancelMyLeaveRequest(
            Authentication authentication,
            @PathVariable Long id) {
        leaveRequestService.cancelMyLeaveRequest(
                id, authentication.getName());
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/team/{id}/approve")
    public ResponseEntity<LeaveRequestResponseDto> approveTeamLeaveRequest(
            Authentication authentication,
            @PathVariable Long id) {
        return ResponseEntity.ok(leaveRequestService.approveLeaveRequest(
                id, authentication.getName()));
    }

    @PatchMapping("/team/{id}/reject")
    public ResponseEntity<LeaveRequestResponseDto> rejectTeamLeaveRequest(
            Authentication authentication,
            @PathVariable Long id,
            @RequestParam @NotBlank @Size(max = 500) String reason) {
        return ResponseEntity.ok(leaveRequestService.rejectLeaveRequest(
                id, authentication.getName(), reason));
    }

    @PostMapping
    public ResponseEntity<LeaveRequestResponseDto> createLeaveRequest(
            @Valid @RequestBody LeaveRequestDto requestDto) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(leaveRequestService.createLeaveRequest(requestDto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<LeaveRequestResponseDto> getLeaveRequestById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                leaveRequestService.getLeaveRequestById(id)
        );
    }

    @GetMapping
    public ResponseEntity<List<LeaveRequestResponseDto>> getAllLeaveRequests() {

        return ResponseEntity.ok(
                leaveRequestService.getAllLeaveRequests()
        );
    }

    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<List<LeaveRequestResponseDto>> getEmployeeLeaveRequests(
            @PathVariable Long employeeId) {

        return ResponseEntity.ok(
                leaveRequestService.getEmployeeLeaveRequests(employeeId)
        );
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<LeaveRequestResponseDto>> getLeaveRequestsByStatus(
            @PathVariable LeaveStatus status) {

        return ResponseEntity.ok(
                leaveRequestService.getLeaveRequestsByStatus(status)
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<LeaveRequestResponseDto> updateLeaveRequest(
            @PathVariable Long id,
            @Valid @RequestBody LeaveRequestDto requestDto) {

        return ResponseEntity.ok(
                leaveRequestService.updateLeaveRequest(id, requestDto)
        );
    }

    @PatchMapping("/{id}/approve")
    public ResponseEntity<LeaveRequestResponseDto> approveLeaveRequest(
            Authentication authentication,
            @PathVariable Long id) {

        return ResponseEntity.ok(
                leaveRequestService.approveLeaveRequest(
                        id, authentication.getName())
        );
    }

    @PatchMapping("/{id}/reject")
    public ResponseEntity<LeaveRequestResponseDto> rejectLeaveRequest(
            Authentication authentication,
            @PathVariable Long id,
            @RequestParam @NotBlank @Size(max = 500) String reason) {

        return ResponseEntity.ok(
                leaveRequestService.rejectLeaveRequest(
                        id, authentication.getName(), reason
                )
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLeaveRequest(
            @PathVariable Long id) {

        leaveRequestService.deleteLeaveRequest(id);
        return ResponseEntity.noContent().build();
    }
}