package se.rikardbq.controller;

import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
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
import se.rikardbq.util.PasswordHasher;
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

    // SERVER_SECRET is ENV VAR that is generated on the server
    // authenticate, i.e send login credentials. Receive a signed access-token (signed with server secret)
    @PostMapping("/authenticate")
    public ResponseEntity<AuthResponse> authenticate(@RequestHeader Map<String, String> requestHeaders, @RequestBody AuthRequest authRequest) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        try {
            String secret = Env.FFBE_S;
            if (Env.isUnset(secret)) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }

            // if api key is not correct unauthorized 401
            String xApiKey = requestHeaders.get("x-api-key");
            System.out.println(xApiKey);
            String envApiKey = Env.FFFE_AK;
            if (!Objects.equals(xApiKey, envApiKey) || Env.isUnset(envApiKey)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            User user = userService.getUserWithUsername(authRequest.getUsername());
            String hashedPW = new PasswordHasher(authRequest.getPassword()).encode();
            if (Objects.isNull(user) || !Objects.equals(user.getPassword(), hashedPW)) {
                ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }

            String accessToken = authService.generateToken(
                    Token.Type.AT,
                    authRequest.getUsername(),
                    authRequest.getApplicationId(),
                    secret
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
            String secret = Env.FFBE_S;
            if (Env.isUnset(secret)) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }

            // if api key is not correct unauthorized 401
            String xApiKey = requestHeaders.get("x-api-key");
            String envApiKey = Env.FFFE_AK;
            if (!Objects.equals(xApiKey, envApiKey) && !Env.isUnset(envApiKey)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            String authorization = requestHeaders.get("authorization");
            String headerToken = authorization.split("Bearer ")[1];
            DecodedJWT decodedJWT = authService.getDecodedToken(headerToken, Token.Type.AT, secret);

            // if token has been decoded then the token is OK and contents can be used safely
            String username = decodedJWT.getClaim("x-uname").asString();
            String applicationId = decodedJWT.getClaim("x-aid").asString();

            // STORE RT IN DB BEFORE SENDING IT OFF
            String refreshToken = authService.generateToken(Token.Type.RT, username, applicationId, secret);
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

    // might be unnecessary to use the entire flow to log in as this can be done in the step before
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestHeader Map<String, String> requestHeaders) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        try {
            String secret = Env.FFBE_S;
            if (Env.isUnset(secret)) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }

            // if api key is not correct unauthorized 401
            String xApiKey = requestHeaders.get("x-api-key");
            String envApiKey = Env.FFFE_AK;
            if (!Objects.equals(xApiKey, envApiKey) && !Env.isUnset(envApiKey)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            String authorization = requestHeaders.get("authorization");
            String headerToken = authorization.split("Bearer ")[1];
            DecodedJWT decodedJWT = authService.getDecodedToken(headerToken, Token.Type.RT, secret);

            // if token has been decoded then the token is OK and contents can be used safely
            String username = decodedJWT.getClaim("x-uname").asString();
            String applicationId = decodedJWT.getClaim("x-aid").asString();

            String dbRefreshToken = authService.getRefreshToken(username, headerToken);
            // if token doesnt exist in db then return unauthorized 401
            if (Objects.isNull(dbRefreshToken)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            String refreshToken;
            Instant now = Instant.now();
            // only re-refresh 1 day old refresh tokens for the DB, else just serve what's in DB
            if (now.getEpochSecond() > decodedJWT.getIssuedAtAsInstant().plus(1, ChronoUnit.DAYS).getEpochSecond()) {
                refreshToken = authService.generateToken(Token.Type.RT, username, applicationId, secret);
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
}