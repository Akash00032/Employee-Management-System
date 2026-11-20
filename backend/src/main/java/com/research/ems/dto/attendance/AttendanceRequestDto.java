package com.research.ems.dto.attendance;

import com.research.ems.entity.Attendance;
import com.research.ems.entity.Employee;


import com.research.ems.entity.enums.AttendanceStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class AttendanceRequestDto {

    @NotNull(message = "Employee ID is required")
    private Long employeeId;

    @NotNull(message = "Attendance date is required")
    private LocalDate attendanceDate;

    private LocalDateTime checkIn;

    private LocalDateTime checkOut;

    private AttendanceStatus status;

    @Size(
            max = 500,
            message = "Remarks must not exceed 500 characters"
    )
    private String remarks;


}