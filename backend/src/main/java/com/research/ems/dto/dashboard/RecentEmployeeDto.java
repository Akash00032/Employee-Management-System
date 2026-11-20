package com.research.ems.dto.dashboard;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RecentEmployeeDto {

    private Long id;

    private String employeeCode;

    private String employeeName;

    private String designation;

    private String departmentName;

    private LocalDate joiningDate;
}