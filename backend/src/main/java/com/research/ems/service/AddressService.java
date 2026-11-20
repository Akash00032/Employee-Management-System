package com.research.ems.service;

import com.research.ems.dto.address.*;

public interface AddressService {

    AddressResponseDto createAddress(
            Long employeeId,
            AddressRequestDto requestDto
    );

    AddressResponseDto getEmployeeAddress(
            Long employeeId
    );

    AddressResponseDto updateAddress(
            Long employeeId,
            AddressRequestDto requestDto
    );

    void deleteAddress(Long employeeId);
}
