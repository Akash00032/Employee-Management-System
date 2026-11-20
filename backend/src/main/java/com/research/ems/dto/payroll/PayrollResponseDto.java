package com.research.ems.dto.payroll;

import com.research.ems.entity.enums.PayrollStatus;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PayrollResponseDto {

    private Long id;

    private Long employeeId;

    private String employeeCode;

    private String employeeName;

    private LocalDate payPeriodStart;

    private LocalDate payPeriodEnd;

    private BigDecimal basicSalary;

    private BigDecimal allowances;

    private BigDecimal deductions;

    private BigDecimal grossSalary;

    private BigDecimal netSalary;

    private PayrollStatus status;

    private LocalDate paymentDate;
}