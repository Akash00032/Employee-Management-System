package com.research.ems.exception.attendance;

public class AttendanceNotFoundException extends RuntimeException {

    public AttendanceNotFoundException(String message) {
        super(message);
    }
}