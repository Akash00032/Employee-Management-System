package com.research.ems.dto.performancereview;

import com.research.ems.entity.Employee;


import com.research.ems.entity.enums.PerformanceRating;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PerformanceReviewRequestDto {

    @NotNull(message = "Employee ID is required")
    private Long employeeId;

    private Long reviewerId;

    @NotNull(message = "Review date is required")
    private LocalDate reviewDate;

    @NotNull(message = "Performance rating is required")
    private PerformanceRating rating;

    @Size(max = 2000)
    private String strengths;

    @Size(max = 2000)
    private String improvements;

    @Size(max = 2000)
    private String comments;

    private LocalDate nextReviewDate;
}