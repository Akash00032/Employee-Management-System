package com.research.ems.dto.employeesalary;

import com.research.ems.entity.enums.SalaryComponentType;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeSalaryResponseDto {

    private Long id;

    private Long employeeId;

    private String employeeCode;

    private String employeeName;

    private Long salaryComponentId;

    private String salaryComponentCode;

    private String salaryComponentName;

    private SalaryComponentType salaryComponentType;

    private BigDecimal amount;

    private LocalDate effectiveFrom;

    private LocalDate effectiveTo;

    private Boolean active;
}