package com.research.ems.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponseDto {

    private String accessToken;

    private String refreshToken;

    private String tokenType;

    private Long expiresIn;

    private Long userId;

    private Long employeeId;

    private String username;

    private String employeeName;

    private String role;
}