package com.research.ems.service.impl;

import com.research.ems.entity.Attendance;
import com.research.ems.entity.Employee;

import com.research.ems.dto.dashboard.*;
import com.research.ems.entity.enums.AttendanceStatus;
import com.research.ems.entity.enums.EmploymentStatus;
import com.research.ems.entity.enums.LeaveStatus;
import com.research.ems.entity.enums.ProjectStatus;
import com.research.ems.repository.EmployeeRepository;
import com.research.ems.repository.DepartmentRepository;
import com.research.ems.repository.ProjectRepository;
import com.research.ems.repository.LeaveRequestRepository;
import com.research.ems.repository.AttendanceRepository;
import com.research.ems.repository.PayrollRepository;
import com.research.ems.service.DashboardService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

@Service
public class DashboardServiceImpl implements DashboardService {

    private final EmployeeRepository employeeRepository;
    private final DepartmentRepository departmentRepository;
    private final ProjectRepository projectRepository;
    private final LeaveRequestRepository leaveRequestRepository;
    private final AttendanceRepository attendanceRepository;
    private final PayrollRepository payrollRepository;

    public DashboardServiceImpl(
            EmployeeRepository employeeRepository,
            DepartmentRepository departmentRepository,
            ProjectRepository projectRepository,
            LeaveRequestRepository leaveRequestRepository,
            AttendanceRepository attendanceRepository,
            PayrollRepository payrollRepository) {
        this.employeeRepository = employeeRepository;
        this.departmentRepository = departmentRepository;
        this.projectRepository = projectRepository;
        this.leaveRequestRepository = leaveRequestRepository;
        this.attendanceRepository = attendanceRepository;
        this.payrollRepository = payrollRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public DashboardSummaryDto getDashboardSummary(boolean includePayroll) {
        LocalDate today = LocalDate.now();
        long totalEmployees = employeeRepository.count();
        long activeEmployees =
                employeeRepository.countByEmploymentStatus(EmploymentStatus.ACTIVE);
        long inactiveEmployees =
                employeeRepository.countByEmploymentStatusNot(EmploymentStatus.ACTIVE);
        List<Attendance> todayAttendance =
                attendanceRepository.findByAttendanceDate(today);
        long attendanceCount = todayAttendance.size();

        List<AttendanceStatsDto> attendanceStats =
                Arrays.stream(AttendanceStatus.values())
                        .map(status -> {
                            long count = todayAttendance.stream()
                                    .filter(item -> item.getStatus() == status)
                                    .count();
                            return new AttendanceStatsDto(
                                    status, count,
                                    percentage(count, attendanceCount));
                        })
                        .toList();

        List<EmployeeStatusStatsDto> employeeStatusStats =
                Arrays.stream(EmploymentStatus.values())
                        .map(status -> {
                            long count = employeeRepository
                                    .countByEmploymentStatus(status);
                            return new EmployeeStatusStatsDto(
                                    status, count, percentage(count, totalEmployees));
                        })
                        .toList();

        List<RecentEmployeeDto> recentEmployees = employeeRepository
                .findAll(PageRequest.of(
                        0, 5, Sort.by(Sort.Direction.DESC, "joiningDate")))
                .getContent()
                .stream()
                .map(this::toRecentEmployee)
                .toList();

        BigDecimal totalPayroll = includePayroll
                ? payrollRepository.sumNetSalary()
                : null;
        return new DashboardSummaryDto(
                totalEmployees,
                activeEmployees,
                inactiveEmployees,
                departmentRepository.count(),
                projectRepository.count(),
                projectRepository.countByStatus(ProjectStatus.ACTIVE),
                leaveRequestRepository.countByStatus(LeaveStatus.PENDING),
                attendanceRepository.countByAttendanceDateAndStatus(
                        today, AttendanceStatus.PRESENT),
                attendanceRepository.countByAttendanceDateAndStatus(
                        today, AttendanceStatus.ABSENT),
                leaveRequestRepository
                        .countByStatusAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
                                LeaveStatus.APPROVED, today, today),
                totalPayroll,
                departmentRepository.getDepartmentStats(),
                attendanceStats,
                employeeStatusStats,
                recentEmployees);
    }

    private RecentEmployeeDto toRecentEmployee(Employee employee) {
        String employeeName = employee.getFirstName() + " " +
                employee.getLastName();
        String departmentName = employee.getDepartment() == null
                ? null
                : employee.getDepartment().getName();
        return new RecentEmployeeDto(
                employee.getId(),
                employee.getEmployeeCode(),
                employeeName,
                employee.getDesignation(),
                departmentName,
                employee.getJoiningDate());
    }

    private double percentage(long count, long total) {
        return total == 0 ? 0.0 : count * 100.0 / total;
    }
}
