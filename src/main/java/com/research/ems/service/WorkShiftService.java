package com.research.ems.service;

import com.research.ems.dto.workshift.*;


import java.util.List;

public interface WorkShiftService {

    WorkShiftResponseDto createWorkShift(WorkShiftRequestDto requestDto);

    WorkShiftResponseDto getWorkShiftById(Long id);

    List<WorkShiftResponseDto> getAllWorkShifts();

    List<WorkShiftResponseDto> getActiveWorkShifts();

    WorkShiftResponseDto updateWorkShift(
            Long id,
            WorkShiftRequestDto requestDto
    );

    void deleteWorkShift(Long id);
}
