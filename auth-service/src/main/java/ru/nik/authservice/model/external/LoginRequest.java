package ru.nik.authservice.model.external;

import lombok.Data;

@Data
public class LoginRequest {

    private String email;
    private String password;
    private String code;

}
