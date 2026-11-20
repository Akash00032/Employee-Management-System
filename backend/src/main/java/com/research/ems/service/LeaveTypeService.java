package com.research.ems.service;

import com.research.ems.dto.leavetype.*;


import java.util.List;

public interface LeaveTypeService {

    LeaveTypeResponseDto createLeaveType(
            LeaveTypeRequestDto requestDto
    );

    LeaveTypeResponseDto getLeaveTypeById(Long id);

    List<LeaveTypeResponseDto> getAllLeaveTypes();

    List<LeaveTypeResponseDto> getActiveLeaveTypes();

    LeaveTypeResponseDto updateLeaveType(
            Long id,
            LeaveTypeRequestDto requestDto
    );

    void deleteLeaveType(Long id);
}
