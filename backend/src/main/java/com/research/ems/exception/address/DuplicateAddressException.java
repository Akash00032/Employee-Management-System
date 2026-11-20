package com.research.ems.exception.address;

public class DuplicateAddressException extends RuntimeException {

    public DuplicateAddressException(String message) {
        super(message);
    }
}