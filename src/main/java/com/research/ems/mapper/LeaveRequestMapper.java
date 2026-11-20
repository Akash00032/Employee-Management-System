package com.research.ems.mapper;

import com.research.ems.entity.LeaveRequest;

import com.research.ems.dto.leaverequest.LeaveRequestDto;
import com.research.ems.dto.leaverequest.LeaveRequestResponseDto;
import org.springframework.stereotype.Component;

import java.time.temporal.ChronoUnit;

@Component
public class LeaveRequestMapper {

    public LeaveRequest toEntity(LeaveRequestDto dto) {

        if (dto == null) {
            return null;
        }

        LeaveRequest request = new LeaveRequest();

        request.setStartDate(dto.getStartDate());
        request.setEndDate(dto.getEndDate());
        request.setReason(dto.getReason());

        calculateTotalDays(request);

        return request;
    }

    public void updateEntity(
            LeaveRequest request,
            LeaveRequestDto dto) {

        if (request == null || dto == null) {
            return;
        }

        request.setStartDate(dto.getStartDate());
        request.setEndDate(dto.getEndDate());
        request.setReason(dto.getReason());

        calculateTotalDays(request);
    }

    public LeaveRequestResponseDto toResponseDto(
            LeaveRequest request) {

        if (request == null) {
            return null;
        }

        Long employeeId = null;
        String employeeCode = null;
        String employeeName = null;

        if (request.getEmployee() != null) {
            employeeId = request.getEmployee().getId();
            employeeCode = request.getEmployee().getEmployeeCode();

            employeeName = buildFullName(
                    request.getEmployee().getFirstName(),
                    request.getEmployee().getLastName()
            );
        }

        Long leaveTypeId = null;
        String leaveTypeCode = null;
        String leaveTypeName = null;

        if (request.getLeaveType() != null) {
            leaveTypeId = request.getLeaveType().getId();
            leaveTypeCode = request.getLeaveType().getCode();
            leaveTypeName = request.getLeaveType().getName();
        }

        Long approvedById = null;
        String approvedByName = null;

        if (request.getApprovedBy() != null) {
            approvedById = request.getApprovedBy().getId();

            approvedByName = buildFullName(
                    request.getApprovedBy().getFirstName(),
                    request.getApprovedBy().getLastName()
            );
        }

        return new LeaveRequestResponseDto(
                request.getId(),
                employeeId,
                employeeCode,
                employeeName,
                leaveTypeId,
                leaveTypeCode,
                leaveTypeName,
                request.getStartDate(),
                request.getEndDate(),
                request.getTotalDays(),
                request.getReason(),
                request.getStatus(),
                approvedById,
                approvedByName,
                request.getAppliedAt(),
                request.getApprovedAt(),
                request.getRejectionReason()
        );
    }

    public void calculateTotalDays(
            LeaveRequest request) {

        if (request.getStartDate() == null ||
                request.getEndDate() == null) {
            return;
        }

        if (request.getEndDate()
                .isBefore(request.getStartDate())) {

            throw new IllegalArgumentException(
                    "Leave end date cannot be before start date"
            );
        }

        long days = ChronoUnit.DAYS.between(
                request.getStartDate(),
                request.getEndDate()
        ) + 1;

        request.setTotalDays((int) days);
    }

    private String buildFullName(
            String firstName,
            String lastName) {

        if (firstName == null) {
            return lastName;
        }

        if (lastName == null) {
            return firstName;
        }

        return firstName + " " + lastName;
    }
}