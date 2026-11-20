package com.research.ems.service;

import com.research.ems.dto.performancereview.*;
import com.research.ems.entity.enums.PerformanceRating;
import java.util.List;

public interface PerformanceReviewService {

    PerformanceReviewResponseDto createPerformanceReview(
            PerformanceReviewRequestDto requestDto
    );

    PerformanceReviewResponseDto getPerformanceReviewById(Long id);

    List<PerformanceReviewResponseDto> getAllPerformanceReviews();

    List<PerformanceReviewResponseDto> getEmployeeReviews(
            Long employeeId
    );

    List<PerformanceReviewResponseDto> getReviewerReviews(Long reviewerId);

    List<PerformanceReviewResponseDto> getReviewsByRating(
            PerformanceRating rating
    );

    PerformanceReviewResponseDto updatePerformanceReview(
            Long id,
            PerformanceReviewRequestDto requestDto
    );

    void deletePerformanceReview(Long id);
}
