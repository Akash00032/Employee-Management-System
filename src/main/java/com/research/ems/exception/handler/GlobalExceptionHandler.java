package com.research.ems.exception.handler;

import com.research.ems.entity.Address;
import com.research.ems.entity.Attendance;
import com.research.ems.entity.Department;
import com.research.ems.entity.Employee;
import com.research.ems.entity.Payroll;
import com.research.ems.entity.Project;


import com.research.ems.dto.common.ApiResponseDto;
import com.research.ems.exception.address.AddressNotFoundException;
import com.research.ems.exception.address.DuplicateAddressException;
import com.research.ems.exception.attendance.AttendanceNotFoundException;
import com.research.ems.exception.attendance.DuplicateAttendanceException;
import com.research.ems.exception.auth.InvalidPasswordException;
import com.research.ems.exception.auth.UnauthorizedException;
import com.research.ems.exception.common.BadRequestException;
import com.research.ems.exception.common.ResourceNotFoundException;
import com.research.ems.exception.department.DepartmentNotFoundException;
import com.research.ems.exception.department.DuplicateDepartmentException;
import com.research.ems.exception.employee.DuplicateEmployeeException;
import com.research.ems.exception.employee.EmployeeNotFoundException;
import com.research.ems.exception.employeeasset.DuplicateEmployeeAssetException;
import com.research.ems.exception.employeeasset.EmployeeAssetNotFoundException;
import com.research.ems.exception.employeedocument.DuplicateEmployeeDocumentException;
import com.research.ems.exception.employeedocument.EmployeeDocumentNotFoundException;
import com.research.ems.exception.employeeproject.EmployeeProjectNotFoundException;
import com.research.ems.exception.employeesalary.DuplicateEmployeeSalaryException;
import com.research.ems.exception.employeesalary.EmployeeSalaryNotFoundException;
import com.research.ems.exception.leaverequest.DuplicateLeaveRequestException;
import com.research.ems.exception.leaverequest.LeaveRequestNotFoundException;
import com.research.ems.exception.leavetype.DuplicateLeaveTypeException;
import com.research.ems.exception.leavetype.LeaveTypeNotFoundException;
import com.research.ems.exception.payroll.DuplicatePayrollException;
import com.research.ems.exception.payroll.PayrollNotFoundException;
import com.research.ems.exception.performancereview.DuplicatePerformanceReviewException;
import com.research.ems.exception.performancereview.PerformanceReviewNotFoundException;
import com.research.ems.exception.employeeproject.DuplicateEmployeeProjectException;
import com.research.ems.exception.project.DuplicateProjectException;
import com.research.ems.exception.project.ProjectNotFoundException;
import com.research.ems.exception.salarycomponent.DuplicateSalaryComponentException;
import com.research.ems.exception.salarycomponent.SalaryComponentNotFoundException;
import com.research.ems.exception.useraccount.DuplicateUserAccountException;
import com.research.ems.exception.useraccount.UserAccountNotFoundException;
import com.research.ems.exception.workshift.DuplicateWorkShiftException;
import com.research.ems.exception.workshift.WorkShiftNotFoundException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import jakarta.validation.ConstraintViolationException;

import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // --------------------------------------------------
    // Validation
    // --------------------------------------------------

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponseDto<Map<String, String>>>
    handleValidationException(
            MethodArgumentNotValidException exception) {

        Map<String, String> errors = new LinkedHashMap<>();

        for (FieldError error :
                exception.getBindingResult().getFieldErrors()) {

            errors.put(
                    error.getField(),
                    error.getDefaultMessage()
            );
        }

        ApiResponseDto<Map<String, String>> response =
                ApiResponseDto.error(
                        "Validation failed"
                );

        response.setData(errors);

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(response);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiResponseDto<Void>>
    handleConstraintViolation(
            ConstraintViolationException exception) {

        return buildResponse(
                HttpStatus.BAD_REQUEST,
                exception.getMessage()
        );
    }

    // --------------------------------------------------
    // Employee
    // --------------------------------------------------

    @ExceptionHandler(EmployeeNotFoundException.class)
    public ResponseEntity<ApiResponseDto<Void>>
    handleEmployeeNotFound(
            EmployeeNotFoundException exception) {

        return buildResponse(
                HttpStatus.NOT_FOUND,
                exception.getMessage()
        );
    }

    @ExceptionHandler(DuplicateEmployeeException.class)
    public ResponseEntity<ApiResponseDto<Void>>
    handleDuplicateEmployee(
            DuplicateEmployeeException exception) {

        return buildResponse(
                HttpStatus.CONFLICT,
                exception.getMessage()
        );
    }

    // --------------------------------------------------
    // Department
    // --------------------------------------------------

    @ExceptionHandler(DepartmentNotFoundException.class)
    public ResponseEntity<ApiResponseDto<Void>>
    handleDepartmentNotFound(
            DepartmentNotFoundException exception) {

        return buildResponse(
                HttpStatus.NOT_FOUND,
                exception.getMessage()
        );
    }

    @ExceptionHandler(DuplicateDepartmentException.class)
    public ResponseEntity<ApiResponseDto<Void>>
    handleDuplicateDepartment(
            DuplicateDepartmentException exception) {

        return buildResponse(
                HttpStatus.CONFLICT,
                exception.getMessage()
        );
    }

    // --------------------------------------------------
    // Work Shift
    // --------------------------------------------------

    @ExceptionHandler(WorkShiftNotFoundException.class)
    public ResponseEntity<ApiResponseDto<Void>>
    handleWorkShiftNotFound(
            WorkShiftNotFoundException exception) {

        return buildResponse(
                HttpStatus.NOT_FOUND,
                exception.getMessage()
        );
    }

    @ExceptionHandler(DuplicateWorkShiftException.class)
    public ResponseEntity<ApiResponseDto<Void>>
    handleDuplicateWorkShift(
            DuplicateWorkShiftException exception) {

        return buildResponse(
                HttpStatus.CONFLICT,
                exception.getMessage()
        );
    }

    // --------------------------------------------------
    // Attendance
    // --------------------------------------------------

    @ExceptionHandler(AttendanceNotFoundException.class)
    public ResponseEntity<ApiResponseDto<Void>>
    handleAttendanceNotFound(
            AttendanceNotFoundException exception) {

        return buildResponse(
                HttpStatus.NOT_FOUND,
                exception.getMessage()
        );
    }

    @ExceptionHandler(DuplicateAttendanceException.class)
    public ResponseEntity<ApiResponseDto<Void>>
    handleDuplicateAttendance(
            DuplicateAttendanceException exception) {

        return buildResponse(
                HttpStatus.CONFLICT,
                exception.getMessage()
        );
    }

    // --------------------------------------------------
    // Leave Type
    // --------------------------------------------------

    @ExceptionHandler(LeaveTypeNotFoundException.class)
    public ResponseEntity<ApiResponseDto<Void>>
    handleLeaveTypeNotFound(
            LeaveTypeNotFoundException exception) {

        return buildResponse(
                HttpStatus.NOT_FOUND,
                exception.getMessage()
        );
    }

    @ExceptionHandler(DuplicateLeaveTypeException.class)
    public ResponseEntity<ApiResponseDto<Void>>
    handleDuplicateLeaveType(
            DuplicateLeaveTypeException exception) {

        return buildResponse(
                HttpStatus.CONFLICT,
                exception.getMessage()
        );
    }

    // --------------------------------------------------
    // Leave Request
    // --------------------------------------------------

    @ExceptionHandler(LeaveRequestNotFoundException.class)
    public ResponseEntity<ApiResponseDto<Void>>
    handleLeaveRequestNotFound(
            LeaveRequestNotFoundException exception) {

        return buildResponse(
                HttpStatus.NOT_FOUND,
                exception.getMessage()
        );
    }

    @ExceptionHandler(DuplicateLeaveRequestException.class)
    public ResponseEntity<ApiResponseDto<Void>>
    handleDuplicateLeaveRequest(
            DuplicateLeaveRequestException exception) {

        return buildResponse(
                HttpStatus.CONFLICT,
                exception.getMessage()
        );
    }

    // --------------------------------------------------
    // Salary Component
    // --------------------------------------------------

    @ExceptionHandler(SalaryComponentNotFoundException.class)
    public ResponseEntity<ApiResponseDto<Void>>
    handleSalaryComponentNotFound(
            SalaryComponentNotFoundException exception) {

        return buildResponse(
                HttpStatus.NOT_FOUND,
                exception.getMessage()
        );
    }

    @ExceptionHandler(DuplicateSalaryComponentException.class)
    public ResponseEntity<ApiResponseDto<Void>>
    handleDuplicateSalaryComponent(
            DuplicateSalaryComponentException exception) {

        return buildResponse(
                HttpStatus.CONFLICT,
                exception.getMessage()
        );
    }

    // --------------------------------------------------
    // Employee Salary
    // --------------------------------------------------

    @ExceptionHandler(EmployeeSalaryNotFoundException.class)
    public ResponseEntity<ApiResponseDto<Void>>
    handleEmployeeSalaryNotFound(
            EmployeeSalaryNotFoundException exception) {

        return buildResponse(
                HttpStatus.NOT_FOUND,
                exception.getMessage()
        );
    }

    @ExceptionHandler(DuplicateEmployeeSalaryException.class)
    public ResponseEntity<ApiResponseDto<Void>>
    handleDuplicateEmployeeSalary(
            DuplicateEmployeeSalaryException exception) {

        return buildResponse(
                HttpStatus.CONFLICT,
                exception.getMessage()
        );
    }

    // --------------------------------------------------
    // Payroll
    // --------------------------------------------------

    @ExceptionHandler(PayrollNotFoundException.class)
    public ResponseEntity<ApiResponseDto<Void>>
    handlePayrollNotFound(
            PayrollNotFoundException exception) {

        return buildResponse(
                HttpStatus.NOT_FOUND,
                exception.getMessage()
        );
    }

    @ExceptionHandler(DuplicatePayrollException.class)
    public ResponseEntity<ApiResponseDto<Void>>
    handleDuplicatePayroll(
            DuplicatePayrollException exception) {

        return buildResponse(
                HttpStatus.CONFLICT,
                exception.getMessage()
        );
    }

    // --------------------------------------------------
    // Performance Review
    // --------------------------------------------------

    @ExceptionHandler(PerformanceReviewNotFoundException.class)
    public ResponseEntity<ApiResponseDto<Void>>
    handlePerformanceReviewNotFound(
            PerformanceReviewNotFoundException exception) {

        return buildResponse(
                HttpStatus.NOT_FOUND,
                exception.getMessage()
        );
    }

    @ExceptionHandler(DuplicatePerformanceReviewException.class)
    public ResponseEntity<ApiResponseDto<Void>>
    handleDuplicatePerformanceReview(
            DuplicatePerformanceReviewException exception) {

        return buildResponse(
                HttpStatus.CONFLICT,
                exception.getMessage()
        );
    }

    // --------------------------------------------------
    // Project
    // --------------------------------------------------

    @ExceptionHandler(ProjectNotFoundException.class)
    public ResponseEntity<ApiResponseDto<Void>>
    handleProjectNotFound(
            ProjectNotFoundException exception) {

        return buildResponse(
                HttpStatus.NOT_FOUND,
                exception.getMessage()
        );
    }

    @ExceptionHandler(DuplicateProjectException.class)
    public ResponseEntity<ApiResponseDto<Void>>
    handleDuplicateProject(
            DuplicateProjectException exception) {

        return buildResponse(
                HttpStatus.CONFLICT,
                exception.getMessage()
        );
    }

    // --------------------------------------------------
    // Employee Project
    // --------------------------------------------------

    @ExceptionHandler(EmployeeProjectNotFoundException.class)
    public ResponseEntity<ApiResponseDto<Void>>
    handleEmployeeProjectNotFound(
            EmployeeProjectNotFoundException exception) {

        return buildResponse(
                HttpStatus.NOT_FOUND,
                exception.getMessage()
        );
    }

    @ExceptionHandler(DuplicateEmployeeProjectException.class)
    public ResponseEntity<ApiResponseDto<Void>>
    handleDuplicateEmployeeProject(
            DuplicateEmployeeProjectException exception) {

        return buildResponse(
                HttpStatus.CONFLICT,
                exception.getMessage()
        );
    }

    // --------------------------------------------------
    // Employee Document
    // --------------------------------------------------

    @ExceptionHandler(EmployeeDocumentNotFoundException.class)
    public ResponseEntity<ApiResponseDto<Void>>
    handleEmployeeDocumentNotFound(
            EmployeeDocumentNotFoundException exception) {

        return buildResponse(
                HttpStatus.NOT_FOUND,
                exception.getMessage()
        );
    }

    @ExceptionHandler(DuplicateEmployeeDocumentException.class)
    public ResponseEntity<ApiResponseDto<Void>>
    handleDuplicateEmployeeDocument(
            DuplicateEmployeeDocumentException exception) {

        return buildResponse(
                HttpStatus.CONFLICT,
                exception.getMessage()
        );
    }

    // --------------------------------------------------
    // Employee Asset
    // --------------------------------------------------

    @ExceptionHandler(EmployeeAssetNotFoundException.class)
    public ResponseEntity<ApiResponseDto<Void>>
    handleEmployeeAssetNotFound(
            EmployeeAssetNotFoundException exception) {

        return buildResponse(
                HttpStatus.NOT_FOUND,
                exception.getMessage()
        );
    }

    @ExceptionHandler(DuplicateEmployeeAssetException.class)
    public ResponseEntity<ApiResponseDto<Void>>
    handleDuplicateEmployeeAsset(
            DuplicateEmployeeAssetException exception) {

        return buildResponse(
                HttpStatus.CONFLICT,
                exception.getMessage()
        );
    }

    // --------------------------------------------------
    // Address
    // --------------------------------------------------

    @ExceptionHandler(AddressNotFoundException.class)
    public ResponseEntity<ApiResponseDto<Void>>
    handleAddressNotFound(
            AddressNotFoundException exception) {

        return buildResponse(
                HttpStatus.NOT_FOUND,
                exception.getMessage()
        );
    }

    @ExceptionHandler(DuplicateAddressException.class)
    public ResponseEntity<ApiResponseDto<Void>>
    handleDuplicateAddress(
            DuplicateAddressException exception) {

        return buildResponse(
                HttpStatus.CONFLICT,
                exception.getMessage()
        );
    }

    // --------------------------------------------------
    // User Account
    // --------------------------------------------------

    @ExceptionHandler(UserAccountNotFoundException.class)
    public ResponseEntity<ApiResponseDto<Void>>
    handleUserAccountNotFound(
            UserAccountNotFoundException exception) {

        return buildResponse(
                HttpStatus.NOT_FOUND,
                exception.getMessage()
        );
    }

    @ExceptionHandler(DuplicateUserAccountException.class)
    public ResponseEntity<ApiResponseDto<Void>>
    handleDuplicateUserAccount(
            DuplicateUserAccountException exception) {

        return buildResponse(
                HttpStatus.CONFLICT,
                exception.getMessage()
        );
    }

    // --------------------------------------------------
    // Authentication
    // --------------------------------------------------

    @ExceptionHandler(InvalidPasswordException.class)
    public ResponseEntity<ApiResponseDto<Void>>
    handleInvalidPassword(
            InvalidPasswordException exception) {

        return buildResponse(
                HttpStatus.BAD_REQUEST,
                exception.getMessage()
        );
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ApiResponseDto<Void>>
    handleUnauthorized(
            UnauthorizedException exception) {

        return buildResponse(
                HttpStatus.UNAUTHORIZED,
                exception.getMessage()
        );
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponseDto<Void>> handleAccessDenied(
            AccessDeniedException exception) {
        return buildResponse(HttpStatus.FORBIDDEN, exception.getMessage());
    }

    // --------------------------------------------------
    // Generic application exceptions
    // --------------------------------------------------

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponseDto<Void>>
    handleResourceNotFound(
            ResourceNotFoundException exception) {

        return buildResponse(
                HttpStatus.NOT_FOUND,
                exception.getMessage()
        );
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ApiResponseDto<Void>>
    handleBadRequest(
            BadRequestException exception) {

        return buildResponse(
                HttpStatus.BAD_REQUEST,
                exception.getMessage()
        );
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponseDto<Void>>
    handleIllegalArgument(
            IllegalArgumentException exception) {

        return buildResponse(
                HttpStatus.BAD_REQUEST,
                exception.getMessage()
        );
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ApiResponseDto<Void>> handleIllegalState(
            IllegalStateException exception) {
        return buildResponse(HttpStatus.CONFLICT, exception.getMessage());
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponseDto<Void>> handleMalformedJson(
            HttpMessageNotReadableException exception) {
        return buildResponse(HttpStatus.BAD_REQUEST, "Malformed request body");
    }

    // --------------------------------------------------
    // Database exceptions
    // --------------------------------------------------

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiResponseDto<Void>>
    handleDataIntegrityViolation(
            DataIntegrityViolationException exception) {

        return buildResponse(
                HttpStatus.CONFLICT,
                "Database constraint violation"
        );
    }

    // --------------------------------------------------
    // Fallback
    // --------------------------------------------------

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponseDto<Void>>
    handleGenericException(Exception exception) {

        return buildResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "An unexpected error occurred"
        );
    }

    // --------------------------------------------------
    // Helper
    // --------------------------------------------------

    private ResponseEntity<ApiResponseDto<Void>> buildResponse(
            HttpStatus status,
            String message) {

        ApiResponseDto<Void> response =
                ApiResponseDto.error(message);

        return ResponseEntity
                .status(status)
                .body(response);
    }
}