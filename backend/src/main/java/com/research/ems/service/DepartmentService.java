package com.research.ems.service;


import com.research.ems.dto.department.*;

import java.util.List;

public interface DepartmentService {

    DepartmentResponseDto createDepartment(
            DepartmentRequestDto requestDto
    );

    DepartmentResponseDto getDepartmentById(
            Long id
    );

    List<DepartmentResponseDto> getAllDepartments();

    DepartmentResponseDto updateDepartment(
            Long id,
            DepartmentRequestDto requestDto
    );

    void deleteDepartment(Long id);
}
