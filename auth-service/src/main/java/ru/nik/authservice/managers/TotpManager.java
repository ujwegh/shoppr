package ru.nik.authservice.managers;

public interface TotpManager {

    String generateSecret ();

    boolean validateCode (String code, String secret);

}
