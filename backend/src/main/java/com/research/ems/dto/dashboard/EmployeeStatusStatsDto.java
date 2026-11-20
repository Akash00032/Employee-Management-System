package com.research.ems.dto.dashboard;

import com.research.ems.entity.enums.EmploymentStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeStatusStatsDto {

    private EmploymentStatus status;

    private long count;

    private double percentage;
}