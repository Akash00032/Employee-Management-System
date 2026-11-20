package com.research.ems;

import com.research.ems.repository.UserAccountRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:admin-bootstrap-disabled;DB_CLOSE_DELAY=-1",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "app.bootstrap.admin.enabled=false"
})
@ActiveProfiles("dev")
class AdminBootstrapDisabledIntegrationTest {

    @Autowired
    private UserAccountRepository userAccountRepository;

    @Test
    void doesNotCreateAdminWhenBootstrapIsDisabled() {
        assertThat(userAccountRepository.findByUsernameIgnoreCase("admin")).isEmpty();
    }
}
