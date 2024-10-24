package com.connect.ClientManagement.exceptions;

public class EmailAndPhoneAlreadyExistsException extends RuntimeException  {
    public EmailAndPhoneAlreadyExistsException(String message) {
        super(message);
    }
}