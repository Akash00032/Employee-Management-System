package com.research.ems.dto.dashboard;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DepartmentStatsDto {

    private Long departmentId;

    private String departmentCode;

    private String departmentName;

    private long employeeCount;
}