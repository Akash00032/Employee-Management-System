package com.research.ems.service;

import com.research.ems.dto.payroll.*;
import com.research.ems.entity.enums.PayrollStatus;

import java.time.LocalDate;
import java.util.List;

public interface PayrollService {

    PayrollResponseDto createPayroll(
            PayrollRequestDto requestDto
    );

    PayrollResponseDto getPayrollById(Long id);

    List<PayrollResponseDto> getAllPayrolls();

    List<PayrollResponseDto> getEmployeePayrolls(
            Long employeeId
    );

    PayrollResponseDto getEmployeePayrollForPeriod(
            Long employeeId,
            LocalDate startDate,
            LocalDate endDate
    );

    List<PayrollResponseDto> getPayrollsByStatus(
            PayrollStatus status
    );

    PayrollResponseDto updatePayroll(
            Long id,
            PayrollRequestDto requestDto
    );

    PayrollResponseDto processPayroll(Long id);

    PayrollResponseDto markPayrollAsPaid(
            Long id
    );

    void deletePayroll(Long id);
}
