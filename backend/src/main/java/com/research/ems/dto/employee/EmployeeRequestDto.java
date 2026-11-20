package com.research.ems.dto.employee;

import com.research.ems.entity.Department;
import com.research.ems.entity.Employee;


import com.research.ems.entity.enums.EmploymentStatus;
import com.research.ems.entity.enums.EmploymentType;
import com.research.ems.entity.enums.Gender;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
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
public class EmployeeRequestDto {

    @NotBlank(message = "Employee code is required")
    @Size(
            max = 30,
            message = "Employee code must not exceed 30 characters"
    )
    private String employeeCode;

    @NotBlank(message = "First name is required")
    @Size(
            min = 2,
            max = 50,
            message = "First name must be between 2 and 50 characters"
    )
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Size(
            min = 2,
            max = 50,
            message = "Last name must be between 2 and 50 characters"
    )
    private String lastName;

    @NotBlank(message = "Email is required")
    @Email(message = "Please provide a valid email address")
    @Size(
            max = 100,
            message = "Email must not exceed 100 characters"
    )
    private String email;

    @NotBlank(message = "Phone number is required")
    @Size(
            min = 10,
            max = 15,
            message = "Phone number must be between 10 and 15 characters"
    )
    private String phone;

    @Past(message = "Date of birth must be in the past")
    private LocalDate dateOfBirth;

    private Gender gender;

    @NotNull(message = "Joining date is required")
    private LocalDate joiningDate;

    @NotBlank(message = "Designation is required")
    @Size(
            max = 100,
            message = "Designation must not exceed 100 characters"
    )
    private String designation;

    @NotNull(message = "Employment type is required")
    private EmploymentType employmentType;

    @NotNull(message = "Employment status is required")
    private EmploymentStatus employmentStatus;

    @NotNull(message = "Salary is required")
    @Positive(message = "Salary must be greater than zero")
    private BigDecimal salary;

    @NotNull(message = "Department ID is required")
    @Positive(message = "Department ID must be greater than zero")
    private Long departmentId;

    private Long managerId;

    private Long workShiftId;
}