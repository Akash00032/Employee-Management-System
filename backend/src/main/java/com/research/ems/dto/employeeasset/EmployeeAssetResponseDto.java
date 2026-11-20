package com.research.ems.dto.employeeasset;

import com.research.ems.entity.enums.AssetStatus;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeAssetResponseDto {

    private Long id;

    private String assetName;

    private String assetType;

    private String serialNumber;

    private Long employeeId;

    private String employeeCode;

    private String employeeName;

    private LocalDate assignedDate;

    private LocalDate returnDate;

    private AssetStatus status;

    private String remarks;
}