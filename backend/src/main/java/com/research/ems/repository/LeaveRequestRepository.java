package com.research.ems.repository;

import com.research.ems.entity.LeaveRequest;

import com.research.ems.entity.enums.LeaveStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface LeaveRequestRepository
        extends JpaRepository<LeaveRequest, Long> {

    List<LeaveRequest> findByEmployeeId(
            Long employeeId
    );

    List<LeaveRequest> findByEmployeeManagerId(Long managerId);

    Page<LeaveRequest> findByEmployeeId(
            Long employeeId,
            Pageable pageable
    );

    List<LeaveRequest> findByEmployeeIdAndStatus(
            Long employeeId,
            LeaveStatus status
    );

    Page<LeaveRequest> findByStatus(
            LeaveStatus status,
            Pageable pageable
    );

    List<LeaveRequest> findByStatus(LeaveStatus status);

    long countByStatus(LeaveStatus status);

    long countByStatusAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
            LeaveStatus status,
            LocalDate dateStart,
            LocalDate dateEnd
    );

    List<LeaveRequest> findByEmployeeIdAndStartDateBetween(
            Long employeeId,
            LocalDate startDate,
            LocalDate endDate
    );

    List<LeaveRequest> findByApprovedById(
            Long employeeId
    );

    long countByEmployeeIdAndStatus(
            Long employeeId,
            LeaveStatus status
    );
}
