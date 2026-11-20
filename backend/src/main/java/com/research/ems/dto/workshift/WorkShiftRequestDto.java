package com.research.ems.dto.workshift;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class WorkShiftRequestDto {

    @NotBlank(message = "Shift name is required")
    @Size(
            min = 2,
            max = 100,
            message = "Shift name must be between 2 and 100 characters"
    )
    private String name;

    @NotNull(message = "Start time is required")
    private LocalTime startTime;

    @NotNull(message = "End time is required")
    private LocalTime endTime;

    private LocalTime breakStartTime;

    private LocalTime breakEndTime;

    private Boolean overnight;

    private Boolean active;
}