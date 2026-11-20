package com.research.ems.service.impl;

import com.research.ems.entity.Employee;
import com.research.ems.entity.UserAccount;

import com.research.ems.dto.useraccount.UserAccountRequestDto;
import com.research.ems.dto.useraccount.UserAccountResponseDto;
import com.research.ems.dto.useraccount.UserAccountUpdateDto;
import com.research.ems.mapper.UserAccountMapper;
import com.research.ems.repository.EmployeeRepository;
import com.research.ems.repository.UserAccountRepository;
import com.research.ems.service.UserAccountService;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class UserAccountServiceImpl
        implements UserAccountService {

    private final UserAccountRepository repository;
    private final EmployeeRepository employeeRepository;
    private final UserAccountMapper mapper;
    private final PasswordEncoder passwordEncoder;

    public UserAccountServiceImpl(
            UserAccountRepository repository,
            EmployeeRepository employeeRepository,
            UserAccountMapper mapper,
            PasswordEncoder passwordEncoder) {

        this.repository = repository;
        this.employeeRepository = employeeRepository;
        this.mapper = mapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserAccountResponseDto createUserAccount(
            UserAccountRequestDto dto) {

        if (repository.existsByUsernameIgnoreCase(
                dto.getUsername())) {

            throw new IllegalArgumentException(
                    "Username already exists");
        }

        if (repository.existsByEmailIgnoreCase(
                dto.getEmail())) {

            throw new IllegalArgumentException(
                    "Email already exists");
        }

        Employee employee =
                findEmployee(dto.getEmployeeId());

        if (repository.existsByEmployeeId(
                dto.getEmployeeId())) {

            throw new IllegalArgumentException(
                    "Employee already has a user account");
        }

        UserAccount account =
                mapper.toEntity(dto);
        account.setPassword(passwordEncoder.encode(dto.getPassword()));

        account.setEmployee(employee);

        if (account.getEnabled() == null) {
            account.setEnabled(true);
        }

        return mapper.toResponseDto(
                repository.save(account));
    }

    @Override
    @Transactional(readOnly = true)
    public UserAccountResponseDto getUserAccountById(
            Long id) {

        return mapper.toResponseDto(find(id));
    }

    @Override
    @Transactional(readOnly = true)
    public UserAccountResponseDto getUserAccountByEmployeeId(
            Long employeeId) {
        return mapper.toResponseDto(repository.findByEmployeeId(employeeId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "User account not found for employee id: " + employeeId)));
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserAccountResponseDto>
    getAllUserAccounts() {

        return repository.findAll()
                .stream()
                .map(mapper::toResponseDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public UserAccountResponseDto getUserAccountByUsername(
            String username) {

        return mapper.toResponseDto(
                repository.findByUsernameIgnoreCase(username)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "User account not found")));
    }

    @Override
    public UserAccountResponseDto updateUserAccount(
            Long id,
            UserAccountUpdateDto dto) {

        UserAccount account = find(id);

        if (repository.existsByUsernameIgnoreCaseAndIdNot(
                dto.getUsername(), id)) {

            throw new IllegalArgumentException(
                    "Username already exists");
        }

        if (repository.existsByEmailIgnoreCaseAndIdNot(
                dto.getEmail(), id)) {

            throw new IllegalArgumentException(
                    "Email already exists");
        }

        Employee employee =
                findEmployee(dto.getEmployeeId());

        mapper.updateEntity(account, dto);

        account.setEmployee(employee);

        return mapper.toResponseDto(
                repository.save(account));
    }

    @Override
    public void deleteUserAccount(Long id) {

        repository.delete(find(id));
    }

    @Override
    public UserAccountResponseDto enableUserAccount(Long id) {
        UserAccount account = find(id);
        account.setEnabled(true);
        return mapper.toResponseDto(repository.save(account));
    }

    @Override
    public UserAccountResponseDto disableUserAccount(Long id) {
        UserAccount account = find(id);
        account.setEnabled(false);
        return mapper.toResponseDto(repository.save(account));
    }

    private UserAccount find(Long id) {

        return repository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "User account not found with id: " + id));
    }

    private Employee findEmployee(Long id) {

        return employeeRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Employee not found with id: " + id));
    }
}