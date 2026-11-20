package com.research.ems.mapper;

import com.research.ems.entity.EmployeeSalary;

import com.research.ems.dto.employeesalary.EmployeeSalaryRequestDto;
import com.research.ems.dto.employeesalary.EmployeeSalaryResponseDto;
import org.springframework.stereotype.Component;

@Component
public class EmployeeSalaryMapper {

    public EmployeeSalary toEntity(
            EmployeeSalaryRequestDto dto) {

        if (dto == null) {
            return null;
        }

        EmployeeSalary salary = new EmployeeSalary();

        salary.setAmount(dto.getAmount());
        salary.setEffectiveFrom(dto.getEffectiveFrom());
        salary.setEffectiveTo(dto.getEffectiveTo());

        if (dto.getActive() != null) {
            salary.setActive(dto.getActive());
        }

        return salary;
    }

    public void updateEntity(
            EmployeeSalary salary,
            EmployeeSalaryRequestDto dto) {

        if (salary == null || dto == null) {
            return;
        }

        salary.setAmount(dto.getAmount());
        salary.setEffectiveFrom(dto.getEffectiveFrom());
        salary.setEffectiveTo(dto.getEffectiveTo());

        if (dto.getActive() != null) {
            salary.setActive(dto.getActive());
        }
    }

    public EmployeeSalaryResponseDto toResponseDto(
            EmployeeSalary salary) {

        if (salary == null) {
            return null;
        }

        Long employeeId = null;
        String employeeCode = null;
        String employeeName = null;

        if (salary.getEmployee() != null) {
            employeeId = salary.getEmployee().getId();
            employeeCode = salary.getEmployee().getEmployeeCode();

            employeeName = buildFullName(
                    salary.getEmployee().getFirstName(),
                    salary.getEmployee().getLastName()
            );
        }

        Long componentId = null;
        String componentCode = null;
        String componentName = null;
        var componentType = (com.research.ems.entity.enums.SalaryComponentType) null;

        if (salary.getSalaryComponent() != null) {
            componentId = salary.getSalaryComponent().getId();
            componentCode = salary.getSalaryComponent().getCode();
            componentName = salary.getSalaryComponent().getName();
            componentType = salary.getSalaryComponent().getType();
        }

        return new EmployeeSalaryResponseDto(
                salary.getId(),
                employeeId,
                employeeCode,
                employeeName,
                componentId,
                componentCode,
                componentName,
                componentType,
                salary.getAmount(),
                salary.getEffectiveFrom(),
                salary.getEffectiveTo(),
                salary.getActive()
        );
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