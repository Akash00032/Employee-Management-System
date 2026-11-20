package com.research.ems.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RefreshTokenResponseDto {

    private String accessToken;

    private String refreshToken;

    private String tokenType;

    private Long expiresIn;
}