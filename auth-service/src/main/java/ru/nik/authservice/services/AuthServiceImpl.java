package ru.nik.authservice.services;

import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import ru.nik.authservice.errors.AlreadyExistsException;
import ru.nik.authservice.errors.LoginDeniedException;
import ru.nik.authservice.managers.TokenManager;
import ru.nik.authservice.managers.TotpManager;
import ru.nik.authservice.model.external.LoginRequest;
import ru.nik.authservice.model.external.LoginResponse;
import ru.nik.authservice.model.external.SignupRequest;
import ru.nik.authservice.model.external.SignupResponse;
import ru.nik.authservice.model.internal.User;
import ru.nik.authservice.repositories.UserRepository;


@Component
public class AuthServiceImpl implements AuthService {

    private final TokenManager tokenManager;
    private final TotpManager totpManager;
    private final UserRepository repository;

    public AuthServiceImpl(TokenManager tokenManager, TotpManager totpManager, UserRepository repository) {
        this.tokenManager = tokenManager;
        this.totpManager = totpManager;
        this.repository = repository;
    }

    @Override
    public Mono<SignupResponse> signup(SignupRequest request) {
        String email = request.getEmail().trim().toLowerCase();
        return repository.findByEmail(email)
                .defaultIfEmpty(createNewUser(request))
                .flatMap(user -> {
                    if (user.getUserId() == null) {
                        return repository.save(user)
                                .flatMap(savedUser -> {
                                    String userId = savedUser.getUserId();
                                    String token = tokenManager.issueToken(userId);
                                    SignupResponse signupResponse = new SignupResponse(userId, token, savedUser.getSecretKey());
                                    return Mono.just(signupResponse);
                                });
                    } else {
                        return Mono.error(new AlreadyExistsException());
                    }
                });
    }

    private User createNewUser(SignupRequest request) {
        String email = request.getEmail().trim().toLowerCase();
        String password = request.getPassword();
        String salt = BCrypt.gensalt();
        String hash = BCrypt.hashpw(password, salt);
        String secret = totpManager.generateSecret();
        return new User(null, email, hash, salt, secret);
    }

    @Override
    public Mono<LoginResponse> login(LoginRequest request) {
        String email = request.getEmail().trim().toLowerCase();
        String password = request.getPassword();
        String code = request.getCode();
        return repository.findByEmail(email)
                .flatMap(user -> {
                    String salt = user.getSalt();
                    String secret = user.getSecretKey();
                    boolean passwordMatch = BCrypt.hashpw(password, salt).equalsIgnoreCase(user.getHash());
                    if (passwordMatch) {
                        // password matched
                        boolean codeMatched = totpManager.validateCode(code, secret);
                        if (codeMatched) {
                            String token = tokenManager.issueToken(user.getUserId());
                            LoginResponse loginResponse = new LoginResponse();
                            loginResponse.setToken(token);
                            loginResponse.setUserId(user.getUserId());
                            return Mono.just(loginResponse);
                        } else {
                            return Mono.error(new LoginDeniedException());
                        }
                    } else {
                        return Mono.error(new LoginDeniedException());
                    }
                });
    }

    @Override
    public Mono<String> parseToken(String token) {
        return tokenManager.parse(token);
    }
}
