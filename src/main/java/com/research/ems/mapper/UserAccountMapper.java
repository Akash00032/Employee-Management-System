package com.research.ems.mapper;

import com.research.ems.entity.UserAccount;

import com.research.ems.dto.useraccount.UserAccountRequestDto;
import com.research.ems.dto.useraccount.UserAccountResponseDto;
import com.research.ems.dto.useraccount.UserAccountUpdateDto;
import org.springframework.stereotype.Component;

@Component
public class UserAccountMapper {

    public UserAccount toEntity(
            UserAccountRequestDto dto) {

        if (dto == null) {
            return null;
        }

        UserAccount account = new UserAccount();

        account.setUsername(dto.getUsername());
        account.setEmail(dto.getEmail());
        account.setRole(dto.getRole());

        if (dto.getEnabled() != null) {
            account.setEnabled(dto.getEnabled());
        }

        return account;
    }

    public void updateEntity(
            UserAccount account,
            UserAccountUpdateDto dto) {

        if (account == null || dto == null) {
            return;
        }

        account.setUsername(dto.getUsername());
        account.setEmail(dto.getEmail());
        account.setRole(dto.getRole());

        if (dto.getEnabled() != null) {
            account.setEnabled(dto.getEnabled());
        }
    }

    public UserAccountResponseDto toResponseDto(
            UserAccount account) {

        if (account == null) {
            return null;
        }

        Long employeeId = null;
        String employeeCode = null;
        String employeeName = null;

        if (account.getEmployee() != null) {

            employeeId = account.getEmployee().getId();

            employeeCode =
                    account.getEmployee().getEmployeeCode();

            employeeName = buildFullName(
                    account.getEmployee().getFirstName(),
                    account.getEmployee().getLastName()
            );
        }

        return new UserAccountResponseDto(
                account.getId(),
                employeeId,
                employeeCode,
                employeeName,
                account.getUsername(),
                account.getEmail(),
                account.getRole(),
                account.getEnabled(),
                account.getLastLogin()
        );
    }

    private String buildFullName(
            String firstName,
            String lastName) {

        if (firstName == null) {
            return lastName;
        }

        if (lastName == null) {
            return firstName;
        }

        return firstName + " " + lastName;
    }
}