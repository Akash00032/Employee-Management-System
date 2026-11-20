package com.research.ems.dto.department;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DepartmentResponseDto {

    private Long id;

    private String code;

    private String name;

    private String description;

    private Boolean active;

    private Integer employeeCount;
}