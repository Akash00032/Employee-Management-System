package com.research.ems.dto.project;

import com.research.ems.entity.Project;


import com.research.ems.entity.enums.ProjectStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProjectRequestDto {

    @NotBlank(message = "Project code is required")
    @Size(max = 30)
    private String code;

    @NotBlank(message = "Project name is required")
    @Size(max = 150)
    private String name;

    @Size(max = 1000)
    private String description;

    private LocalDate startDate;

    private LocalDate endDate;

    private ProjectStatus status;

    private Long projectManagerId;
}