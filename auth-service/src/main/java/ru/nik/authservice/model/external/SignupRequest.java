package ru.nik.authservice.model.external;

import lombok.Data;

@Data
public class SignupRequest {

    private String email;
    private String password;

}
