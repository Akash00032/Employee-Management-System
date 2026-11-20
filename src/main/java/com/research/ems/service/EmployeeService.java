package com.research.ems.service;

import com.research.ems.dto.employee.*;
import org.springframework.data.domain.Page;

import java.util.List;

public interface EmployeeService {

    EmployeeResponseDto createEmployee(EmployeeRequestDto requestDto);

    EmployeeResponseDto getEmployeeById(Long id);

    List<EmployeeResponseDto> getAllEmployees();

    EmployeeResponseDto updateEmployee(
            Long id,
            EmployeeRequestDto requestDto
    );

    void deleteEmployee(Long id);

    List<EmployeeResponseDto> searchEmployees(String keyword);

    Page<EmployeeResponseDto> getEmployeeDirectory(
            String keyword,
            int page,
            int size,
            String sort
    );

    Page<EmployeeResponseDto> getEmployeesByDesignation(
            String designation,
            int page,
            int size
    );

    EmployeeResponseDto getMyProfile(String name);

    List<EmployeeResponseDto> getMyTeam(String username, String keyword);

    EmployeeResponseDto getMyTeamMember(String username, Long employeeId);
}
