package com.research.ems.repository;

import com.research.ems.entity.Attendance;

import com.research.ems.entity.enums.AttendanceStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface AttendanceRepository
        extends JpaRepository<Attendance, Long> {

    Optional<Attendance> findByEmployeeIdAndAttendanceDate(
            Long employeeId,
            LocalDate attendanceDate
    );

    List<Attendance> findByEmployeeId(
            Long employeeId
    );

    List<Attendance> findByEmployeeIdIn(List<Long> employeeIds);

    Page<Attendance> findByEmployeeId(
            Long employeeId,
            Pageable pageable
    );

    List<Attendance> findByEmployeeIdAndAttendanceDateBetween(
            Long employeeId,
            LocalDate startDate,
            LocalDate endDate
    );

    Page<Attendance> findByAttendanceDateBetween(
            LocalDate startDate,
            LocalDate endDate,
            Pageable pageable
    );

    List<Attendance> findByStatus(
            AttendanceStatus status
    );

    List<Attendance> findByAttendanceDate(LocalDate attendanceDate);

    boolean existsByEmployeeIdAndAttendanceDate(
            Long employeeId,
            LocalDate attendanceDate
    );

    long countByEmployeeIdAndStatus(
            Long employeeId,
            AttendanceStatus status
    );

    long countByAttendanceDateAndStatus(
            LocalDate attendanceDate,
            AttendanceStatus status
    );
}
