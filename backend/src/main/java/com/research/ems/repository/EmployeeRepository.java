package com.research.ems.repository;

import com.research.ems.entity.Employee;

import com.research.ems.entity.enums.EmploymentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    Optional<Employee> findByEmployeeCodeIgnoreCase(String employeeCode);

    Optional<Employee> findByEmailIgnoreCase(String email);

    Optional<Employee> findByPhone(String phone);

    boolean existsByEmployeeCodeIgnoreCase(String employeeCode);

    boolean existsByEmailIgnoreCase(String email);

    boolean existsByPhone(String phone);

    boolean existsByEmployeeCodeIgnoreCaseAndIdNot(
            String employeeCode,
            Long id
    );

    boolean existsByEmailIgnoreCaseAndIdNot(
            String email,
            Long id
    );

    boolean existsByPhoneAndIdNot(
            String phone,
            Long id
    );

    List<Employee>
    findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseOrEmailContainingIgnoreCase(
            String firstName,
            String lastName,
            String email
    );

    Page<Employee>
    findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseOrEmailContainingIgnoreCase(
            String firstName,
            String lastName,
            String email,
            Pageable pageable
    );

    Page<Employee> findByDesignationIgnoreCase(
            String designation,
            Pageable pageable
    );

    Page<Employee> findByDepartmentId(
            Long departmentId,
            Pageable pageable
    );

    Page<Employee> findByEmploymentStatus(
            EmploymentStatus employmentStatus,
            Pageable pageable
    );

    Page<Employee> findByDepartmentIdAndEmploymentStatus(
            Long departmentId,
            EmploymentStatus employmentStatus,
            Pageable pageable
    );

    List<Employee> findByDepartmentId(Long departmentId);

    List<Employee> findByManagerId(Long managerId);

    long countByDepartmentId(Long departmentId);

    boolean existsByDepartmentId(Long departmentId);

    long countByEmploymentStatus(EmploymentStatus employmentStatus);

    long countByEmploymentStatusNot(EmploymentStatus employmentStatus);
}
