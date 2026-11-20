package com.research.ems.exception.employeedocument;

public class DuplicateEmployeeDocumentException extends RuntimeException {

    public DuplicateEmployeeDocumentException(String message) {
        super(message);
    }
}