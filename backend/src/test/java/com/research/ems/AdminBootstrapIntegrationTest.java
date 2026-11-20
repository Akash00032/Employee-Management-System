package com.research.ems;

import com.research.ems.entity.Employee;
import com.research.ems.entity.UserAccount;
import com.research.ems.repository.EmployeeRepository;
import com.research.ems.repository.UserAccountRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:admin-bootstrap;DB_CLOSE_DELAY=-1",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "app.bootstrap.admin.enabled=true",
        "app.bootstrap.admin.password=Admin123"
})
@AutoConfigureMockMvc
@ActiveProfiles("dev")
class AdminBootstrapIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private UserAccountRepository userAccountRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    void createsMissingAdminEmployeeAndAllowsConfiguredLogin() throws Exception {
        Employee employee = employeeRepository
                .findByEmployeeCodeIgnoreCase("EMP001")
                .orElseThrow();
        UserAccount account = userAccountRepository
                .findByUsernameIgnoreCase("admin")
                .orElseThrow();

        assertThat(account.getEmployee().getId()).isEqualTo(employee.getId());
        assertThat(passwordEncoder.matches(
                "Admin123", account.getPassword())).isTrue();

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"admin\",\"password\":\"Admin123\"}"))
                .andExpect(status().isOk());
    }
}
