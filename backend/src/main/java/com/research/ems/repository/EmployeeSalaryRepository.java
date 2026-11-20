package com.research.ems.repository;

import com.research.ems.entity.EmployeeSalary;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface EmployeeSalaryRepository
        extends JpaRepository<EmployeeSalary, Long> {

    List<EmployeeSalary> findByEmployeeId(
            Long employeeId
    );

    List<EmployeeSalary> findByEmployeeIdAndActiveTrue(
            Long employeeId
    );

    List<EmployeeSalary> findBySalaryComponentId(
            Long salaryComponentId
    );

    List<EmployeeSalary>
    findByEmployeeIdAndEffectiveFromLessThanEqual(
            Long employeeId,
            LocalDate date
    );

    boolean existsByEmployeeIdAndSalaryComponentIdAndActiveTrue(
            Long employeeId,
            Long salaryComponentId
    );
}
