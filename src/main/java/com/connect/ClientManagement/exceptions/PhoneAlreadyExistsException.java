package com.connect.ClientManagement.exceptions;

public class PhoneAlreadyExistsException extends RuntimeException  {
    public PhoneAlreadyExistsException(String message) {
        super(message);
    }
}