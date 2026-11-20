package com.research.ems.service.impl;

import com.research.ems.entity.Department;
import com.research.ems.entity.Employee;
import com.research.ems.entity.UserAccount;
import com.research.ems.entity.WorkShift;

import com.research.ems.dto.employee.EmployeeRequestDto;
import com.research.ems.dto.employee.EmployeeResponseDto;
import com.research.ems.entity.enums.Role;
import com.research.ems.exception.department.DepartmentNotFoundException;
import com.research.ems.exception.employee.DuplicateEmployeeException;
import com.research.ems.exception.employee.EmployeeNotFoundException;
import com.research.ems.exception.useraccount.UserAccountNotFoundException;
import com.research.ems.exception.workshift.WorkShiftNotFoundException;
import com.research.ems.mapper.EmployeeMapper;
import com.research.ems.repository.DepartmentRepository;
import com.research.ems.repository.EmployeeRepository;
import com.research.ems.repository.UserAccountRepository;
import com.research.ems.repository.WorkShiftRepository;
import com.research.ems.service.EmployeeService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;

@Service
@Transactional
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final DepartmentRepository departmentRepository;
    private final WorkShiftRepository workShiftRepository;
    private final UserAccountRepository userAccountRepository;
    private final EmployeeMapper employeeMapper;

    public EmployeeServiceImpl(
            EmployeeRepository employeeRepository,
            DepartmentRepository departmentRepository,
            WorkShiftRepository workShiftRepository,
            UserAccountRepository userAccountRepository,
            EmployeeMapper employeeMapper) {

        this.employeeRepository = employeeRepository;
        this.departmentRepository = departmentRepository;
        this.workShiftRepository = workShiftRepository;
        this.userAccountRepository = userAccountRepository;
        this.employeeMapper = employeeMapper;
    }

    @Override
    public EmployeeResponseDto createEmployee(EmployeeRequestDto dto) {

        if (employeeRepository.existsByEmployeeCodeIgnoreCase(dto.getEmployeeCode())) {
            throw new DuplicateEmployeeException(
                    "Employee code already exists: " + dto.getEmployeeCode());
        }

        if (employeeRepository.existsByEmailIgnoreCase(dto.getEmail())) {
            throw new DuplicateEmployeeException(
                    "Employee email already exists: " + dto.getEmail());
        }

        if (dto.getPhone() != null &&
                employeeRepository.existsByPhone(dto.getPhone())) {
            throw new DuplicateEmployeeException(
                    "Employee phone already exists: " + dto.getPhone());
        }

        Employee employee = employeeMapper.toEntity(dto);

        setRelationships(employee, dto);

        return employeeMapper.toResponseDto(
                employeeRepository.save(employee));
    }

    @Override
    @Transactional(readOnly = true)
    public EmployeeResponseDto getEmployeeById(Long id) {

        return employeeMapper.toResponseDto(findEmployee(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<EmployeeResponseDto> getAllEmployees() {

        return employeeRepository.findAll()
                .stream()
                .map(employeeMapper::toResponseDto)
                .toList();
    }

    @Override
    public EmployeeResponseDto updateEmployee(
            Long id,
            EmployeeRequestDto dto) {

        Employee employee = findEmployee(id);

        if (employeeRepository.existsByEmployeeCodeIgnoreCaseAndIdNot(
                dto.getEmployeeCode(), id)) {
            throw new DuplicateEmployeeException(
                    "Employee code already exists: " + dto.getEmployeeCode());
        }

        if (employeeRepository.existsByEmailIgnoreCaseAndIdNot(
                dto.getEmail(), id)) {
            throw new DuplicateEmployeeException(
                    "Employee email already exists: " + dto.getEmail());
        }

        if (dto.getPhone() != null &&
                employeeRepository.existsByPhoneAndIdNot(
                        dto.getPhone(), id)) {
            throw new DuplicateEmployeeException(
                    "Employee phone already exists: " + dto.getPhone());
        }

        employeeMapper.updateEntity(employee, dto);

        setRelationships(employee, dto);

        return employeeMapper.toResponseDto(
                employeeRepository.save(employee));
    }

    @Override
    public void deleteEmployee(Long id) {

        Employee employee = findEmployee(id);

        employeeRepository.delete(employee);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EmployeeResponseDto> searchEmployees(String keyword) {

        return employeeRepository
                .findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseOrEmailContainingIgnoreCase(
                        keyword, keyword, keyword)
                .stream()
                .map(employeeMapper::toResponseDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<EmployeeResponseDto> getEmployeeDirectory(
            String keyword,
            int page,
            int size,
            String sort) {
        if (page < 0) {
            throw new IllegalArgumentException("Page index must not be negative");
        }
        if (size < 1 || size > 100) {
            throw new IllegalArgumentException(
                    "Page size must be between 1 and 100");
        }

        Sort ordering = switch (sort) {
            case "joiningDate" -> Sort.by(Sort.Order.desc("joiningDate"),
                    Sort.Order.asc("id"));
            case "name" -> Sort.by(Sort.Order.asc("firstName"),
                    Sort.Order.asc("lastName"), Sort.Order.asc("id"));
            default -> throw new IllegalArgumentException(
                    "Unsupported employee sort: " + sort);
        };
        Pageable pageable = PageRequest.of(page, size, ordering);
        String term = keyword == null ? "" : keyword.trim();

        return employeeRepository
                .findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseOrEmailContainingIgnoreCase(
                        term, term, term, pageable)
                .map(employeeMapper::toResponseDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<EmployeeResponseDto> getEmployeesByDesignation(
            String designation,
            int page,
            int size) {

        Pageable pageable = PageRequest.of(page, size);

        return employeeRepository
                .findByDesignationIgnoreCase(designation, pageable)
                .map(employeeMapper::toResponseDto);
    }

    @Override
    @Transactional(readOnly = true)
    public EmployeeResponseDto getMyProfile(String username) {
        UserAccount userAccount = userAccountRepository
                .findByUsernameIgnoreCase(username)
                .orElseThrow(() -> new UserAccountNotFoundException(
                        "User account not found for username: " + username));

        Employee employee = userAccount.getEmployee();
        if (employee == null) {
            throw new EmployeeNotFoundException(
                    "No employee profile linked to username: " + username);
        }

        return employeeMapper.toResponseDto(employee);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EmployeeResponseDto> getMyTeam(
            String username,
            String keyword) {
        Employee manager = findManager(username);
        String normalizedKeyword = keyword == null
                ? ""
                : keyword.trim().toLowerCase(Locale.ROOT);

        return employeeRepository.findByManagerId(manager.getId())
                .stream()
                .filter(employee -> normalizedKeyword.isEmpty()
                        || contains(employee.getEmployeeCode(), normalizedKeyword)
                        || contains(employee.getFirstName(), normalizedKeyword)
                        || contains(employee.getLastName(), normalizedKeyword)
                        || contains(employee.getEmail(), normalizedKeyword))
                .map(employee -> {
                    EmployeeResponseDto response =
                            employeeMapper.toResponseDto(employee);
                    response.setSalary(null);
                    return response;
                })
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public EmployeeResponseDto getMyTeamMember(
            String username,
            Long employeeId) {
        Employee manager = findManager(username);
        Employee member = findEmployee(employeeId);
        if (member.getManager() == null
                || !manager.getId().equals(member.getManager().getId())) {
            throw new AccessDeniedException(
                    "Employee is not a direct report of the current manager");
        }

        EmployeeResponseDto response = employeeMapper.toResponseDto(member);
        response.setSalary(null);
        return response;
    }

    private Employee findManager(String username) {
        UserAccount account = userAccountRepository
                .findByUsernameIgnoreCase(username)
                .orElseThrow(() -> new UserAccountNotFoundException(
                        "User account not found"));
        if (account.getRole() != Role.MANAGER) {
            throw new AccessDeniedException("Manager role is required");
        }
        return account.getEmployee();
    }

    private boolean contains(String value, String keyword) {
        return value != null
                && value.toLowerCase(Locale.ROOT).contains(keyword);
    }

    private Employee findEmployee(Long id) {

        return employeeRepository.findById(id)
                .orElseThrow(() ->
                        new EmployeeNotFoundException(
                                "Employee not found with id: " + id));
    }

    private void setRelationships(
            Employee employee,
            EmployeeRequestDto dto) {

        if (dto.getDepartmentId() != null) {

            Department department =
                    departmentRepository.findById(dto.getDepartmentId())
                            .orElseThrow(() ->
                                    new DepartmentNotFoundException(
                                            "Department not found with id: "
                                                    + dto.getDepartmentId()));

            employee.setDepartment(department);
        } else {
            employee.setDepartment(null);
        }

        if (dto.getWorkShiftId() != null) {

            WorkShift workShift =
                    workShiftRepository.findById(dto.getWorkShiftId())
                            .orElseThrow(() ->
                                    new WorkShiftNotFoundException(
                                            "Work shift not found with id: "
                                                    + dto.getWorkShiftId()));

            employee.setWorkShift(workShift);
        } else {
            employee.setWorkShift(null);
        }

        if (dto.getManagerId() != null) {

            Employee manager =
                    employeeRepository.findById(dto.getManagerId())
                            .orElseThrow(() ->
                                    new EmployeeNotFoundException(
                                            "Manager not found with id: "
                                                    + dto.getManagerId()));

            if (employee.getId() != null &&
                    employee.getId().equals(manager.getId())) {

                throw new IllegalArgumentException(
                        "Employee cannot be their own manager");
            }

            employee.setManager(manager);

        } else {
            employee.setManager(null);
        }
    }
}