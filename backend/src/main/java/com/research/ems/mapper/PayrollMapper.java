package com.research.ems.mapper;

import com.research.ems.entity.Payroll;

import com.research.ems.dto.payroll.PayrollRequestDto;
import com.research.ems.dto.payroll.PayrollResponseDto;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class PayrollMapper {

    public Payroll toEntity(PayrollRequestDto dto) {

        if (dto == null) {
            return null;
        }

        Payroll payroll = new Payroll();

        payroll.setPayPeriodStart(dto.getPayPeriodStart());
        payroll.setPayPeriodEnd(dto.getPayPeriodEnd());
        payroll.setBasicSalary(defaultAmount(dto.getBasicSalary()));
        payroll.setAllowances(defaultAmount(dto.getAllowances()));
        payroll.setDeductions(defaultAmount(dto.getDeductions()));

        calculateTotals(payroll);

        return payroll;
    }

    public void updateEntity(
            Payroll payroll,
            PayrollRequestDto dto) {

        if (payroll == null || dto == null) {
            return;
        }

        payroll.setPayPeriodStart(dto.getPayPeriodStart());
        payroll.setPayPeriodEnd(dto.getPayPeriodEnd());
        payroll.setBasicSalary(defaultAmount(dto.getBasicSalary()));
        payroll.setAllowances(defaultAmount(dto.getAllowances()));
        payroll.setDeductions(defaultAmount(dto.getDeductions()));

        calculateTotals(payroll);
    }

    public PayrollResponseDto toResponseDto(
            Payroll payroll) {

        if (payroll == null) {
            return null;
        }

        Long employeeId = null;
        String employeeCode = null;
        String employeeName = null;

        if (payroll.getEmployee() != null) {
            employeeId = payroll.getEmployee().getId();
            employeeCode = payroll.getEmployee().getEmployeeCode();

            employeeName = buildFullName(
                    payroll.getEmployee().getFirstName(),
                    payroll.getEmployee().getLastName()
            );
        }

        return new PayrollResponseDto(
                payroll.getId(),
                employeeId,
                employeeCode,
                employeeName,
                payroll.getPayPeriodStart(),
                payroll.getPayPeriodEnd(),
                payroll.getBasicSalary(),
                payroll.getAllowances(),
                payroll.getDeductions(),
                payroll.getGrossSalary(),
                payroll.getNetSalary(),
                payroll.getStatus(),
                payroll.getPaymentDate()
        );
    }

    public void calculateTotals(Payroll payroll) {

        if (payroll == null) {
            return;
        }

        BigDecimal basic = defaultAmount(
                payroll.getBasicSalary()
        );

        BigDecimal allowances = defaultAmount(
                payroll.getAllowances()
        );

        BigDecimal deductions = defaultAmount(
                payroll.getDeductions()
        );

        BigDecimal gross = basic.add(allowances);
        BigDecimal net = gross.subtract(deductions);

        payroll.setGrossSalary(gross);
        payroll.setNetSalary(net);
    }

    private BigDecimal defaultAmount(BigDecimal amount) {

        return amount == null
                ? BigDecimal.ZERO
                : amount;
    }

    private String buildFullName(
            String firstName,
            String lastName) {

        if (firstName == null) {
            return lastName;
        }

        if (lastName == null) {
            return firstName;
        }

        return firstName + " " + lastName;
    }
}