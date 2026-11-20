package com.research.ems.controller;

import com.research.ems.dto.address.AddressRequestDto;
import com.research.ems.dto.address.AddressResponseDto;
import com.research.ems.service.AddressService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/employees/{employeeId}/address")
@RequiredArgsConstructor
public class AddressController {

    private final AddressService addressService;

    @PostMapping
    public ResponseEntity<AddressResponseDto> createAddress(
            @PathVariable Long employeeId,
            @Valid @RequestBody AddressRequestDto requestDto) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(addressService.createAddress(employeeId, requestDto));
    }

    @GetMapping
    public ResponseEntity<AddressResponseDto> getAddressByEmployeeId(
            @PathVariable Long employeeId) {

        return ResponseEntity.ok(
                addressService.getEmployeeAddress(employeeId)
        );
    }

    @PutMapping
    public ResponseEntity<AddressResponseDto> updateAddress(
            @PathVariable Long employeeId,
            @Valid @RequestBody AddressRequestDto requestDto) {

        return ResponseEntity.ok(
                addressService.updateAddress(employeeId, requestDto)
        );
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteAddress(
            @PathVariable Long employeeId) {

        addressService.deleteAddress(employeeId);
        return ResponseEntity.noContent().build();
    }
}