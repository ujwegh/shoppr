package ru.nik.authservice.model.external;

import lombok.Data;

@Data
public class LoginResponse {

    private boolean success;
    private String userId;
    private String token;

}
