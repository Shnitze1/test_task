package com.example.test_task.exeptions;

public class UserDataProcessingException extends Exception {
    public UserDataProcessingException(String message) {
        super(message);
    }

    public UserDataProcessingException(String message, Throwable cause) {
        super(message, cause);
    }
}