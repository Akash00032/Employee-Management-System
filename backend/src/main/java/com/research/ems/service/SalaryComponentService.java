package com.research.ems.service;

import com.research.ems.dto.salarycomponent.*;

import com.research.ems.entity.enums.SalaryComponentType;

import java.util.List;

public interface SalaryComponentService {

    SalaryComponentResponseDto createSalaryComponent(
            SalaryComponentRequestDto requestDto
    );

    SalaryComponentResponseDto getSalaryComponentById(Long id);

    List<SalaryComponentResponseDto> getAllSalaryComponents();

    List<SalaryComponentResponseDto> getActiveSalaryComponents();

    List<SalaryComponentResponseDto> getSalaryComponentsByType(
            SalaryComponentType type
    );

    SalaryComponentResponseDto updateSalaryComponent(
            Long id,
            SalaryComponentRequestDto requestDto
    );

    void deleteSalaryComponent(Long id);
}
