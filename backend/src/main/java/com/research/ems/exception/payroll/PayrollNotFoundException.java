package com.research.ems.exception.payroll;

public class PayrollNotFoundException extends RuntimeException {

    public PayrollNotFoundException(String message) {
        super(message);
    }
}