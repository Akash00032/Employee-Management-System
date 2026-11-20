package com.research.ems.service.impl;

import com.research.ems.entity.Employee;
import com.research.ems.entity.PerformanceReview;

import com.research.ems.dto.performancereview.PerformanceReviewRequestDto;
import com.research.ems.dto.performancereview.PerformanceReviewResponseDto;
import com.research.ems.entity.enums.PerformanceRating;
import com.research.ems.mapper.PerformanceReviewMapper;
import com.research.ems.repository.EmployeeRepository;
import com.research.ems.repository.PerformanceReviewRepository;
import com.research.ems.service.PerformanceReviewService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class PerformanceReviewServiceImpl
        implements PerformanceReviewService {

    private final PerformanceReviewRepository repository;
    private final EmployeeRepository employeeRepository;
    private final PerformanceReviewMapper mapper;

    public PerformanceReviewServiceImpl(
            PerformanceReviewRepository repository,
            EmployeeRepository employeeRepository,
            PerformanceReviewMapper mapper) {

        this.repository = repository;
        this.employeeRepository = employeeRepository;
        this.mapper = mapper;
    }

    @Override
    public PerformanceReviewResponseDto createPerformanceReview(
            PerformanceReviewRequestDto dto) {

        Employee employee = findEmployee(dto.getEmployeeId());
        Employee reviewer = findEmployee(dto.getReviewerId());

        PerformanceReview review = mapper.toEntity(dto);

        review.setEmployee(employee);
        review.setReviewer(reviewer);

        return mapper.toResponseDto(
                repository.save(review));
    }

    @Override
    @Transactional(readOnly = true)
    public PerformanceReviewResponseDto getPerformanceReviewById(
            Long id) {

        return mapper.toResponseDto(find(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<PerformanceReviewResponseDto>
    getAllPerformanceReviews() {

        return repository.findAll()
                .stream()
                .map(mapper::toResponseDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<PerformanceReviewResponseDto>
    getEmployeeReviews(Long employeeId) {

        findEmployee(employeeId);

        return repository.findByEmployeeId(employeeId)
                .stream()
                .map(mapper::toResponseDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<PerformanceReviewResponseDto> getReviewerReviews(
            Long reviewerId) {
        findEmployee(reviewerId);
        return repository.findByReviewerId(reviewerId).stream()
                .map(mapper::toResponseDto).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<PerformanceReviewResponseDto> getReviewsByRating(
            PerformanceRating rating) {
        return repository.findByRating(rating).stream()
                .map(mapper::toResponseDto).toList();
    }

    @Override
    public PerformanceReviewResponseDto updatePerformanceReview(
            Long id,
            PerformanceReviewRequestDto dto) {

        PerformanceReview review = find(id);

        Employee employee =
                findEmployee(dto.getEmployeeId());

        Employee reviewer =
                findEmployee(dto.getReviewerId());

        mapper.updateEntity(review, dto);

        review.setEmployee(employee);
        review.setReviewer(reviewer);

        return mapper.toResponseDto(
                repository.save(review));
    }

    @Override
    public void deletePerformanceReview(Long id) {

        repository.delete(find(id));
    }

    private PerformanceReview find(Long id) {

        return repository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Performance review not found with id: " + id));
    }

    private Employee findEmployee(Long id) {

        return employeeRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Employee not found with id: " + id));
    }
}