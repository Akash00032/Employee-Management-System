package com.research.ems.service.impl;

import com.research.ems.entity.Address;
import com.research.ems.entity.Employee;

import com.research.ems.dto.address.AddressRequestDto;
import com.research.ems.dto.address.AddressResponseDto;
import com.research.ems.mapper.AddressMapper;
import com.research.ems.repository.AddressRepository;
import com.research.ems.repository.EmployeeRepository;
import com.research.ems.service.AddressService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AddressServiceImpl implements AddressService {

    private final AddressRepository addressRepository;
    private final EmployeeRepository employeeRepository;
    private final AddressMapper addressMapper;

    public AddressServiceImpl(
            AddressRepository addressRepository,
            EmployeeRepository employeeRepository,
            AddressMapper addressMapper) {

        this.addressRepository = addressRepository;
        this.employeeRepository = employeeRepository;
        this.addressMapper = addressMapper;
    }

    @Override
    public AddressResponseDto createAddress(
            Long employeeId,
            AddressRequestDto dto) {

        Employee employee = findEmployee(employeeId);

        if (addressRepository.existsByEmployeeId(employeeId)) {
            throw new IllegalArgumentException(
                    "Address already exists for employee");
        }

        Address address = addressMapper.toEntity(dto);

        address.setEmployee(employee);

        return addressMapper.toResponseDto(
                addressRepository.save(address));
    }

    @Override
    @Transactional(readOnly = true)
    public AddressResponseDto getEmployeeAddress(
            Long employeeId) {

        return addressMapper.toResponseDto(
                addressRepository.findByEmployeeId(employeeId)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Address not found for employee")));
    }

    @Override
    public AddressResponseDto updateAddress(
            Long employeeId,
            AddressRequestDto dto) {

        Address address =
                addressRepository.findByEmployeeId(employeeId)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Address not found for employee"));

        addressMapper.updateEntity(address, dto);

        return addressMapper.toResponseDto(
                addressRepository.save(address));
    }

    @Override
    public void deleteAddress(Long employeeId) {

        Address address =
                addressRepository.findByEmployeeId(employeeId)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Address not found for employee"));

        addressRepository.delete(address);
    }

    private Employee findEmployee(Long id) {

        return employeeRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Employee not found with id: " + id));
    }
}