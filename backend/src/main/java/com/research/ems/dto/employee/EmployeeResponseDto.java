package com.research.ems.dto.employee;

import com.research.ems.entity.enums.EmploymentStatus;
import com.research.ems.entity.enums.EmploymentType;
import com.research.ems.entity.enums.Gender;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeResponseDto {

    private Long id;

    private String employeeCode;

    private String firstName;

    private String lastName;

    private String fullName;

    private String email;

    private String phone;

    private LocalDate dateOfBirth;

    private Gender gender;

    private LocalDate joiningDate;

    private String designation;

    private EmploymentType employmentType;

    private EmploymentStatus employmentStatus;

    private BigDecimal salary;

    private Long departmentId;

    private String departmentCode;

    private String departmentName;

    private Long managerId;

    private String managerName;

    private Long workShiftId;

    private String workShiftName;
}