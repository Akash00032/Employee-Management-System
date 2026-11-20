package com.research.ems.dto.performancereview;

import com.research.ems.entity.enums.PerformanceRating;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PerformanceReviewResponseDto {

    private Long id;

    private Long employeeId;

    private String employeeCode;

    private String employeeName;

    private Long reviewerId;

    private String reviewerName;

    private LocalDate reviewDate;

    private PerformanceRating rating;

    private String strengths;

    private String improvements;

    private String comments;

    private LocalDate nextReviewDate;
}