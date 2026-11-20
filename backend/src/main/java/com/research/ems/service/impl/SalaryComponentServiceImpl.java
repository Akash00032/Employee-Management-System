package com.research.ems.service.impl;

import com.research.ems.entity.SalaryComponent;

import com.research.ems.dto.salarycomponent.SalaryComponentRequestDto;
import com.research.ems.dto.salarycomponent.SalaryComponentResponseDto;
import com.research.ems.entity.enums.SalaryComponentType;
import com.research.ems.mapper.SalaryComponentMapper;
import com.research.ems.repository.SalaryComponentRepository;
import com.research.ems.service.SalaryComponentService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class SalaryComponentServiceImpl
        implements SalaryComponentService {

    private final SalaryComponentRepository repository;
    private final SalaryComponentMapper mapper;

    public SalaryComponentServiceImpl(
            SalaryComponentRepository repository,
            SalaryComponentMapper mapper) {

        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public SalaryComponentResponseDto createSalaryComponent(
            SalaryComponentRequestDto dto) {

        if (repository.existsByCodeIgnoreCase(dto.getCode())) {
            throw new IllegalArgumentException(
                    "Salary component code already exists");
        }

        if (repository.existsByNameIgnoreCase(dto.getName())) {
            throw new IllegalArgumentException(
                    "Salary component name already exists");
        }

        return mapper.toResponseDto(
                repository.save(mapper.toEntity(dto)));
    }

    @Override
    @Transactional(readOnly = true)
    public SalaryComponentResponseDto getSalaryComponentById(Long id) {

        return mapper.toResponseDto(find(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<SalaryComponentResponseDto> getAllSalaryComponents() {

        return repository.findAll()
                .stream()
                .map(mapper::toResponseDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<SalaryComponentResponseDto> getActiveSalaryComponents() {
        return repository.findByActiveTrue().stream()
                .map(mapper::toResponseDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<SalaryComponentResponseDto> getSalaryComponentsByType(
            SalaryComponentType type) {
        return repository.findByType(type).stream()
                .map(mapper::toResponseDto)
                .toList();
    }

    @Override
    public SalaryComponentResponseDto updateSalaryComponent(
            Long id,
            SalaryComponentRequestDto dto) {

        SalaryComponent component = find(id);

        if (repository.existsByCodeIgnoreCaseAndIdNot(
                dto.getCode(), id)) {
            throw new IllegalArgumentException(
                    "Salary component code already exists");
        }

        if (repository.existsByNameIgnoreCaseAndIdNot(
                dto.getName(), id)) {
            throw new IllegalArgumentException(
                    "Salary component name already exists");
        }

        mapper.updateEntity(component, dto);

        return mapper.toResponseDto(
                repository.save(component));
    }

    @Override
    public void deleteSalaryComponent(Long id) {
        repository.delete(find(id));
    }

    private SalaryComponent find(Long id) {

        return repository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Salary component not found with id: " + id));
    }
}