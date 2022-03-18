package ru.nik.authservice.managers;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.gen.RSAKeyGenerator;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import ru.nik.authservice.errors.InvalidTokenException;

import java.security.interfaces.RSAPublicKey;
import java.util.UUID;

@Slf4j
@Component
public class TokenManagerImpl implements TokenManager{

    private final RSAKey key;

    public TokenManagerImpl() throws Exception{
        key =  new RSAKeyGenerator(2048)
                .keyID(UUID.randomUUID().toString())
                .generate();
    }

    @Override
    public String issueToken(String userId) {
        try {
            JWSSigner signer = new RSASSASigner(key);
            JWTClaimsSet cs = new JWTClaimsSet.Builder().subject(userId).build();
            SignedJWT signedJWT = new SignedJWT(new JWSHeader.Builder(JWSAlgorithm.RS256).keyID(key.getKeyID()).build(), cs);
            signedJWT.sign(signer);
            return signedJWT.serialize();

        } catch (Exception ex){
            log.warn("Failed to issue token", ex);
            return null;
        }
    }

    @Override
    public Mono<String> parse(String token) {
        try {
            SignedJWT signedJWT = SignedJWT.parse(token);
            RSAPublicKey publicKey = key.toRSAPublicKey();
            JWSVerifier verifier = new RSASSAVerifier(publicKey);
            boolean success = signedJWT.verify(verifier);
            if (success){
                String userId = signedJWT.getJWTClaimsSet().getSubject();
                return Mono.just(userId);
            } else {
                return Mono.error(InvalidTokenException::new);
            }
        } catch (Exception ex){
            return Mono.error(InvalidTokenException::new);
        }
    }
}
