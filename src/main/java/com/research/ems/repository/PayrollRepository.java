package com.research.ems.repository;

import com.research.ems.entity.Payroll;

import com.research.ems.entity.enums.PayrollStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface PayrollRepository
        extends JpaRepository<Payroll, Long> {

    List<Payroll> findByEmployeeId(
            Long employeeId
    );

    Page<Payroll> findByEmployeeId(
            Long employeeId,
            Pageable pageable
    );

    Optional<Payroll> findByEmployeeIdAndPayPeriodStartAndPayPeriodEnd(
            Long employeeId,
            LocalDate payPeriodStart,
            LocalDate payPeriodEnd
    );

    List<Payroll> findByStatus(
            PayrollStatus status
    );

    Page<Payroll> findByStatus(
            PayrollStatus status,
            Pageable pageable
    );

    List<Payroll> findByEmployeeIdAndStatus(
            Long employeeId,
            PayrollStatus status
    );

    boolean existsByEmployeeIdAndPayPeriodStartAndPayPeriodEnd(
            Long employeeId,
            LocalDate payPeriodStart,
            LocalDate payPeriodEnd
    );

    boolean existsByEmployeeIdAndPayPeriodStartAndPayPeriodEndAndIdNot(
            Long employeeId,
            LocalDate payPeriodStart,
            LocalDate payPeriodEnd,
            Long id
    );

    @Query("select coalesce(sum(p.netSalary), 0) from Payroll p")
    BigDecimal sumNetSalary();
}