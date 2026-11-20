package com.research.ems.service.impl;

import com.research.ems.entity.Attendance;
import com.research.ems.entity.Employee;
import com.research.ems.entity.UserAccount;

import com.research.ems.dto.attendance.AttendanceRequestDto;
import com.research.ems.dto.attendance.AttendanceResponseDto;
import com.research.ems.entity.enums.AttendanceStatus;
import com.research.ems.entity.enums.Role;
import com.research.ems.exception.employee.EmployeeNotFoundException;
import com.research.ems.exception.useraccount.UserAccountNotFoundException;
import com.research.ems.mapper.AttendanceMapper;
import com.research.ems.repository.AttendanceRepository;
import com.research.ems.repository.EmployeeRepository;
import com.research.ems.repository.UserAccountRepository;
import com.research.ems.service.AttendanceService;
import org.springframework.stereotype.Service;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
public class AttendanceServiceImpl implements AttendanceService {

    private final AttendanceRepository attendanceRepository;
    private final EmployeeRepository employeeRepository;
    private final UserAccountRepository userAccountRepository;
    private final AttendanceMapper attendanceMapper;

    public AttendanceServiceImpl(
            AttendanceRepository attendanceRepository,
            EmployeeRepository employeeRepository,
            UserAccountRepository userAccountRepository,
            AttendanceMapper attendanceMapper) {

        this.attendanceRepository = attendanceRepository;
        this.employeeRepository = employeeRepository;
        this.userAccountRepository = userAccountRepository;
        this.attendanceMapper = attendanceMapper;
    }

    @Override
    public AttendanceResponseDto markAttendance(
            AttendanceRequestDto dto) {

        Employee employee = findEmployee(dto.getEmployeeId());

        if (attendanceRepository
                .existsByEmployeeIdAndAttendanceDate(
                        dto.getEmployeeId(),
                        dto.getAttendanceDate())) {

            throw new IllegalArgumentException(
                    "Attendance already exists for this employee and date");
        }

        Attendance attendance =
                attendanceMapper.toEntity(dto);

        attendance.setEmployee(employee);

        if (attendance.getStatus() == null) {
            attendance.setStatus(AttendanceStatus.PRESENT);
        }

        attendanceMapper.calculateWorkingHours(attendance);

        return attendanceMapper.toResponseDto(
                attendanceRepository.save(attendance));
    }

    @Override
    @Transactional(readOnly = true)
    public AttendanceResponseDto getAttendanceById(Long id) {

        return attendanceMapper.toResponseDto(
                findAttendance(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<AttendanceResponseDto> getAllAttendance() {

        return attendanceRepository.findAll()
                .stream()
                .map(attendanceMapper::toResponseDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<AttendanceResponseDto> getEmployeeAttendance(
            Long employeeId) {

        findEmployee(employeeId);

        return attendanceRepository
                .findByEmployeeId(employeeId)
                .stream()
                .map(attendanceMapper::toResponseDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<AttendanceResponseDto> getEmployeeAttendanceBetweenDates(
            Long employeeId,
            LocalDate startDate,
            LocalDate endDate) {

        findEmployee(employeeId);

        return attendanceRepository
                .findByEmployeeIdAndAttendanceDateBetween(
                        employeeId,
                        startDate,
                        endDate)
                .stream()
                .map(attendanceMapper::toResponseDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<AttendanceResponseDto> getAttendanceByStatus(
            AttendanceStatus status) {

        return attendanceRepository
                .findByStatus(status)
                .stream()
                .map(attendanceMapper::toResponseDto)
                .toList();
    }

    @Override
    public AttendanceResponseDto updateAttendance(
            Long id,
            AttendanceRequestDto dto) {

        Attendance attendance = findAttendance(id);

        if (attendanceRepository
                .existsByEmployeeIdAndAttendanceDate(
                        dto.getEmployeeId(),
                        dto.getAttendanceDate())
                &&
                !(attendance.getEmployee().getId()
                        .equals(dto.getEmployeeId())
                        &&
                        attendance.getAttendanceDate()
                                .equals(dto.getAttendanceDate()))) {

            throw new IllegalArgumentException(
                    "Attendance already exists for this employee and date");
        }

        Employee employee = findEmployee(dto.getEmployeeId());

        attendanceMapper.updateEntity(attendance, dto);
        attendance.setEmployee(employee);

        if (attendance.getStatus() == null) {
            attendance.setStatus(AttendanceStatus.PRESENT);
        }

        return attendanceMapper.toResponseDto(
                attendanceRepository.save(attendance));
    }

    @Override
    public void deleteAttendance(Long id) {

        attendanceRepository.delete(findAttendance(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<AttendanceResponseDto> getMyAttendance(String username) {
        UserAccount userAccount = userAccountRepository
                .findByUsernameIgnoreCase(username)
                .orElseThrow(() -> new UserAccountNotFoundException(
                        "User account not found for username: " + username));

        Employee employee = userAccount.getEmployee();
        if (employee == null) {
            throw new EmployeeNotFoundException(
                    "No employee profile linked to username: " + username);
        }

        return attendanceRepository.findByEmployeeId(employee.getId())
                .stream()
                .map(attendanceMapper::toResponseDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<AttendanceResponseDto> getTeamAttendance(String username) {
        UserAccount account = userAccountRepository
                .findByUsernameIgnoreCase(username)
                .orElseThrow(() -> new UserAccountNotFoundException(
                        "User account not found"));
        if (account.getRole() != Role.MANAGER) {
            throw new AccessDeniedException("Manager role is required");
        }

        List<Long> employeeIds = employeeRepository
                .findByManagerId(account.getEmployee().getId())
                .stream()
                .map(Employee::getId)
                .toList();
        if (employeeIds.isEmpty()) {
            return List.of();
        }

        return attendanceRepository.findByEmployeeIdIn(employeeIds)
                .stream()
                .map(attendanceMapper::toResponseDto)
                .toList();
    }

    private Attendance findAttendance(Long id) {

        return attendanceRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Attendance not found with id: " + id));
    }

    private Employee findEmployee(Long id) {

        return employeeRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Employee not found with id: " + id));
    }
}