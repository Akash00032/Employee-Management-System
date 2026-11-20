package com.research.ems.dto.salarycomponent;

import com.research.ems.entity.enums.SalaryComponentType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SalaryComponentRequestDto {

    @NotBlank(message = "Salary component code is required")
    @Size(max = 30, message = "Code must not exceed 30 characters")
    private String code;

    @NotBlank(message = "Salary component name is required")
    @Size(max = 100, message = "Name must not exceed 100 characters")
    private String name;

    @NotNull(message = "Salary component type is required")
    private SalaryComponentType type;

    @Size(max = 500, message = "Description must not exceed 500 characters")
    private String description;

    private Boolean active;
}