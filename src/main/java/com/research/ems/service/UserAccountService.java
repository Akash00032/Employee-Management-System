package com.research.ems.service;

import com.research.ems.dto.useraccount.*;
import java.util.List;

public interface UserAccountService {

    UserAccountResponseDto createUserAccount(
            UserAccountRequestDto requestDto
    );

    UserAccountResponseDto getUserAccountById(Long id);

    List<UserAccountResponseDto> getAllUserAccounts();

    UserAccountResponseDto getUserAccountByEmployeeId(
            Long employeeId
    );

    UserAccountResponseDto getUserAccountByUsername(String username);

    UserAccountResponseDto updateUserAccount(
            Long id,
            UserAccountUpdateDto requestDto
    );

    UserAccountResponseDto enableUserAccount(Long id);

    UserAccountResponseDto disableUserAccount(Long id);

    void deleteUserAccount(Long id);
}
