package ru.nik.authservice.model.internal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Table
public class User {

    @Id
    private String userId;
    private String email;
    private String hash;
    private String salt;
    private String secretKey;

}
