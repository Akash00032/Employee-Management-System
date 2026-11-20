package com.research.ems.dto.payroll;

import com.research.ems.entity.Employee;


import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PayrollRequestDto {

    @NotNull(message = "Employee ID is required")
    private Long employeeId;

    @NotNull(message = "Pay period start is required")
    private LocalDate payPeriodStart;

    @NotNull(message = "Pay period end is required")
    private LocalDate payPeriodEnd;

    @NotNull(message = "Basic salary is required")
    @PositiveOrZero(message = "Basic salary cannot be negative")
    private BigDecimal basicSalary;

    @PositiveOrZero(message = "Allowances cannot be negative")
    private BigDecimal allowances;

    @PositiveOrZero(message = "Deductions cannot be negative")
    private BigDecimal deductions;
}