package com.research.ems.security;

import com.research.ems.entity.Attendance;
import com.research.ems.entity.Employee;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final AuthenticationEntryPoint authenticationEntryPoint;
    private final AccessDeniedHandler accessDeniedHandler;

    public SecurityConfig(
            JwtAuthenticationFilter jwtAuthenticationFilter,
            AuthenticationEntryPoint authenticationEntryPoint,
            AccessDeniedHandler accessDeniedHandler) {

        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.authenticationEntryPoint = authenticationEntryPoint;
        this.accessDeniedHandler = accessDeniedHandler;
    }

    @Bean
    SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            AuthenticationManager authenticationManager) throws Exception {

        return http
                // JWT-based API -> CSRF is not required
                .csrf(AbstractHttpConfigurer::disable)

                // Enable CorsConfig
                .cors(Customizer.withDefaults())

                // Stateless authentication
                .sessionManagement(session -> session
                        .sessionCreationPolicy(
                                SessionCreationPolicy.STATELESS))

                // Authentication / authorization errors
                .exceptionHandling(exceptions -> exceptions
                        .authenticationEntryPoint(authenticationEntryPoint)
                        .accessDeniedHandler(accessDeniedHandler))

                .authorizeHttpRequests(authorize -> authorize

                        // CORS preflight
                        .requestMatchers(
                                HttpMethod.OPTIONS,
                                "/**"
                        ).permitAll()

                        // Public endpoints
                        .requestMatchers(
                                "/api/v1/auth/login",
                                "/api/v1/auth/refresh",
                                "/actuator/health",
                                "/v3/api-docs/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html"
                        ).permitAll()

                        // ADMIN only
                        .requestMatchers(
                                "/api/v1/user-accounts/**"
                        ).hasRole(SecurityRoles.ADMIN)

                        // ADMIN + HR
                        .requestMatchers(
                                "/api/v1/departments/**",
                                "/api/v1/work-shifts/**",
                                "/api/v1/addresses/**",
                                "/api/v1/employee-documents/**",
                                "/api/v1/employee-assets/**",
                                "/api/v1/leave-types/**",
                                "/api/v1/salary-components/**",
                                "/api/v1/employee-salaries/**",
                                "/api/v1/payrolls/**"
                        ).hasAnyRole(
                                SecurityRoles.ADMIN,
                                SecurityRoles.HR
                        )

                        // Managers may read project metadata but cannot edit projects.
                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/v1/projects/**"
                        ).hasAnyRole(
                                SecurityRoles.ADMIN,
                                SecurityRoles.HR,
                                SecurityRoles.MANAGER
                        )
                        .requestMatchers(
                                "/api/v1/projects/**",
                                "/api/v1/employee-projects/**",
                                "/api/v1/performance-reviews/**"
                        ).hasAnyRole(
                                SecurityRoles.ADMIN,
                                SecurityRoles.HR
                        )

                        // Self-service and manager-team routes precede broad matchers.
                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/v1/employees/me"
                        ).hasAnyRole(
                                SecurityRoles.ADMIN,
                                SecurityRoles.HR,
                                SecurityRoles.MANAGER,
                                SecurityRoles.EMPLOYEE
                        )
                        .requestMatchers(
                                "/api/v1/employees/team/**"
                        ).hasRole(SecurityRoles.MANAGER)
                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/v1/attendance/me"
                        ).hasAnyRole(
                                SecurityRoles.ADMIN,
                                SecurityRoles.HR,
                                SecurityRoles.MANAGER,
                                SecurityRoles.EMPLOYEE
                        )
                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/v1/leave-requests/me"
                        ).hasAnyRole(
                                SecurityRoles.ADMIN,
                                SecurityRoles.HR,
                                SecurityRoles.MANAGER,
                                SecurityRoles.EMPLOYEE
                        )
                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/v1/attendance/team"
                        ).hasRole(SecurityRoles.MANAGER)
                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/v1/leave-requests/me"
                        ).hasAnyRole(
                                SecurityRoles.ADMIN,
                                SecurityRoles.HR,
                                SecurityRoles.MANAGER,
                                SecurityRoles.EMPLOYEE
                        )
                        .requestMatchers(
                                HttpMethod.DELETE,
                                "/api/v1/leave-requests/me/**"
                        ).hasAnyRole(
                                SecurityRoles.ADMIN,
                                SecurityRoles.HR,
                                SecurityRoles.MANAGER,
                                SecurityRoles.EMPLOYEE
                        )
                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/v1/leave-requests/team"
                        ).hasRole(SecurityRoles.MANAGER)
                        .requestMatchers(
                                HttpMethod.PATCH,
                                "/api/v1/leave-requests/team/**"
                        ).hasRole(SecurityRoles.MANAGER)

                        // Attendance
                        .requestMatchers(
                                "/api/v1/attendance/**"
                        ).hasAnyRole(
                                SecurityRoles.ADMIN,
                                SecurityRoles.HR
                        )

                        // Leave requests
                        .requestMatchers(
                                "/api/v1/leave-requests/**"
                        ).hasAnyRole(
                                SecurityRoles.ADMIN,
                                SecurityRoles.HR
                        )

                        // Dashboard
                        .requestMatchers(
                                "/api/v1/dashboard/**"
                        ).hasAnyRole(
                                SecurityRoles.ADMIN,
                                SecurityRoles.HR,
                                SecurityRoles.MANAGER
                        )

                        // Employee management
                        .requestMatchers(
                                "/api/v1/employees/**"
                        ).hasAnyRole(
                                SecurityRoles.ADMIN,
                                SecurityRoles.HR
                        )

                        // Everything else
                        .anyRequest().authenticated()
                )

                // Use our AuthenticationManager
                .authenticationManager(authenticationManager)

                // JWT authentication happens before username/password filter
                .addFilterBefore(
                        jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class
                )

                .build();
    }

    @Bean
    AuthenticationManager authenticationManager(
            org.springframework.security.core.userdetails.UserDetailsService userDetailsService,
            org.springframework.security.crypto.password.PasswordEncoder passwordEncoder) {

        DaoAuthenticationProvider provider =
                new DaoAuthenticationProvider(userDetailsService);

        provider.setPasswordEncoder(passwordEncoder);

        return new ProviderManager(provider);
    }
}