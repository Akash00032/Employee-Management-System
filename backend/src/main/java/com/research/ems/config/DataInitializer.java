package com.research.ems.config;

import com.research.ems.entity.Employee;
import com.research.ems.entity.UserAccount;
import com.research.ems.entity.enums.EmploymentStatus;
import com.research.ems.entity.enums.EmploymentType;
import com.research.ems.entity.enums.Role;
import com.research.ems.repository.EmployeeRepository;
import com.research.ems.repository.UserAccountRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.time.LocalDate;

@Configuration
@Profile("dev")
public class DataInitializer {

    @Bean
    CommandLineRunner createAdminUser(
            EmployeeRepository employeeRepository,
            UserAccountRepository userAccountRepository,
            PasswordEncoder passwordEncoder,
            @Value("${app.bootstrap.admin.enabled:false}") boolean bootstrapEnabled,
            @Value("${app.bootstrap.admin.password:}") String adminPassword) {

        return args -> {
            if (!bootstrapEnabled
                    || userAccountRepository.findByUsernameIgnoreCase("admin").isPresent()) {
                return;
            }

            if (adminPassword == null || adminPassword.isBlank()) {
                throw new IllegalStateException(
                        "EMS_ADMIN_PASSWORD is required when admin bootstrap is enabled");
            }

            Employee employee = employeeRepository
                    .findByEmployeeCodeIgnoreCase("EMP001")
                    .orElseGet(() -> employeeRepository.save(Employee.builder()
                            .employeeCode("EMP001")
                            .firstName("System")
                            .lastName("Administrator")
                            .email("admin@localhost.invalid")
                            .phone("0000000000")
                            .joiningDate(LocalDate.now())
                            .designation("System Administrator")
                            .employmentType(EmploymentType.FULL_TIME)
                            .employmentStatus(EmploymentStatus.ACTIVE)
                            .salary(BigDecimal.ZERO)
                            .build()));

            if (userAccountRepository.existsByEmployeeId(employee.getId())) {
                throw new IllegalStateException(
                        "EMP001 already has a user account; refusing to create another admin");
            }

            UserAccount admin = UserAccount.builder()
                    .username("admin")
                    .password(passwordEncoder.encode(adminPassword))
                    .email(employee.getEmail())
                    .role(Role.ADMIN)
                    .enabled(true)
                    .employee(employee)
                    .build();

            userAccountRepository.save(admin);
        };
    }
}
