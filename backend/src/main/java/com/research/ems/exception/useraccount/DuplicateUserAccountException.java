package com.research.ems.exception.useraccount;

public class DuplicateUserAccountException extends RuntimeException {

    public DuplicateUserAccountException(String message) {
        super(message);
    }
}