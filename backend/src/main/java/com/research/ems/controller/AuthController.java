package com.research.ems.controller;

import com.research.ems.dto.auth.ChangePasswordRequestDto;
import com.research.ems.dto.auth.LoginRequestDto;
import com.research.ems.dto.auth.LoginResponseDto;
import com.research.ems.dto.auth.RefreshTokenRequestDto;
import com.research.ems.service.AuthenticationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationService authenticationService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(
            @Valid @RequestBody LoginRequestDto request) {
        return ResponseEntity.ok(authenticationService.login(request));
    }

    @PostMapping("/refresh")
    public ResponseEntity<LoginResponseDto> refresh(
            @Valid @RequestBody RefreshTokenRequestDto request) {
        return ResponseEntity.ok(
                authenticationService.refresh(request.getRefreshToken()));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            @Valid @RequestBody RefreshTokenRequestDto request) {
        authenticationService.logout(request.getRefreshToken());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/change-password")
    public ResponseEntity<Void> changePassword(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody ChangePasswordRequestDto request) {
        authenticationService.changePassword(
                userDetails.getUsername(), request);
        return ResponseEntity.noContent().build();
    }
}
