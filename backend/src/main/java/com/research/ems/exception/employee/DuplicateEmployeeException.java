package com.research.ems.exception.employee;

public class DuplicateEmployeeException extends  RuntimeException {

    public DuplicateEmployeeException(String message) {
        super(message);
    }
}
