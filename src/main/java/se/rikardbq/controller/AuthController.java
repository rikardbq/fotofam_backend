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
import se.rikardbq.models.auth.AuthRequest;
import se.rikardbq.models.auth.AuthResponse;
import se.rikardbq.service.AuthService;
import se.rikardbq.util.Token;

import java.util.Map;
import java.util.Objects;

@RestController
public class AuthController {

    @Autowired
    private AuthService<DecodedJWT> authService;

    // SERVER_SECRET is ENV VAR that is generated on the server
    // authenticate, i.e send login credentials. Receive a signed access-token (signed with client-application-id + server secret and username)
    @PostMapping("/authenticate")
    public ResponseEntity<AuthResponse> authenticate(@RequestHeader Map<String, String> requestHeaders, @RequestBody AuthRequest authRequest) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        try {
            // if api key is not correct unauthorized 401
            String xApiKey = requestHeaders.get("X-API-KEY");
            if (!Objects.equals(xApiKey, "API-KEY FROM ENV VAR")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
            // BEFORE creating accessToken check the USER PW and existence in DB
            String accessToken = authService.generateToken(
                    Token.Type.AT,
                    authRequest.getUsername(),
                    authRequest.getApplicationId(),
                    "SERVER_SECRET"
            );

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(new AuthResponse(accessToken));
        } catch (SerfConnectorException e) {
            throw new RuntimeException(e);
        }
    }

    // authorize, accept access-token and verify token as originating from self. i.e signed with client-application-id + server secret and username
    @PostMapping("/authorize")
    public ResponseEntity<AuthResponse> authorize(@RequestHeader Map<String, String> requestHeaders) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        try {
            // if api key is not correct unauthorized 401
            String xApiKey = requestHeaders.get("X-API-KEY");
            if (!Objects.equals(xApiKey, "API-KEY FROM ENV VAR")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            String authorization = requestHeaders.get("Authorization");
            String headerToken = authorization.split("Bearer ")[1];
            DecodedJWT decodedJWT = authService.getDecodedToken(headerToken, Token.Type.AT, "SERVER_SECRET");

            // if token has been decoded then the token is OK and contents can be used safely
            String username = decodedJWT.getClaim("x-uname").asString();
            String applicationId = decodedJWT.getClaim("x-aid").asString();

            // STORE RT IN DB BEFORE SENDING IT OFF
            String refreshToken = authService.generateToken(Token.Type.RT, username, applicationId, "SERVER_SECRET");
            authService.saveRefreshToken(username, refreshToken);

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(new AuthResponse(refreshToken));
        } catch (JWTVerificationException | JWTCreationException | SerfConnectorException e) {
            throw new RuntimeException(e);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestHeader Map<String, String> requestHeaders) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        try {
            // if api key is not correct unauthorized 401
            String xApiKey = requestHeaders.get("X-API-KEY");
            if (!Objects.equals(xApiKey, "API-KEY FROM ENV VAR")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            String authorization = requestHeaders.get("Authorization");
            String headerToken = authorization.split("Bearer ")[1];
            DecodedJWT decodedJWT = authService.getDecodedToken(headerToken, Token.Type.RT, "SERVER_SECRET");

            // if token has been decoded then the token is OK and contents can be used safely
            String username = decodedJWT.getClaim("x-uname").asString();
            String applicationId = decodedJWT.getClaim("x-aid").asString();

            // CHECK RT FROM DB BEFORE RE-GENERATING NEW ONE, keyed on username or the token string itself
            // IF RT expired then respond with 403 or something and clear tokens for user.
            // Client APP logic will handle the flow for resetting state and user login page is shown again for re-login flow
            // FLOW = authenticate -> authorize -> login
            String refreshToken = authService.generateToken(Token.Type.RT, username, applicationId, "SERVER_SECRET");

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(new AuthResponse(refreshToken));
        } catch (JWTVerificationException | JWTCreationException e) {
            throw new RuntimeException(e);
        }
    }
}