package com.agriseva.user.exception;

public class PhoneNumberAlreadyExistsException extends RuntimeException {

    public PhoneNumberAlreadyExistsException(String phoneNumber) {
        super("An account already exists with phone number: " + phoneNumber);
    }
}