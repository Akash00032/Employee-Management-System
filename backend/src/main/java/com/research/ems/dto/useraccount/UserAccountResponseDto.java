package com.research.ems.dto.useraccount;

import com.research.ems.entity.enums.Role;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserAccountResponseDto {

    private Long id;

    private Long employeeId;

    private String employeeCode;

    private String employeeName;

    private String username;

    private String email;

    private Role role;

    private Boolean enabled;

    private LocalDateTime lastLogin;
}