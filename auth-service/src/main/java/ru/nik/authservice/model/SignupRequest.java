package ru.nik.authservice.model;

import lombok.Data;

@Data
public class SignupRequest {

    private String email;
    private String password;

}
