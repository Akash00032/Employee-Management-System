package com.research.ems.service.impl;

import com.research.ems.entity.Employee;
import com.research.ems.entity.Payroll;

import com.research.ems.dto.payroll.PayrollRequestDto;
import com.research.ems.dto.payroll.PayrollResponseDto;
import com.research.ems.entity.enums.PayrollStatus;
import com.research.ems.mapper.PayrollMapper;
import com.research.ems.repository.EmployeeRepository;
import com.research.ems.repository.PayrollRepository;
import com.research.ems.service.PayrollService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
public class PayrollServiceImpl implements PayrollService {

    private final PayrollRepository payrollRepository;
    private final EmployeeRepository employeeRepository;
    private final PayrollMapper payrollMapper;

    public PayrollServiceImpl(
            PayrollRepository payrollRepository,
            EmployeeRepository employeeRepository,
            PayrollMapper payrollMapper) {

        this.payrollRepository = payrollRepository;
        this.employeeRepository = employeeRepository;
        this.payrollMapper = payrollMapper;
    }

    @Override
    public PayrollResponseDto createPayroll(
            PayrollRequestDto dto) {

        Employee employee = findEmployee(dto.getEmployeeId());

        if (payrollRepository.existsByEmployeeIdAndPayPeriodStartAndPayPeriodEnd(
                dto.getEmployeeId(),
                dto.getPayPeriodStart(),
                dto.getPayPeriodEnd())) {

            throw new IllegalArgumentException(
                    "Payroll already exists for this employee and pay period");
        }

        Payroll payroll = payrollMapper.toEntity(dto);

        payroll.setEmployee(employee);
        payroll.setStatus(PayrollStatus.PENDING);

        return payrollMapper.toResponseDto(
                payrollRepository.save(payroll));
    }

    @Override
    @Transactional(readOnly = true)
    public PayrollResponseDto getPayrollById(Long id) {

        return payrollMapper.toResponseDto(find(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<PayrollResponseDto> getAllPayrolls() {

        return payrollRepository.findAll()
                .stream()
                .map(payrollMapper::toResponseDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<PayrollResponseDto> getEmployeePayrolls(
            Long employeeId) {

        findEmployee(employeeId);

        return payrollRepository.findByEmployeeId(employeeId)
                .stream()
                .map(payrollMapper::toResponseDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public PayrollResponseDto getEmployeePayrollForPeriod(
            Long employeeId,
            LocalDate startDate,
            LocalDate endDate) {
        findEmployee(employeeId);
        return payrollMapper.toResponseDto(
                payrollRepository.findByEmployeeIdAndPayPeriodStartAndPayPeriodEnd(
                                employeeId, startDate, endDate)
                        .orElseThrow(() -> new IllegalArgumentException(
                                "Payroll not found for the requested period")));
    }

    @Override
    @Transactional(readOnly = true)
    public List<PayrollResponseDto> getPayrollsByStatus(
            PayrollStatus status) {

        return payrollRepository.findByStatus(status)
                .stream()
                .map(payrollMapper::toResponseDto)
                .toList();
    }

    @Override
    public PayrollResponseDto processPayroll(Long id) {

        Payroll payroll = find(id);
        if (payroll.getStatus() != PayrollStatus.PENDING) {
            throw new IllegalStateException("Only pending payrolls can be processed");
        }

        payrollMapper.calculateTotals(payroll);

        payroll.setStatus(PayrollStatus.PROCESSED);

        return payrollMapper.toResponseDto(
                payrollRepository.save(payroll));
    }

    @Override
    public PayrollResponseDto updatePayroll(
            Long id,
            PayrollRequestDto dto) {
        Payroll payroll = find(id);
        if (payroll.getStatus() != PayrollStatus.PENDING) {
            throw new IllegalStateException("Only pending payrolls can be updated");
        }
        if (payrollRepository.existsByEmployeeIdAndPayPeriodStartAndPayPeriodEndAndIdNot(
                dto.getEmployeeId(), dto.getPayPeriodStart(), dto.getPayPeriodEnd(), id)) {
            throw new IllegalArgumentException(
                    "Payroll already exists for this employee and pay period");
        }

        Employee employee = findEmployee(dto.getEmployeeId());
        payrollMapper.updateEntity(payroll, dto);
        payroll.setEmployee(employee);
        return payrollMapper.toResponseDto(payrollRepository.save(payroll));
    }

    @Override
    public PayrollResponseDto markPayrollAsPaid(Long id) {

        Payroll payroll = find(id);
        if (payroll.getStatus() != PayrollStatus.PROCESSED) {
            throw new IllegalStateException("Only processed payrolls can be marked as paid");
        }

        payroll.setStatus(PayrollStatus.PAID);
        payroll.setPaymentDate(LocalDate.now());

        return payrollMapper.toResponseDto(
                payrollRepository.save(payroll));
    }

    @Override
    public void deletePayroll(Long id) {

        payrollRepository.delete(find(id));
    }

    private Payroll find(Long id) {

        return payrollRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Payroll not found with id: " + id));
    }

    private Employee findEmployee(Long id) {

        return employeeRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Employee not found with id: " + id));
    }
}