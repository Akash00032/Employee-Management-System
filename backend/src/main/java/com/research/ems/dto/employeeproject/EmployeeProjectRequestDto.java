package com.research.ems.dto.employeeproject;

import com.research.ems.entity.Employee;
import com.research.ems.entity.Project;


import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeProjectRequestDto {

    @NotNull(message = "Employee ID is required")
    private Long employeeId;

    @NotNull(message = "Project ID is required")
    private Long projectId;

    @Size(max = 100)
    private String projectRole;

    @Min(value = 1, message = "Allocation must be at least 1%")
    @Max(value = 100, message = "Allocation cannot exceed 100%")
    private Integer allocationPercentage;

    private LocalDate assignedDate;

    private LocalDate releasedDate;

    private Boolean active;
}