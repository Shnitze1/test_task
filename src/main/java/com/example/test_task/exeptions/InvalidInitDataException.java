package com.example.test_task.exeptions;

public class InvalidInitDataException extends Exception {
    public InvalidInitDataException(String message) {
        super(message);
    }
}