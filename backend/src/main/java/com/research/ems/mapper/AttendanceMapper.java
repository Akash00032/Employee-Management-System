package com.research.ems.mapper;

import com.research.ems.entity.Attendance;

import com.research.ems.dto.attendance.AttendanceRequestDto;
import com.research.ems.dto.attendance.AttendanceResponseDto;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;

@Component
public class AttendanceMapper {

    public Attendance toEntity(AttendanceRequestDto dto) {

        if (dto == null) {
            return null;
        }

        Attendance attendance = new Attendance();

        attendance.setAttendanceDate(dto.getAttendanceDate());
        attendance.setCheckIn(dto.getCheckIn());
        attendance.setCheckOut(dto.getCheckOut());
        attendance.setStatus(dto.getStatus());
        attendance.setRemarks(dto.getRemarks());

        calculateWorkingHours(attendance);

        return attendance;
    }

    public void updateEntity(
            Attendance attendance,
            AttendanceRequestDto dto) {

        if (attendance == null || dto == null) {
            return;
        }

        attendance.setAttendanceDate(dto.getAttendanceDate());
        attendance.setCheckIn(dto.getCheckIn());
        attendance.setCheckOut(dto.getCheckOut());
        attendance.setStatus(dto.getStatus());
        attendance.setRemarks(dto.getRemarks());

        calculateWorkingHours(attendance);
    }

    public AttendanceResponseDto toResponseDto(
            Attendance attendance) {

        if (attendance == null) {
            return null;
        }

        Long employeeId = null;
        String employeeCode = null;
        String employeeName = null;

        if (attendance.getEmployee() != null) {

            employeeId = attendance.getEmployee().getId();
            employeeCode = attendance.getEmployee().getEmployeeCode();

            employeeName = buildFullName(
                    attendance.getEmployee().getFirstName(),
                    attendance.getEmployee().getLastName()
            );
        }

        return new AttendanceResponseDto(
                attendance.getId(),
                employeeId,
                employeeCode,
                employeeName,
                attendance.getAttendanceDate(),
                attendance.getCheckIn(),
                attendance.getCheckOut(),
                attendance.getWorkingHours(),
                attendance.getStatus(),
                attendance.getRemarks()
        );
    }

    public void calculateWorkingHours(
            Attendance attendance) {

        if (attendance == null) {
            return;
        }

        LocalDateTime checkIn = attendance.getCheckIn();
        LocalDateTime checkOut = attendance.getCheckOut();

        if (checkIn == null || checkOut == null) {
            attendance.setWorkingHours(null);
            return;
        }

        if (checkOut.isBefore(checkIn)) {
            throw new IllegalArgumentException(
                    "Check-out time cannot be before check-in time"
            );
        }

        Duration duration =
                Duration.between(checkIn, checkOut);

        double hours =
                duration.toMinutes() / 60.0;

        attendance.setWorkingHours(
                Math.round(hours * 100.0) / 100.0
        );
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