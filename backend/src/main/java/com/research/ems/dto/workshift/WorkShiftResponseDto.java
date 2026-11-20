package com.research.ems.dto.workshift;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class WorkShiftResponseDto {

    private Long id;

    private String name;

    private LocalTime startTime;

    private LocalTime endTime;

    private LocalTime breakStartTime;

    private LocalTime breakEndTime;

    private Boolean overnight;

    private Boolean active;

    private Integer employeeCount;
}