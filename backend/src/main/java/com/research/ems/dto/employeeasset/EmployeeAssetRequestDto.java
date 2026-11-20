package com.research.ems.dto.employeeasset;

import com.research.ems.entity.Employee;


import com.research.ems.entity.enums.AssetStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeAssetRequestDto {

    @NotBlank(message = "Asset name is required")
    @Size(max = 100)
    private String assetName;

    @NotBlank(message = "Asset type is required")
    @Size(max = 50)
    private String assetType;

    @NotBlank(message = "Serial number is required")
    @Size(max = 100)
    private String serialNumber;

    @NotNull(message = "Employee ID is required")
    private Long employeeId;

    private LocalDate assignedDate;

    private LocalDate returnDate;

    private AssetStatus status;

    @Size(max = 500)
    private String remarks;
}