package com.research.ems.controller;

import com.research.ems.dto.performancereview.PerformanceReviewRequestDto;
import com.research.ems.dto.performancereview.PerformanceReviewResponseDto;
import com.research.ems.entity.enums.PerformanceRating;
import com.research.ems.service.PerformanceReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/performance-reviews")
@RequiredArgsConstructor
public class PerformanceReviewController {

    private final PerformanceReviewService performanceReviewService;

    @PostMapping
    public ResponseEntity<PerformanceReviewResponseDto> createPerformanceReview(
            @Valid @RequestBody PerformanceReviewRequestDto requestDto) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(performanceReviewService.createPerformanceReview(requestDto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PerformanceReviewResponseDto> getPerformanceReviewById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                performanceReviewService.getPerformanceReviewById(id)
        );
    }

    @GetMapping
    public ResponseEntity<List<PerformanceReviewResponseDto>> getAllPerformanceReviews() {

        return ResponseEntity.ok(
                performanceReviewService.getAllPerformanceReviews()
        );
    }

    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<List<PerformanceReviewResponseDto>> getEmployeeReviews(
            @PathVariable Long employeeId) {

        return ResponseEntity.ok(
                performanceReviewService.getEmployeeReviews(employeeId)
        );
    }

    @GetMapping("/reviewer/{reviewerId}")
    public ResponseEntity<List<PerformanceReviewResponseDto>> getReviewerReviews(
            @PathVariable Long reviewerId) {

        return ResponseEntity.ok(
                performanceReviewService.getReviewerReviews(reviewerId)
        );
    }

    @GetMapping("/rating/{rating}")
    public ResponseEntity<List<PerformanceReviewResponseDto>> getByRating(
            @PathVariable PerformanceRating rating) {

        return ResponseEntity.ok(
                performanceReviewService.getReviewsByRating(rating)
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<PerformanceReviewResponseDto> updatePerformanceReview(
            @PathVariable Long id,
            @Valid @RequestBody PerformanceReviewRequestDto requestDto) {

        return ResponseEntity.ok(
                performanceReviewService.updatePerformanceReview(
                        id, requestDto
                )
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePerformanceReview(
            @PathVariable Long id) {

        performanceReviewService.deletePerformanceReview(id);
        return ResponseEntity.noContent().build();
    }
}