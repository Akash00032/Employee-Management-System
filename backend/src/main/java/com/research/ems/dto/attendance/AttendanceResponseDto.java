package com.research.ems.dto.attendance;


import com.research.ems.entity.enums.AttendanceStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AttendanceResponseDto {

    private Long id;

    private Long employeeId;

    private String employeeCode;

    private String employeeName;

    private LocalDate attendanceDate;

    private LocalDateTime checkIn;

    private LocalDateTime checkOut;

    private Double workingHours;

    private AttendanceStatus status;

    private String remarks;

}