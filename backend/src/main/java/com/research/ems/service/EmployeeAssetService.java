package com.research.ems.service;

import com.research.ems.dto.employeeasset.*;
import com.research.ems.entity.enums.AssetStatus;

import java.time.LocalDate;
import java.util.List;

public interface EmployeeAssetService {

    EmployeeAssetResponseDto assignAsset(
            EmployeeAssetRequestDto requestDto
    );

    EmployeeAssetResponseDto getAssetById(Long id);

    List<EmployeeAssetResponseDto> getAllAssets();

    List<EmployeeAssetResponseDto> getEmployeeAssets(
            Long employeeId
    );

    List<EmployeeAssetResponseDto> getEmployeeAssetsByStatus(
            Long employeeId,
            AssetStatus status
    );

    List<EmployeeAssetResponseDto> getAssetsByStatus(
            AssetStatus status
    );

    EmployeeAssetResponseDto returnAsset(
            Long assetId,
            LocalDate returnDate
    );

    EmployeeAssetResponseDto updateAsset(
            Long id,
            EmployeeAssetRequestDto requestDto
    );

    void deleteAsset(Long id);
}
