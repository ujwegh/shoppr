package ru.nik.authservice.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {

    @Id
    private String userId;
    private String email;
    private String hash;
    private String salt;
    private String secretKey;

}
