package com.research.ems.security;

import com.research.ems.entity.UserAccount;
import com.research.ems.repository.UserAccountRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
public class AuthorizationService {

    private final UserAccountRepository userAccountRepository;

    public AuthorizationService(
            UserAccountRepository userAccountRepository) {
        this.userAccountRepository = userAccountRepository;
    }

    public boolean isCurrentEmployee(
            Authentication authentication,
            Long employeeId) {

        if (authentication == null ||
                !authentication.isAuthenticated()) {
            return false;
        }

        String username = authentication.getName();

        return userAccountRepository
                .findByUsernameIgnoreCase(username)
                .map(UserAccount::getEmployee)
                .map(employee -> employee.getId().equals(employeeId))
                .orElse(false);
    }
}