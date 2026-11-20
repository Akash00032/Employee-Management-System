package com.research.ems.service.impl;

import com.research.ems.entity.Department;

import com.research.ems.dto.department.DepartmentRequestDto;
import com.research.ems.dto.department.DepartmentResponseDto;
import com.research.ems.exception.department.DepartmentNotFoundException;
import com.research.ems.exception.department.DuplicateDepartmentException;
import com.research.ems.mapper.DepartmentMapper;
import com.research.ems.repository.DepartmentRepository;
import com.research.ems.repository.EmployeeRepository;
import com.research.ems.service.DepartmentService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class DepartmentServiceImpl implements DepartmentService {

    private final DepartmentRepository departmentRepository;
    private final EmployeeRepository employeeRepository;
    private final DepartmentMapper departmentMapper;

    public DepartmentServiceImpl(
            DepartmentRepository departmentRepository,
            EmployeeRepository employeeRepository,
            DepartmentMapper departmentMapper) {

        this.departmentRepository = departmentRepository;
        this.employeeRepository = employeeRepository;
        this.departmentMapper = departmentMapper;
    }

    @Override
    public DepartmentResponseDto createDepartment(
            DepartmentRequestDto dto) {

        if (departmentRepository.existsByCodeIgnoreCase(dto.getCode())) {
            throw new DuplicateDepartmentException(
                    "Department code already exists: " + dto.getCode());
        }

        if (departmentRepository.existsByNameIgnoreCase(dto.getName())) {
            throw new DuplicateDepartmentException(
                    "Department name already exists: " + dto.getName());
        }

        Department department = departmentMapper.toEntity(dto);

        return departmentMapper.toResponseDto(
                departmentRepository.save(department));
    }

    @Override
    @Transactional(readOnly = true)
    public DepartmentResponseDto getDepartmentById(Long id) {

        return departmentMapper.toResponseDto(findDepartment(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<DepartmentResponseDto> getAllDepartments() {

        return departmentRepository.findAll()
                .stream()
                .map(departmentMapper::toResponseDto)
                .toList();
    }

    @Override
    public DepartmentResponseDto updateDepartment(
            Long id,
            DepartmentRequestDto dto) {

        Department department = findDepartment(id);

        if (departmentRepository.existsByCodeIgnoreCaseAndIdNot(
                dto.getCode(), id)) {
            throw new DuplicateDepartmentException(
                    "Department code already exists: " + dto.getCode());
        }

        if (departmentRepository.existsByNameIgnoreCaseAndIdNot(
                dto.getName(), id)) {
            throw new DuplicateDepartmentException(
                    "Department name already exists: " + dto.getName());
        }

        departmentMapper.updateEntity(department, dto);

        return departmentMapper.toResponseDto(
                departmentRepository.save(department));
    }

    @Override
    public void deleteDepartment(Long id) {

        findDepartment(id);

        if (employeeRepository.existsByDepartmentId(id)) {
            throw new IllegalStateException(
                    "Department cannot be deleted because employees are assigned to it");
        }

        departmentRepository.deleteById(id);
    }

    private Department findDepartment(Long id) {

        return departmentRepository.findById(id)
                .orElseThrow(() ->
                        new DepartmentNotFoundException(
                                "Department not found with id: " + id));
    }
}