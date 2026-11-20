package com.research.ems.mapper;

import com.research.ems.entity.WorkShift;

import com.research.ems.dto.workshift.WorkShiftRequestDto;
import com.research.ems.dto.workshift.WorkShiftResponseDto;
import org.springframework.stereotype.Component;

@Component
public class WorkShiftMapper {

    public WorkShift toEntity(WorkShiftRequestDto dto) {

        if (dto == null) {
            return null;
        }

        WorkShift workShift = new WorkShift();

        workShift.setName(dto.getName());
        workShift.setStartTime(dto.getStartTime());
        workShift.setEndTime(dto.getEndTime());
        workShift.setBreakStartTime(dto.getBreakStartTime());
        workShift.setBreakEndTime(dto.getBreakEndTime());

        if (dto.getOvernight() != null) {
            workShift.setOvernight(dto.getOvernight());
        }

        if (dto.getActive() != null) {
            workShift.setActive(dto.getActive());
        }

        return workShift;
    }

    public void updateEntity(
            WorkShift workShift,
            WorkShiftRequestDto dto) {

        if (workShift == null || dto == null) {
            return;
        }

        workShift.setName(dto.getName());
        workShift.setStartTime(dto.getStartTime());
        workShift.setEndTime(dto.getEndTime());
        workShift.setBreakStartTime(dto.getBreakStartTime());
        workShift.setBreakEndTime(dto.getBreakEndTime());

        if (dto.getOvernight() != null) {
            workShift.setOvernight(dto.getOvernight());
        }

        if (dto.getActive() != null) {
            workShift.setActive(dto.getActive());
        }
    }

    public WorkShiftResponseDto toResponseDto(WorkShift workShift) {

        if (workShift == null) {
            return null;
        }

        int employeeCount = workShift.getEmployees() == null
                ? 0
                : workShift.getEmployees().size();

        return new WorkShiftResponseDto(
                workShift.getId(),
                workShift.getName(),
                workShift.getStartTime(),
                workShift.getEndTime(),
                workShift.getBreakStartTime(),
                workShift.getBreakEndTime(),
                workShift.getOvernight(),
                workShift.getActive(),
                employeeCount
        );
    }
}