package com.research.ems.service;

import com.research.ems.dto.employeesalary.*;

import java.util.List;

public interface EmployeeSalaryService {

    EmployeeSalaryResponseDto createEmployeeSalary(
            EmployeeSalaryRequestDto requestDto
    );

    EmployeeSalaryResponseDto getEmployeeSalaryById(Long id);

    List<EmployeeSalaryResponseDto> getAllEmployeeSalaries();

    List<EmployeeSalaryResponseDto> getEmployeeSalaries(
            Long employeeId
    );

    EmployeeSalaryResponseDto updateEmployeeSalary(
            Long id,
            EmployeeSalaryRequestDto requestDto
    );

    void deleteEmployeeSalary(Long id);
}
