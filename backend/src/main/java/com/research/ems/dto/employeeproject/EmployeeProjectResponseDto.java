package com.research.ems.dto.employeeproject;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeProjectResponseDto {

    private Long id;

    private Long employeeId;

    private String employeeCode;

    private String employeeName;

    private Long projectId;

    private String projectCode;

    private String projectName;

    private String projectRole;

    private Integer allocationPercentage;

    private LocalDate assignedDate;

    private LocalDate releasedDate;

    private Boolean active;
}