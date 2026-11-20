package com.research.ems.mapper;

import com.research.ems.entity.EmployeeAsset;

import com.research.ems.dto.employeeasset.EmployeeAssetRequestDto;
import com.research.ems.dto.employeeasset.EmployeeAssetResponseDto;
import org.springframework.stereotype.Component;

@Component
public class EmployeeAssetMapper {

    public EmployeeAsset toEntity(
            EmployeeAssetRequestDto dto) {

        if (dto == null) {
            return null;
        }

        EmployeeAsset asset = new EmployeeAsset();

        asset.setAssetName(dto.getAssetName());
        asset.setAssetType(dto.getAssetType());
        asset.setSerialNumber(dto.getSerialNumber());
        asset.setAssignedDate(dto.getAssignedDate());
        asset.setReturnDate(dto.getReturnDate());
        asset.setStatus(dto.getStatus());
        asset.setRemarks(dto.getRemarks());

        return asset;
    }

    public void updateEntity(
            EmployeeAsset asset,
            EmployeeAssetRequestDto dto) {

        if (asset == null || dto == null) {
            return;
        }

        asset.setAssetName(dto.getAssetName());
        asset.setAssetType(dto.getAssetType());
        asset.setSerialNumber(dto.getSerialNumber());
        asset.setAssignedDate(dto.getAssignedDate());
        asset.setReturnDate(dto.getReturnDate());
        asset.setStatus(dto.getStatus());
        asset.setRemarks(dto.getRemarks());
    }

    public EmployeeAssetResponseDto toResponseDto(
            EmployeeAsset asset) {

        if (asset == null) {
            return null;
        }

        Long employeeId = null;
        String employeeCode = null;
        String employeeName = null;

        if (asset.getEmployee() != null) {
            employeeId = asset.getEmployee().getId();
            employeeCode = asset.getEmployee().getEmployeeCode();

            employeeName = buildFullName(
                    asset.getEmployee().getFirstName(),
                    asset.getEmployee().getLastName()
            );
        }

        return new EmployeeAssetResponseDto(
                asset.getId(),
                asset.getAssetName(),
                asset.getAssetType(),
                asset.getSerialNumber(),
                employeeId,
                employeeCode,
                employeeName,
                asset.getAssignedDate(),
                asset.getReturnDate(),
                asset.getStatus(),
                asset.getRemarks()
        );
    }

    private String buildFullName(
            String firstName,
            String lastName) {

        if (firstName == null) {
            return lastName;
        }

        if (lastName == null) {
            return firstName;
        }

        return firstName + " " + lastName;
    }
}