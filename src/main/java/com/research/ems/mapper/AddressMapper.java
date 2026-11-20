package com.research.ems.mapper;

import com.research.ems.entity.Address;

import com.research.ems.dto.address.AddressRequestDto;
import com.research.ems.dto.address.AddressResponseDto;
import org.springframework.stereotype.Component;

@Component
public class AddressMapper {

    public Address toEntity(AddressRequestDto dto) {

        if (dto == null) {
            return null;
        }

        Address address = new Address();

        address.setAddressLine1(dto.getAddressLine1());
        address.setAddressLine2(dto.getAddressLine2());
        address.setCity(dto.getCity());
        address.setState(dto.getState());
        address.setPostalCode(dto.getPostalCode());
        address.setCountry(dto.getCountry());

        return address;
    }

    public void updateEntity(
            Address address,
            AddressRequestDto dto) {

        if (address == null || dto == null) {
            return;
        }

        address.setAddressLine1(dto.getAddressLine1());
        address.setAddressLine2(dto.getAddressLine2());
        address.setCity(dto.getCity());
        address.setState(dto.getState());
        address.setPostalCode(dto.getPostalCode());
        address.setCountry(dto.getCountry());
    }

    public AddressResponseDto toResponseDto(
            Address address) {

        if (address == null) {
            return null;
        }

        Long employeeId = null;
        String employeeCode = null;

        if (address.getEmployee() != null) {
            employeeId = address.getEmployee().getId();
            employeeCode = address.getEmployee().getEmployeeCode();
        }

        return new AddressResponseDto(
                address.getId(),
                employeeId,
                employeeCode,
                address.getAddressLine1(),
                address.getAddressLine2(),
                address.getCity(),
                address.getState(),
                address.getPostalCode(),
                address.getCountry()
        );
    }
}