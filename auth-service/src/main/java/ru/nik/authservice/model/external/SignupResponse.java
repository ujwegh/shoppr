package ru.nik.authservice.model.external;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SignupResponse {

//    private boolean success;
    private String userId;
    private String token;
    private String secretKey;

}
