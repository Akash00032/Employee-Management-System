package com.research.ems.dto.address;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AddressResponseDto {

    private Long id;

    private Long employeeId;

    private String employeeCode;

    private String addressLine1;

    private String addressLine2;

    private String city;

    private String state;

    private String postalCode;

    private String country;
}