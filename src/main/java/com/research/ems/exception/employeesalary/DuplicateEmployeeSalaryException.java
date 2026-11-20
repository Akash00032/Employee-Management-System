package com.research.ems.exception.employeesalary;

public class DuplicateEmployeeSalaryException extends RuntimeException {

    public DuplicateEmployeeSalaryException(String message) {
        super(message);
    }
}