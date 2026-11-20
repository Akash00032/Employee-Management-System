package com.research.ems.service.impl;

import com.research.ems.entity.Employee;
import com.research.ems.entity.LeaveRequest;
import com.research.ems.entity.LeaveType;
import com.research.ems.entity.UserAccount;

import com.research.ems.dto.leaverequest.LeaveRequestDto;
import com.research.ems.dto.leaverequest.LeaveRequestResponseDto;
import com.research.ems.entity.enums.LeaveStatus;
import com.research.ems.entity.enums.Role;
import com.research.ems.exception.employee.EmployeeNotFoundException;
import com.research.ems.exception.useraccount.UserAccountNotFoundException;
import com.research.ems.mapper.LeaveRequestMapper;
import com.research.ems.repository.EmployeeRepository;
import com.research.ems.repository.LeaveRequestRepository;
import com.research.ems.repository.LeaveTypeRepository;
import com.research.ems.repository.UserAccountRepository;
import com.research.ems.service.LeaveRequestService;
import org.springframework.stereotype.Service;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class LeaveRequestServiceImpl implements LeaveRequestService {

    private final LeaveRequestRepository leaveRequestRepository;
    private final EmployeeRepository employeeRepository;
    private final LeaveTypeRepository leaveTypeRepository;
    private final UserAccountRepository userAccountRepository;
    private final LeaveRequestMapper leaveRequestMapper;

    public LeaveRequestServiceImpl(
            LeaveRequestRepository leaveRequestRepository,
            EmployeeRepository employeeRepository,
            LeaveTypeRepository leaveTypeRepository,
            UserAccountRepository userAccountRepository,
            LeaveRequestMapper leaveRequestMapper) {

        this.leaveRequestRepository = leaveRequestRepository;
        this.employeeRepository = employeeRepository;
        this.leaveTypeRepository = leaveTypeRepository;
        this.userAccountRepository = userAccountRepository;
        this.leaveRequestMapper = leaveRequestMapper;
    }

    @Override
    public LeaveRequestResponseDto createLeaveRequest(
            LeaveRequestDto dto) {

        Employee employee = findEmployee(dto.getEmployeeId());
        LeaveType leaveType = findLeaveType(dto.getLeaveTypeId());

        LeaveRequest request =
                leaveRequestMapper.toEntity(dto);

        request.setEmployee(employee);
        request.setLeaveType(leaveType);
        request.setStatus(LeaveStatus.PENDING);
        request.setAppliedAt(LocalDateTime.now());

        return leaveRequestMapper.toResponseDto(
                leaveRequestRepository.save(request));
    }

    @Override
    public LeaveRequestResponseDto createMyLeaveRequest(
            String username,
            com.research.ems.dto.leaverequest.MyLeaveRequestDto dto) {
        Employee employee = findCurrentEmployee(username);
        return createLeaveRequest(new LeaveRequestDto(
                employee.getId(),
                dto.leaveTypeId(),
                dto.startDate(),
                dto.endDate(),
                dto.reason()));
    }

    @Override
    @Transactional(readOnly = true)
    public LeaveRequestResponseDto getLeaveRequestById(Long id) {

        return leaveRequestMapper.toResponseDto(findRequest(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<LeaveRequestResponseDto> getAllLeaveRequests() {

        return leaveRequestRepository.findAll()
                .stream()
                .map(leaveRequestMapper::toResponseDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<LeaveRequestResponseDto> getEmployeeLeaveRequests(
            Long employeeId) {

        findEmployee(employeeId);

        return leaveRequestRepository
                .findByEmployeeId(employeeId)
                .stream()
                .map(leaveRequestMapper::toResponseDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<LeaveRequestResponseDto> getLeaveRequestsByStatus(
            LeaveStatus status) {

        return leaveRequestRepository
                .findByStatus(status)
                .stream()
                .map(leaveRequestMapper::toResponseDto)
                .toList();
    }

    @Override
    public LeaveRequestResponseDto updateLeaveRequest(
            Long id,
            LeaveRequestDto dto) {

        LeaveRequest request = findRequest(id);
        if (request.getStatus() != LeaveStatus.PENDING) {
            throw new IllegalStateException(
                    "Only pending leave requests can be updated");
        }

        Employee employee = findEmployee(dto.getEmployeeId());
        LeaveType leaveType = findLeaveType(dto.getLeaveTypeId());
        leaveRequestMapper.updateEntity(request, dto);
        request.setEmployee(employee);
        request.setLeaveType(leaveType);
        return leaveRequestMapper.toResponseDto(
                leaveRequestRepository.save(request));
    }

    @Override
    public LeaveRequestResponseDto approveLeaveRequest(
            Long id,
            String approverUsername) {

        LeaveRequest request = findRequest(id);
        if (request.getStatus() != LeaveStatus.PENDING) {
            throw new IllegalStateException(
                    "Only pending leave requests can be approved");
        }
        Employee approver = findApprover(request, approverUsername);

        request.setStatus(LeaveStatus.APPROVED);
        request.setApprovedBy(approver);
        request.setApprovedAt(
                java.time.LocalDateTime.now());
        request.setRejectionReason(null);

        return leaveRequestMapper.toResponseDto(
                leaveRequestRepository.save(request));
    }

    @Override
    public LeaveRequestResponseDto rejectLeaveRequest(
            Long id,
            String approverUsername,
            String rejectionReason) {

        LeaveRequest request = findRequest(id);
        if (request.getStatus() != LeaveStatus.PENDING) {
            throw new IllegalStateException(
                    "Only pending leave requests can be rejected");
        }
        Employee approver = findApprover(request, approverUsername);

        request.setStatus(LeaveStatus.REJECTED);
        request.setApprovedBy(approver);
        request.setApprovedAt(
                java.time.LocalDateTime.now());
        request.setRejectionReason(rejectionReason);

        return leaveRequestMapper.toResponseDto(
                leaveRequestRepository.save(request));
    }

    @Override
    public void deleteLeaveRequest(Long id) {

        leaveRequestRepository.delete(findRequest(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<LeaveRequestResponseDto> getMyLeaveRequests(String username) {
        Employee employee = findCurrentEmployee(username);
        return leaveRequestRepository.findByEmployeeId(employee.getId())
                .stream()
                .map(leaveRequestMapper::toResponseDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<LeaveRequestResponseDto> getTeamLeaveRequests(
            String managerUsername,
            LeaveStatus status) {
        UserAccount managerAccount = findUserAccount(managerUsername);
        if (managerAccount.getRole() != Role.MANAGER) {
            throw new AccessDeniedException("Manager role is required");
        }

        Long managerId = managerAccount.getEmployee().getId();
        return leaveRequestRepository.findByEmployeeManagerId(managerId)
                .stream()
                .filter(request -> status == null || request.getStatus() == status)
                .map(leaveRequestMapper::toResponseDto)
                .toList();
    }

    @Override
    public void cancelMyLeaveRequest(Long id, String username) {
        LeaveRequest request = findRequest(id);
        Employee employee = findCurrentEmployee(username);
        if (!request.getEmployee().getId().equals(employee.getId())) {
            throw new AccessDeniedException(
                    "Leave request does not belong to the current user");
        }
        if (request.getStatus() != LeaveStatus.PENDING) {
            throw new IllegalStateException(
                    "Only pending leave requests can be cancelled");
        }
        request.setStatus(LeaveStatus.CANCELLED);
        leaveRequestRepository.save(request);
    }

    private Employee findApprover(
            LeaveRequest request,
            String username) {
        UserAccount account = findUserAccount(username);
        Employee approver = account.getEmployee();
        if (account.getRole() == Role.ADMIN || account.getRole() == Role.HR) {
            return approver;
        }
        if (account.getRole() == Role.MANAGER
                && request.getEmployee().getManager() != null
                && request.getEmployee().getManager().getId()
                        .equals(approver.getId())) {
            return approver;
        }
        throw new AccessDeniedException(
                "You are not authorized to approve this leave request");
    }

    private Employee findCurrentEmployee(String username) {
        Employee employee = findUserAccount(username).getEmployee();
        if (employee == null) {
            throw new EmployeeNotFoundException(
                    "No employee profile is linked to the current account");
        }
        return employee;
    }

    private UserAccount findUserAccount(String username) {
        return userAccountRepository
                .findByUsernameIgnoreCase(username)
                .orElseThrow(() -> new UserAccountNotFoundException(
                        "User account not found"));
    }

    private LeaveRequest findRequest(Long id) {

        return leaveRequestRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Leave request not found with id: " + id));
    }

    private Employee findEmployee(Long id) {

        return employeeRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Employee not found with id: " + id));
    }

    private LeaveType findLeaveType(Long id) {

        return leaveTypeRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Leave type not found with id: " + id));
    }
}