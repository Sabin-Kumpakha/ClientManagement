package com.connect.ClientManagement.exceptions;

public class EmailAlreadyExistsException extends RuntimeException  {
    public EmailAlreadyExistsException(String message) {
        super(message);
    }
}