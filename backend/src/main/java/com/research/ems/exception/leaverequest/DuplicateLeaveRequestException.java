package com.research.ems.exception.leaverequest;

public class DuplicateLeaveRequestException extends RuntimeException {

    public DuplicateLeaveRequestException(String message) {
        super(message);
    }
}