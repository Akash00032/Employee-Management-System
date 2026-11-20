package com.research.ems.mapper;

import com.research.ems.entity.Employee;

import com.research.ems.dto.employee.EmployeeRequestDto;
import com.research.ems.dto.employee.EmployeeResponseDto;
import org.springframework.stereotype.Component;

@Component
public class EmployeeMapper {

    public Employee toEntity(EmployeeRequestDto dto) {

        if (dto == null) {
            return null;
        }

        Employee employee = new Employee();

        employee.setEmployeeCode(dto.getEmployeeCode());
        employee.setFirstName(dto.getFirstName());
        employee.setLastName(dto.getLastName());
        employee.setEmail(dto.getEmail());
        employee.setPhone(dto.getPhone());
        employee.setDateOfBirth(dto.getDateOfBirth());
        employee.setGender(dto.getGender());
        employee.setJoiningDate(dto.getJoiningDate());
        employee.setDesignation(dto.getDesignation());
        employee.setEmploymentType(dto.getEmploymentType());
        employee.setEmploymentStatus(dto.getEmploymentStatus());
        employee.setSalary(dto.getSalary());

        return employee;
    }

    public void updateEntity(Employee employee, EmployeeRequestDto dto) {

        if (employee == null || dto == null) {
            return;
        }

        employee.setEmployeeCode(dto.getEmployeeCode());
        employee.setFirstName(dto.getFirstName());
        employee.setLastName(dto.getLastName());
        employee.setEmail(dto.getEmail());
        employee.setPhone(dto.getPhone());
        employee.setDateOfBirth(dto.getDateOfBirth());
        employee.setGender(dto.getGender());
        employee.setJoiningDate(dto.getJoiningDate());
        employee.setDesignation(dto.getDesignation());
        employee.setEmploymentType(dto.getEmploymentType());
        employee.setEmploymentStatus(dto.getEmploymentStatus());
        employee.setSalary(dto.getSalary());
    }

    public EmployeeResponseDto toResponseDto(Employee employee) {

        if (employee == null) {
            return null;
        }

        Long departmentId = null;
        String departmentCode = null;
        String departmentName = null;

        if (employee.getDepartment() != null) {
            departmentId = employee.getDepartment().getId();
            departmentCode = employee.getDepartment().getCode();
            departmentName = employee.getDepartment().getName();
        }

        Long managerId = null;
        String managerName = null;

        if (employee.getManager() != null) {
            managerId = employee.getManager().getId();
            managerName = buildFullName(
                    employee.getManager().getFirstName(),
                    employee.getManager().getLastName()
            );
        }

        Long workShiftId = null;
        String workShiftName = null;

        if (employee.getWorkShift() != null) {
            workShiftId = employee.getWorkShift().getId();
            workShiftName = employee.getWorkShift().getName();
        }

        return new EmployeeResponseDto(
                employee.getId(),
                employee.getEmployeeCode(),
                employee.getFirstName(),
                employee.getLastName(),
                buildFullName(employee.getFirstName(), employee.getLastName()),
                employee.getEmail(),
                employee.getPhone(),
                employee.getDateOfBirth(),
                employee.getGender(),
                employee.getJoiningDate(),
                employee.getDesignation(),
                employee.getEmploymentType(),
                employee.getEmploymentStatus(),
                employee.getSalary(),
                departmentId,
                departmentCode,
                departmentName,
                managerId,
                managerName,
                workShiftId,
                workShiftName
        );
    }

    private String buildFullName(String firstName, String lastName) {

        if (firstName == null) {
            return lastName;
        }

        if (lastName == null) {
            return firstName;
        }

        return firstName + " " + lastName;
    }
}