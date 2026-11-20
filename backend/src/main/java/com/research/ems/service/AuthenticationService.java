package com.research.ems.service;

import com.research.ems.dto.auth.ChangePasswordRequestDto;
import com.research.ems.dto.auth.LoginRequestDto;
import com.research.ems.dto.auth.LoginResponseDto;

public interface AuthenticationService {

    LoginResponseDto login(LoginRequestDto request);

    LoginResponseDto refresh(String refreshToken);

    void logout(String refreshToken);

    void changePassword(
            String username,
            ChangePasswordRequestDto request);
}
