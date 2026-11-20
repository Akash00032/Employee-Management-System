package com.research.ems.repository;

import com.research.ems.entity.UserAccount;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserAccountRepository
        extends JpaRepository<UserAccount, Long> {

    Optional<UserAccount> findByUsernameIgnoreCase(String username);

    Optional<UserAccount> findByEmailIgnoreCase(String email);

    Optional<UserAccount> findByEmployeeId(Long employeeId);

    boolean existsByUsernameIgnoreCase(String username);

    boolean existsByEmailIgnoreCase(String email);

    boolean existsByEmployeeId(Long employeeId);

    boolean existsByUsernameIgnoreCaseAndIdNot(
            String username,
            Long id
    );

    boolean existsByEmailIgnoreCaseAndIdNot(
            String email,
            Long id
    );
}
