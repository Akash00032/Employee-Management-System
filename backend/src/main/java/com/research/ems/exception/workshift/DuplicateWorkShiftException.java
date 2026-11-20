package com.research.ems.exception.workshift;

public class DuplicateWorkShiftException extends RuntimeException {

    public DuplicateWorkShiftException(String message) {
        super(message);
    }
}