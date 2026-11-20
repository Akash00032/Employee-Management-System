package com.research.ems.service.impl;

import com.research.ems.entity.Employee;
import com.research.ems.entity.EmployeeSalary;
import com.research.ems.entity.SalaryComponent;

import com.research.ems.dto.employeesalary.EmployeeSalaryRequestDto;
import com.research.ems.dto.employeesalary.EmployeeSalaryResponseDto;
import com.research.ems.mapper.EmployeeSalaryMapper;
import com.research.ems.repository.EmployeeRepository;
import com.research.ems.repository.EmployeeSalaryRepository;
import com.research.ems.repository.SalaryComponentRepository;
import com.research.ems.service.EmployeeSalaryService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
public class EmployeeSalaryServiceImpl
        implements EmployeeSalaryService {

    private final EmployeeSalaryRepository repository;
    private final EmployeeRepository employeeRepository;
    private final SalaryComponentRepository componentRepository;
    private final EmployeeSalaryMapper mapper;

    public EmployeeSalaryServiceImpl(
            EmployeeSalaryRepository repository,
            EmployeeRepository employeeRepository,
            SalaryComponentRepository componentRepository,
            EmployeeSalaryMapper mapper) {

        this.repository = repository;
        this.employeeRepository = employeeRepository;
        this.componentRepository = componentRepository;
        this.mapper = mapper;
    }

    @Override
    public EmployeeSalaryResponseDto createEmployeeSalary(
            EmployeeSalaryRequestDto dto) {

        Employee employee = findEmployee(dto.getEmployeeId());
        SalaryComponent component =
                findComponent(dto.getSalaryComponentId());

        EmployeeSalary salary = mapper.toEntity(dto);

        salary.setEmployee(employee);
        salary.setSalaryComponent(component);

        return mapper.toResponseDto(
                repository.save(salary));
    }

    @Override
    @Transactional(readOnly = true)
    public EmployeeSalaryResponseDto getEmployeeSalaryById(Long id) {

        return mapper.toResponseDto(find(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<EmployeeSalaryResponseDto> getAllEmployeeSalaries() {

        return repository.findAll()
                .stream()
                .map(mapper::toResponseDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<EmployeeSalaryResponseDto> getEmployeeSalaries(
            Long employeeId) {

        findEmployee(employeeId);

        return repository.findByEmployeeId(employeeId)
                .stream()
                .map(mapper::toResponseDto)
                .toList();
    }

    @Override
    public EmployeeSalaryResponseDto updateEmployeeSalary(
            Long id,
            EmployeeSalaryRequestDto dto) {

        EmployeeSalary salary = find(id);

        Employee employee =
                findEmployee(dto.getEmployeeId());

        SalaryComponent component =
                findComponent(dto.getSalaryComponentId());

        mapper.updateEntity(salary, dto);

        salary.setEmployee(employee);
        salary.setSalaryComponent(component);

        return mapper.toResponseDto(
                repository.save(salary));
    }

    @Override
    public void deleteEmployeeSalary(Long id) {

        repository.delete(find(id));
    }

    private EmployeeSalary find(Long id) {

        return repository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Employee salary not found with id: " + id));
    }

    private Employee findEmployee(Long id) {

        return employeeRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Employee not found with id: " + id));
    }

    private SalaryComponent findComponent(Long id) {

        return componentRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Salary component not found with id: " + id));
    }
}