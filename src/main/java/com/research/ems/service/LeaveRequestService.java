package com.research.ems.service;

import com.research.ems.dto.leaverequest.*;

import com.research.ems.entity.enums.LeaveStatus;
import java.util.List;

public interface LeaveRequestService {

    LeaveRequestResponseDto createLeaveRequest(
            LeaveRequestDto requestDto
    );

    LeaveRequestResponseDto createMyLeaveRequest(
            String username,
            MyLeaveRequestDto requestDto
    );

    LeaveRequestResponseDto getLeaveRequestById(Long id);

    List<LeaveRequestResponseDto> getAllLeaveRequests();

    List<LeaveRequestResponseDto> getEmployeeLeaveRequests(
            Long employeeId
    );

    List<LeaveRequestResponseDto> getLeaveRequestsByStatus(
            LeaveStatus status
    );

    LeaveRequestResponseDto updateLeaveRequest(
            Long id,
            LeaveRequestDto requestDto
    );

    LeaveRequestResponseDto approveLeaveRequest(
            Long leaveRequestId,
            String approverUsername
    );

    LeaveRequestResponseDto rejectLeaveRequest(
            Long leaveRequestId,
            String approverUsername,
            String rejectionReason
    );

    void deleteLeaveRequest(Long id);

    List<LeaveRequestResponseDto> getMyLeaveRequests(String name);

    List<LeaveRequestResponseDto> getTeamLeaveRequests(
            String managerUsername,
            LeaveStatus status
    );

    void cancelMyLeaveRequest(Long id, String username);
}
