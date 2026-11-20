package com.research.ems.dto.project;

import com.research.ems.entity.enums.ProjectStatus;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProjectResponseDto {

    private Long id;

    private String code;

    private String name;

    private String description;

    private LocalDate startDate;

    private LocalDate endDate;

    private ProjectStatus status;

    private Long projectManagerId;

    private String projectManagerName;

    private Long employeeCount;
}