package com.research.ems.dto.leaverequest;

import com.research.ems.entity.enums.LeaveStatus;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LeaveRequestResponseDto {

    private Long id;

    private Long employeeId;

    private String employeeCode;

    private String employeeName;

    private Long leaveTypeId;

    private String leaveTypeCode;

    private String leaveTypeName;

    private LocalDate startDate;

    private LocalDate endDate;

    private Integer totalDays;

    private String reason;

    private LeaveStatus status;

    private Long approvedById;

    private String approvedByName;

    private LocalDateTime appliedAt;

    private LocalDateTime approvedAt;

    private String rejectionReason;
}