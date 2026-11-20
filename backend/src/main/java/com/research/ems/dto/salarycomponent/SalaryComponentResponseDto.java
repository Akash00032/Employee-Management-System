package com.research.ems.dto.salarycomponent;

import com.research.ems.entity.enums.SalaryComponentType;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SalaryComponentResponseDto {

    private Long id;

    private String code;

    private String name;

    private SalaryComponentType type;

    private String description;

    private Boolean active;
}