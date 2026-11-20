package com.research.ems.mapper;

import com.research.ems.entity.LeaveType;

import com.research.ems.dto.leavetype.LeaveTypeRequestDto;
import com.research.ems.dto.leavetype.LeaveTypeResponseDto;
import org.springframework.stereotype.Component;

@Component
public class LeaveTypeMapper {

    public LeaveType toEntity(LeaveTypeRequestDto dto) {

        if (dto == null) {
            return null;
        }

        LeaveType leaveType = new LeaveType();

        leaveType.setCode(dto.getCode());
        leaveType.setName(dto.getName());
        leaveType.setMaxDays(dto.getMaxDays());
        leaveType.setPaidLeave(dto.getPaidLeave());
        leaveType.setDescription(dto.getDescription());

        if (dto.getActive() != null) {
            leaveType.setActive(dto.getActive());
        }

        return leaveType;
    }

    public void updateEntity(
            LeaveType leaveType,
            LeaveTypeRequestDto dto) {

        if (leaveType == null || dto == null) {
            return;
        }

        leaveType.setCode(dto.getCode());
        leaveType.setName(dto.getName());
        leaveType.setMaxDays(dto.getMaxDays());
        leaveType.setPaidLeave(dto.getPaidLeave());
        leaveType.setDescription(dto.getDescription());

        if (dto.getActive() != null) {
            leaveType.setActive(dto.getActive());
        }
    }

    public LeaveTypeResponseDto toResponseDto(
            LeaveType leaveType) {

        if (leaveType == null) {
            return null;
        }

        return new LeaveTypeResponseDto(
                leaveType.getId(),
                leaveType.getCode(),
                leaveType.getName(),
                leaveType.getMaxDays(),
                leaveType.getPaidLeave(),
                leaveType.getActive(),
                leaveType.getDescription()
        );
    }
}