package com.research.ems.dto.leavetype;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LeaveTypeRequestDto {

    @NotBlank(message = "Leave type code is required")
    @Size(max = 20, message = "Code must not exceed 20 characters")
    private String code;

    @NotBlank(message = "Leave type name is required")
    @Size(max = 100, message = "Name must not exceed 100 characters")
    private String name;

    @NotNull(message = "Maximum leave days are required")
    @Positive(message = "Maximum leave days must be greater than zero")
    private Integer maxDays;

    private Boolean paidLeave;

    private Boolean active;

    @Size(max = 500, message = "Description must not exceed 500 characters")
    private String description;
}