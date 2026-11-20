package com.research.ems.service.impl;

import com.research.ems.entity.LeaveType;

import com.research.ems.dto.leavetype.LeaveTypeRequestDto;
import com.research.ems.dto.leavetype.LeaveTypeResponseDto;
import com.research.ems.mapper.LeaveTypeMapper;
import com.research.ems.repository.LeaveTypeRepository;
import com.research.ems.service.LeaveTypeService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class LeaveTypeServiceImpl implements LeaveTypeService {

    private final LeaveTypeRepository leaveTypeRepository;
    private final LeaveTypeMapper leaveTypeMapper;

    public LeaveTypeServiceImpl(
            LeaveTypeRepository leaveTypeRepository,
            LeaveTypeMapper leaveTypeMapper) {

        this.leaveTypeRepository = leaveTypeRepository;
        this.leaveTypeMapper = leaveTypeMapper;
    }

    @Override
    public LeaveTypeResponseDto createLeaveType(
            LeaveTypeRequestDto dto) {

        if (leaveTypeRepository.existsByCodeIgnoreCase(dto.getCode())) {
            throw new IllegalArgumentException(
                    "Leave type code already exists: " + dto.getCode());
        }

        if (leaveTypeRepository.existsByNameIgnoreCase(dto.getName())) {
            throw new IllegalArgumentException(
                    "Leave type name already exists: " + dto.getName());
        }

        LeaveType leaveType = leaveTypeMapper.toEntity(dto);

        return leaveTypeMapper.toResponseDto(
                leaveTypeRepository.save(leaveType));
    }

    @Override
    @Transactional(readOnly = true)
    public LeaveTypeResponseDto getLeaveTypeById(Long id) {
        return leaveTypeMapper.toResponseDto(findLeaveType(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<LeaveTypeResponseDto> getAllLeaveTypes() {

        return leaveTypeRepository.findAll()
                .stream()
                .map(leaveTypeMapper::toResponseDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<LeaveTypeResponseDto> getActiveLeaveTypes() {
        return leaveTypeRepository.findByActiveTrue().stream()
                .map(leaveTypeMapper::toResponseDto)
                .toList();
    }

    @Override
    public LeaveTypeResponseDto updateLeaveType(
            Long id,
            LeaveTypeRequestDto dto) {

        LeaveType leaveType = findLeaveType(id);

        if (leaveTypeRepository.existsByCodeIgnoreCaseAndIdNot(
                dto.getCode(), id)) {
            throw new IllegalArgumentException(
                    "Leave type code already exists");
        }

        if (leaveTypeRepository.existsByNameIgnoreCaseAndIdNot(
                dto.getName(), id)) {
            throw new IllegalArgumentException(
                    "Leave type name already exists");
        }

        leaveTypeMapper.updateEntity(leaveType, dto);

        return leaveTypeMapper.toResponseDto(
                leaveTypeRepository.save(leaveType));
    }

    @Override
    public void deleteLeaveType(Long id) {

        leaveTypeRepository.delete(findLeaveType(id));
    }

    private LeaveType findLeaveType(Long id) {

        return leaveTypeRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Leave type not found with id: " + id));
    }
}