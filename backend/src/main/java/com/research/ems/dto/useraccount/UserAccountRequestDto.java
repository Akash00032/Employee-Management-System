package com.research.ems.dto.useraccount;

import com.research.ems.entity.Employee;


import com.research.ems.entity.enums.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserAccountRequestDto {

    @NotNull(message = "Employee ID is required")
    private Long employeeId;

    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 50)
    private String username;

    @NotBlank(message = "Password is required")
    @Size(min = 8, max = 255)
    private String password;

    @NotBlank(message = "Email is required")
    @Email(message = "Please provide a valid email")
    @Size(max = 100)
    private String email;

    @NotNull(message = "Role is required")
    private Role role;

    private Boolean enabled;
}