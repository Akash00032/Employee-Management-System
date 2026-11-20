package com.research.ems.exception.address;

public class AddressNotFoundException extends RuntimeException {

    public AddressNotFoundException(String message) {
        super(message);
    }
}