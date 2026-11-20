package com.research.ems.mapper;

import com.research.ems.entity.SalaryComponent;

import com.research.ems.dto.salarycomponent.SalaryComponentRequestDto;
import com.research.ems.dto.salarycomponent.SalaryComponentResponseDto;
import org.springframework.stereotype.Component;

@Component
public class SalaryComponentMapper {

    public SalaryComponent toEntity(
            SalaryComponentRequestDto dto) {

        if (dto == null) {
            return null;
        }

        SalaryComponent component = new SalaryComponent();

        component.setCode(dto.getCode());
        component.setName(dto.getName());
        component.setType(dto.getType());
        component.setDescription(dto.getDescription());

        if (dto.getActive() != null) {
            component.setActive(dto.getActive());
        }

        return component;
    }

    public void updateEntity(
            SalaryComponent component,
            SalaryComponentRequestDto dto) {

        if (component == null || dto == null) {
            return;
        }

        component.setCode(dto.getCode());
        component.setName(dto.getName());
        component.setType(dto.getType());
        component.setDescription(dto.getDescription());

        if (dto.getActive() != null) {
            component.setActive(dto.getActive());
        }
    }

    public SalaryComponentResponseDto toResponseDto(
            SalaryComponent component) {

        if (component == null) {
            return null;
        }

        return new SalaryComponentResponseDto(
                component.getId(),
                component.getCode(),
                component.getName(),
                component.getType(),
                component.getDescription(),
                component.getActive()
        );
    }
}