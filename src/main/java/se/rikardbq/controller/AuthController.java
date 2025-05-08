package se.rikardbq.controller;

import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import jakarta.websocket.server.PathParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import se.rikardbq.exception.SerfConnectorException;
import se.rikardbq.models.User;
import se.rikardbq.models.auth.AuthRequest;
import se.rikardbq.models.auth.AuthResponse;
import se.rikardbq.service.IAuthService;
import se.rikardbq.service.IUserService;
import se.rikardbq.util.Env;
import se.rikardbq.util.Token;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.Objects;

@RestController
public class AuthController {

    @Autowired
    private IAuthService<DecodedJWT> authService;
    @Autowired
    private IUserService<User> userService;

    @PostMapping("/authenticate")
    public ResponseEntity<AuthResponse> authenticate(@RequestHeader Map<String, String> requestHeaders, @RequestBody AuthRequest authRequest) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        try {
            if (!authService.checkApiKeyValid(requestHeaders)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            User user = userService.getUserWithUsername(authRequest.getUsername());
            if (Objects.isNull(user) || !userService.checkUserCredentialsValid(user, authRequest.getPassword())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }

            String accessToken = authService.generateToken(
                    Token.Type.AT,
                    authRequest.getUsername(),
                    authRequest.getApplicationId(),
                    Env.FFBE_S
            );

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(new AuthResponse(accessToken));
        } catch (SerfConnectorException | NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new RuntimeException(e);
        }
    }

    @PostMapping("/authorize")
    public ResponseEntity<AuthResponse> authorize(@RequestHeader Map<String, String> requestHeaders) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        try {
            if (!authService.checkApiKeyValid(requestHeaders)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            String headerToken = authService.getHeaderToken(requestHeaders);
            DecodedJWT decodedJWT = authService.getDecodedToken(headerToken, Token.Type.AT, Env.FFBE_S);
            String username = decodedJWT.getClaim("x-uname").asString();
            String applicationId = decodedJWT.getClaim("x-aid").asString();
            String refreshToken = authService.generateToken(Token.Type.RT, username, applicationId, Env.FFBE_S);
            authService.saveRefreshToken(username, refreshToken);

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(new AuthResponse(refreshToken));
        } catch (JWTVerificationException | JWTCreationException | SerfConnectorException e) {
            if (e instanceof JWTVerificationException) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }

            throw new RuntimeException(e);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestHeader Map<String, String> requestHeaders) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        try {
            if (!authService.checkApiKeyValid(requestHeaders)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            String headerToken = authService.getHeaderToken(requestHeaders);
            DecodedJWT decodedJWT = authService.getDecodedToken(headerToken, Token.Type.RT, Env.FFBE_S);
            String username = decodedJWT.getClaim("x-uname").asString();
            String dbRefreshToken = authService.getRefreshToken(username, headerToken);

            if (Objects.isNull(dbRefreshToken)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            String refreshToken;
            Instant now = Instant.now();
            if (now.getEpochSecond() > decodedJWT.getIssuedAtAsInstant().plus(1, ChronoUnit.DAYS).getEpochSecond()) {
                String applicationId = decodedJWT.getClaim("x-aid").asString();
                refreshToken = authService.generateToken(Token.Type.RT, username, applicationId, Env.FFBE_S);
                authService.saveRefreshToken(username, refreshToken);
            } else {
                refreshToken = dbRefreshToken;
            }

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(new AuthResponse(refreshToken));
        } catch (JWTVerificationException | JWTCreationException | SerfConnectorException e) {
            if (e instanceof JWTVerificationException) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }

            throw new RuntimeException(e);
        }
    }

    @PostMapping("/revoke/{username}")
    public ResponseEntity<Long> revokeUserAccess(@PathParam("username") String username) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        try {
            long affectedId = authService.removeRefreshTokenByUsername(username);

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(affectedId);
        } catch (SerfConnectorException e) {
            throw new RuntimeException(e);
        }
    }
}