package com.research.ems.dto.leaverequest;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record MyLeaveRequestDto(
        @NotNull Long leaveTypeId,
        @NotNull LocalDate startDate,
        @NotNull LocalDate endDate,
        @Size(max = 1000) String reason) {
}
