package com.research.ems.mapper;

import com.research.ems.entity.PerformanceReview;

import com.research.ems.dto.performancereview.PerformanceReviewRequestDto;
import com.research.ems.dto.performancereview.PerformanceReviewResponseDto;
import org.springframework.stereotype.Component;

@Component
public class PerformanceReviewMapper {

    public PerformanceReview toEntity(
            PerformanceReviewRequestDto dto) {

        if (dto == null) {
            return null;
        }

        PerformanceReview review = new PerformanceReview();

        review.setReviewDate(dto.getReviewDate());
        review.setRating(dto.getRating());
        review.setStrengths(dto.getStrengths());
        review.setImprovements(dto.getImprovements());
        review.setComments(dto.getComments());
        review.setNextReviewDate(dto.getNextReviewDate());

        return review;
    }

    public void updateEntity(
            PerformanceReview review,
            PerformanceReviewRequestDto dto) {

        if (review == null || dto == null) {
            return;
        }

        review.setReviewDate(dto.getReviewDate());
        review.setRating(dto.getRating());
        review.setStrengths(dto.getStrengths());
        review.setImprovements(dto.getImprovements());
        review.setComments(dto.getComments());
        review.setNextReviewDate(dto.getNextReviewDate());
    }

    public PerformanceReviewResponseDto toResponseDto(
            PerformanceReview review) {

        if (review == null) {
            return null;
        }

        Long employeeId = null;
        String employeeCode = null;
        String employeeName = null;

        if (review.getEmployee() != null) {
            employeeId = review.getEmployee().getId();
            employeeCode = review.getEmployee().getEmployeeCode();

            employeeName = buildFullName(
                    review.getEmployee().getFirstName(),
                    review.getEmployee().getLastName()
            );
        }

        Long reviewerId = null;
        String reviewerName = null;

        if (review.getReviewer() != null) {
            reviewerId = review.getReviewer().getId();

            reviewerName = buildFullName(
                    review.getReviewer().getFirstName(),
                    review.getReviewer().getLastName()
            );
        }

        return new PerformanceReviewResponseDto(
                review.getId(),
                employeeId,
                employeeCode,
                employeeName,
                reviewerId,
                reviewerName,
                review.getReviewDate(),
                review.getRating(),
                review.getStrengths(),
                review.getImprovements(),
                review.getComments(),
                review.getNextReviewDate()
        );
    }

    private String buildFullName(
            String firstName,
            String lastName) {

        if (firstName == null) {
            return lastName;
        }

        if (lastName == null) {
            return firstName;
        }

        return firstName + " " + lastName;
    }
}