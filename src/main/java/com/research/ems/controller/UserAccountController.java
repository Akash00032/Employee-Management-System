package com.research.ems.controller;

import com.research.ems.dto.useraccount.UserAccountRequestDto;
import com.research.ems.dto.useraccount.UserAccountResponseDto;
import com.research.ems.dto.useraccount.UserAccountUpdateDto;
import com.research.ems.service.UserAccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/user-accounts")
@RequiredArgsConstructor
public class UserAccountController {

    private final UserAccountService userAccountService;

    @PostMapping
    public ResponseEntity<UserAccountResponseDto> createUserAccount(
            @Valid @RequestBody UserAccountRequestDto requestDto) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(userAccountService.createUserAccount(requestDto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserAccountResponseDto> getUserAccountById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                userAccountService.getUserAccountById(id)
        );
    }

    @GetMapping
    public ResponseEntity<List<UserAccountResponseDto>> getAllUserAccounts() {

        return ResponseEntity.ok(
                userAccountService.getAllUserAccounts()
        );
    }

    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<UserAccountResponseDto> getUserAccountByEmployeeId(
            @PathVariable Long employeeId) {

        return ResponseEntity.ok(
                userAccountService.getUserAccountByEmployeeId(employeeId)
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserAccountResponseDto> updateUserAccount(
            @PathVariable Long id,
            @Valid @RequestBody UserAccountUpdateDto requestDto) {

        return ResponseEntity.ok(
                userAccountService.updateUserAccount(id, requestDto)
        );
    }

    @PatchMapping("/{id}/enable")
    public ResponseEntity<UserAccountResponseDto> enableUserAccount(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                userAccountService.enableUserAccount(id)
        );
    }

    @PatchMapping("/{id}/disable")
    public ResponseEntity<UserAccountResponseDto> disableUserAccount(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                userAccountService.disableUserAccount(id)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUserAccount(
            @PathVariable Long id) {

        userAccountService.deleteUserAccount(id);
        return ResponseEntity.noContent().build();
    }
}