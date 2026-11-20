package com.research.ems.mapper;

import com.research.ems.entity.Department;

import com.research.ems.dto.department.DepartmentRequestDto;
import com.research.ems.dto.department.DepartmentResponseDto;
import org.springframework.stereotype.Component;

@Component
public class DepartmentMapper {

    public Department toEntity(DepartmentRequestDto dto) {

        if (dto == null) {
            return null;
        }

        Department department = new Department();

        department.setCode(dto.getCode());
        department.setName(dto.getName());
        department.setDescription(dto.getDescription());

        if (dto.getActive() != null) {
            department.setActive(dto.getActive());
        }

        return department;
    }

    public void updateEntity(
            Department department,
            DepartmentRequestDto dto) {

        if (department == null || dto == null) {
            return;
        }

        department.setCode(dto.getCode());
        department.setName(dto.getName());
        department.setDescription(dto.getDescription());

        if (dto.getActive() != null) {
            department.setActive(dto.getActive());
        }
    }

    public DepartmentResponseDto toResponseDto(Department department) {

        if (department == null) {
            return null;
        }

        int employeeCount = department.getEmployees() == null
                ? 0
                : department.getEmployees().size();

        return new DepartmentResponseDto(
                department.getId(),
                department.getCode(),
                department.getName(),
                department.getDescription(),
                department.getActive(),
                employeeCount
        );
    }
}