package com.research.ems.service.impl;

import com.research.ems.entity.RefreshToken;
import com.research.ems.entity.UserAccount;

import com.research.ems.dto.auth.ChangePasswordRequestDto;
import com.research.ems.dto.auth.LoginRequestDto;
import com.research.ems.dto.auth.LoginResponseDto;
import com.research.ems.exception.auth.UnauthorizedException;
import com.research.ems.repository.RefreshTokenRepository;
import com.research.ems.repository.UserAccountRepository;
import com.research.ems.security.CustomUserDetails;
import com.research.ems.security.JwtTokenProvider;
import com.research.ems.service.AuthenticationService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;

@Service
@Transactional
public class AuthenticationServiceImpl implements AuthenticationService {

    private final AuthenticationManager authenticationManager;
    private final UserAccountRepository userAccountRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final long refreshTokenExpirationMs;
    private final SecureRandom secureRandom = new SecureRandom();

    public AuthenticationServiceImpl(
            AuthenticationManager authenticationManager,
            UserAccountRepository userAccountRepository,
            RefreshTokenRepository refreshTokenRepository,
            PasswordEncoder passwordEncoder,
            JwtTokenProvider jwtTokenProvider,
            @Value("${app.jwt.refresh-token-expiration-ms:604800000}")
            long refreshTokenExpirationMs) {
        this.authenticationManager = authenticationManager;
        this.userAccountRepository = userAccountRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
        if (refreshTokenExpirationMs <= 0) {
            throw new IllegalArgumentException(
                    "JWT refresh-token expiration must be positive");
        }
        this.refreshTokenExpirationMs = refreshTokenExpirationMs;
    }

    @Override
    public LoginResponseDto login(LoginRequestDto request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(), request.getPassword()));
        CustomUserDetails principal =
                (CustomUserDetails) authentication.getPrincipal();
        UserAccount account = principal.getUserAccount();
        account.setLastLogin(LocalDateTime.now());
        userAccountRepository.save(account);
        return createResponse(principal, account);
    }

    @Override
    public LoginResponseDto refresh(String rawRefreshToken) {
        RefreshToken refreshToken = refreshTokenRepository
                .findByTokenHash(hashToken(rawRefreshToken))
                .orElseThrow(() ->
                        new UnauthorizedException("Invalid refresh token"));

        if (Boolean.TRUE.equals(refreshToken.getRevoked()) ||
                !refreshToken.getExpiresAt().isAfter(LocalDateTime.now()) ||
                !Boolean.TRUE.equals(refreshToken.getUserAccount().getEnabled())) {
            throw new UnauthorizedException("Refresh token is expired or revoked");
        }

        refreshToken.setRevoked(true);
        refreshTokenRepository.save(refreshToken);
        UserAccount account = refreshToken.getUserAccount();
        return createResponse(new CustomUserDetails(account), account);
    }

    @Override
    public void logout(String rawRefreshToken) {
        RefreshToken refreshToken = refreshTokenRepository
                .findByTokenHash(hashToken(rawRefreshToken))
                .orElseThrow(() ->
                        new UnauthorizedException("Invalid refresh token"));
        refreshToken.setRevoked(true);
        refreshTokenRepository.save(refreshToken);
    }

    @Override
    public void changePassword(
            String username,
            ChangePasswordRequestDto request) {
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new IllegalArgumentException(
                    "New password and confirmation do not match");
        }
        UserAccount account = userAccountRepository
                .findByUsernameIgnoreCase(username)
                .orElseThrow(() ->
                        new UnauthorizedException("User account not found"));
        if (!passwordEncoder.matches(
                request.getCurrentPassword(), account.getPassword())) {
            throw new UnauthorizedException("Current password is incorrect");
        }
        account.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userAccountRepository.save(account);
        refreshTokenRepository
                .findByUserAccountIdAndRevokedFalse(account.getId())
                .forEach(token -> token.setRevoked(true));
    }

    private LoginResponseDto createResponse(
            CustomUserDetails userDetails,
            UserAccount account) {
        String rawRefreshToken = createRefreshToken(account);
        String employeeName = account.getEmployee().getFirstName() + " " +
                account.getEmployee().getLastName();
        return new LoginResponseDto(
                jwtTokenProvider.generateAccessToken(userDetails),
                rawRefreshToken,
                "Bearer",
                jwtTokenProvider.getAccessTokenExpirationMs(),
                account.getId(),
                account.getEmployee().getId(),
                account.getUsername(),
                employeeName,
                account.getRole().name());
    }

    private String createRefreshToken(UserAccount account) {
        byte[] bytes = new byte[48];
        secureRandom.nextBytes(bytes);
        String rawToken = Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(bytes);

        RefreshToken token = RefreshToken.builder()
                .tokenHash(hashToken(rawToken))
                .userAccount(account)
                .expiresAt(LocalDateTime.now()
                        .plusNanos(refreshTokenExpirationMs * 1_000_000))
                .revoked(false)
                .build();
        refreshTokenRepository.save(token);
        return rawToken;
    }

    private String hashToken(String rawToken) {
        try {
            byte[] digest = MessageDigest.getInstance("SHA-256")
                    .digest(rawToken.getBytes(StandardCharsets.UTF_8));
            return java.util.HexFormat.of().formatHex(digest);
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException(
                    "SHA-256 is not available", exception);
        }
    }
}
