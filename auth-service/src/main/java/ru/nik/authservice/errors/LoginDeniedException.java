package ru.nik.authservice.errors;

public class LoginDeniedException extends RuntimeException{
    public LoginDeniedException(String message) {
        super(message);
    }
}
