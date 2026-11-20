package com.research.ems.exception.attendance;

public class DuplicateAttendanceException extends RuntimeException {

    public DuplicateAttendanceException(String message) {
        super(message);
    }
}