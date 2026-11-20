package com.research.ems.dto.dashboard;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DashboardSummaryDto {

    private long totalEmployees;

    private long activeEmployees;

    private long inactiveEmployees;

    private long totalDepartments;

    private long totalProjects;

    private long activeProjects;

    private long pendingLeaveRequests;

    private long presentToday;

    private long absentToday;

    private long onLeaveToday;

    private BigDecimal totalPayroll;

    private List<DepartmentStatsDto> departmentStats;

    private List<AttendanceStatsDto> attendanceStats;

    private List<EmployeeStatusStatsDto> employeeStatusStats;

    private List<RecentEmployeeDto> recentEmployees;
}