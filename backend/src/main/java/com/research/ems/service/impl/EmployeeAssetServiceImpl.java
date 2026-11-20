package com.research.ems.service.impl;

import com.research.ems.entity.Employee;
import com.research.ems.entity.EmployeeAsset;

import com.research.ems.dto.employeeasset.EmployeeAssetRequestDto;
import com.research.ems.dto.employeeasset.EmployeeAssetResponseDto;
import com.research.ems.entity.enums.AssetStatus;
import com.research.ems.mapper.EmployeeAssetMapper;
import com.research.ems.repository.EmployeeAssetRepository;
import com.research.ems.repository.EmployeeRepository;
import com.research.ems.service.EmployeeAssetService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.time.LocalDate;

@Service
@Transactional
public class EmployeeAssetServiceImpl
        implements EmployeeAssetService {

    private final EmployeeAssetRepository repository;
    private final EmployeeRepository employeeRepository;
    private final EmployeeAssetMapper mapper;

    public EmployeeAssetServiceImpl(
            EmployeeAssetRepository repository,
            EmployeeRepository employeeRepository,
            EmployeeAssetMapper mapper) {

        this.repository = repository;
        this.employeeRepository = employeeRepository;
        this.mapper = mapper;
    }

    @Override
    public EmployeeAssetResponseDto assignAsset(
            EmployeeAssetRequestDto dto) {

        if (repository.existsBySerialNumberIgnoreCase(dto.getSerialNumber())) {
            throw new IllegalArgumentException(
                    "Asset serial number already exists");
        }

        Employee employee =
                findEmployee(dto.getEmployeeId());

        EmployeeAsset asset =
                mapper.toEntity(dto);

        asset.setEmployee(employee);

        if (asset.getStatus() == null) {
            asset.setStatus(AssetStatus.ASSIGNED);
        }

        return mapper.toResponseDto(
                repository.save(asset));
    }

    @Override
    @Transactional(readOnly = true)
    public EmployeeAssetResponseDto getAssetById(
            Long id) {

        return mapper.toResponseDto(find(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<EmployeeAssetResponseDto>
    getAllAssets() {

        return repository.findAll()
                .stream()
                .map(mapper::toResponseDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<EmployeeAssetResponseDto>
    getEmployeeAssets(Long employeeId) {

        findEmployee(employeeId);

        return repository.findByEmployeeId(employeeId)
                .stream()
                .map(mapper::toResponseDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<EmployeeAssetResponseDto> getEmployeeAssetsByStatus(
            Long employeeId,
            AssetStatus status) {

        findEmployee(employeeId);
        return repository.findByEmployeeIdAndStatus(employeeId, status)
                .stream()
                .map(mapper::toResponseDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<EmployeeAssetResponseDto>
    getAssetsByStatus(AssetStatus status) {

        return repository.findByStatus(status)
                .stream()
                .map(mapper::toResponseDto)
                .toList();
    }

    @Override
    public EmployeeAssetResponseDto updateAsset(
            Long id,
            EmployeeAssetRequestDto dto) {

        EmployeeAsset asset = find(id);
        Employee employee =
                findEmployee(dto.getEmployeeId());

        mapper.updateEntity(asset, dto);

        asset.setEmployee(employee);

        return mapper.toResponseDto(
                repository.save(asset));
    }

    @Override
    public EmployeeAssetResponseDto returnAsset(
            Long id,
            LocalDate returnDate) {

        EmployeeAsset asset = find(id);
        if (asset.getStatus() != AssetStatus.ASSIGNED) {
            throw new IllegalArgumentException("Only assigned assets can be returned");
        }

        asset.setStatus(AssetStatus.RETURNED);
        asset.setReturnDate(returnDate);
        return mapper.toResponseDto(repository.save(asset));
    }

    @Override
    public void deleteAsset(Long id) {

        repository.delete(find(id));
    }

    private EmployeeAsset find(Long id) {

        return repository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Employee asset not found with id: " + id));
    }

    private Employee findEmployee(Long id) {

        return employeeRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Employee not found with id: " + id));
    }
}