package com.research.ems.service;

import com.research.ems.dto.attendance.*;
import com.research.ems.entity.enums.AttendanceStatus;

import java.time.LocalDate;
import java.util.List;

public interface AttendanceService {

    AttendanceResponseDto markAttendance(
            AttendanceRequestDto requestDto
    );

    AttendanceResponseDto getAttendanceById(Long id);

    List<AttendanceResponseDto> getAllAttendance();

    List<AttendanceResponseDto> getEmployeeAttendance(
            Long employeeId
    );

    List<AttendanceResponseDto> getEmployeeAttendanceBetweenDates(
            Long employeeId,
            LocalDate startDate,
            LocalDate endDate
    );

    List<AttendanceResponseDto> getAttendanceByStatus(
            AttendanceStatus status
    );

    AttendanceResponseDto updateAttendance(
            Long id,
            AttendanceRequestDto requestDto
    );

    void deleteAttendance(Long id);

    List<AttendanceResponseDto> getMyAttendance(String name);

    List<AttendanceResponseDto> getTeamAttendance(String username);
}
