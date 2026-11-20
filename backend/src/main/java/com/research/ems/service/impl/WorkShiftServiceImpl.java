package com.research.ems.service.impl;

import com.research.ems.entity.WorkShift;

import com.research.ems.dto.workshift.WorkShiftRequestDto;
import com.research.ems.dto.workshift.WorkShiftResponseDto;
import com.research.ems.exception.workshift.DuplicateWorkShiftException;
import com.research.ems.exception.workshift.WorkShiftNotFoundException;
import com.research.ems.mapper.WorkShiftMapper;
import com.research.ems.repository.WorkShiftRepository;
import com.research.ems.service.WorkShiftService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.util.List;

@Service
@Transactional
public class WorkShiftServiceImpl implements WorkShiftService {

    private final WorkShiftRepository workShiftRepository;
    private final WorkShiftMapper workShiftMapper;

    public WorkShiftServiceImpl(
            WorkShiftRepository workShiftRepository,
            WorkShiftMapper workShiftMapper) {

        this.workShiftRepository = workShiftRepository;
        this.workShiftMapper = workShiftMapper;
    }

    @Override
    public WorkShiftResponseDto createWorkShift(
            WorkShiftRequestDto dto) {

        validateTimes(dto);

        if (workShiftRepository.existsByNameIgnoreCase(dto.getName())) {
            throw new DuplicateWorkShiftException(
                    "Work shift already exists: " + dto.getName());
        }

        WorkShift workShift = workShiftMapper.toEntity(dto);

        if (workShift.getOvernight() == null) {
            workShift.setOvernight(false);
        }

        if (workShift.getActive() == null) {
            workShift.setActive(true);
        }

        return workShiftMapper.toResponseDto(
                workShiftRepository.save(workShift));
    }

    @Override
    @Transactional(readOnly = true)
    public WorkShiftResponseDto getWorkShiftById(Long id) {

        return workShiftMapper.toResponseDto(findWorkShift(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<WorkShiftResponseDto> getAllWorkShifts() {

        return workShiftRepository.findAll()
                .stream()
                .map(workShiftMapper::toResponseDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<WorkShiftResponseDto> getActiveWorkShifts() {

        return workShiftRepository.findByActiveTrue()
                .stream()
                .map(workShiftMapper::toResponseDto)
                .toList();
    }

    @Override
    public WorkShiftResponseDto updateWorkShift(
            Long id,
            WorkShiftRequestDto dto) {

        validateTimes(dto);

        WorkShift workShift = findWorkShift(id);

        if (workShiftRepository.existsByNameIgnoreCaseAndIdNot(
                dto.getName(), id)) {
            throw new DuplicateWorkShiftException(
                    "Work shift already exists: " + dto.getName());
        }

        workShiftMapper.updateEntity(workShift, dto);

        return workShiftMapper.toResponseDto(
                workShiftRepository.save(workShift));
    }

    @Override
    public void deleteWorkShift(Long id) {

        WorkShift workShift = findWorkShift(id);

        if (workShift.getEmployees() != null &&
                !workShift.getEmployees().isEmpty()) {

            throw new IllegalStateException(
                    "Work shift cannot be deleted because employees are assigned to it");
        }

        workShiftRepository.delete(workShift);
    }

    private WorkShift findWorkShift(Long id) {

        return workShiftRepository.findById(id)
                .orElseThrow(() ->
                        new WorkShiftNotFoundException(
                                "Work shift not found with id: " + id));
    }

    private void validateTimes(WorkShiftRequestDto dto) {

        LocalTime start = dto.getStartTime();
        LocalTime end = dto.getEndTime();

        if (start == null || end == null) {
            return;
        }

        if (!Boolean.TRUE.equals(dto.getOvernight())
                && !end.isAfter(start)) {

            throw new IllegalArgumentException(
                    "End time must be after start time for a non-overnight shift");
        }

        if (dto.getBreakStartTime() != null &&
                dto.getBreakEndTime() != null &&
                !dto.getBreakEndTime()
                        .isAfter(dto.getBreakStartTime())) {

            throw new IllegalArgumentException(
                    "Break end time must be after break start time");
        }
    }
}