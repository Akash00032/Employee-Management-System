package com.research.ems.repository;

import com.research.ems.entity.PerformanceReview;

import com.research.ems.entity.enums.PerformanceRating;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface PerformanceReviewRepository
        extends JpaRepository<PerformanceReview, Long> {

    List<PerformanceReview> findByEmployeeId(
            Long employeeId
    );

    Page<PerformanceReview> findByEmployeeId(
            Long employeeId,
            Pageable pageable
    );

    List<PerformanceReview> findByReviewerId(
            Long reviewerId
    );

    List<PerformanceReview> findByEmployeeIdAndRating(
            Long employeeId,
            PerformanceRating rating
    );

    List<PerformanceReview> findByRating(PerformanceRating rating);

    List<PerformanceReview> findByNextReviewDateBefore(
            LocalDate date
    );
}
