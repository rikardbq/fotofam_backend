package se.rikardbq.controller;

import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import se.rikardbq.exception.SerfConnectorException;
import se.rikardbq.models.Image;
import se.rikardbq.models.auth.AuthRequest;
import se.rikardbq.models.auth.AuthResponse;
import se.rikardbq.service.AuthService;
import se.rikardbq.service.ImageService;
import se.rikardbq.service.TokenServiceImpl;

@RestController
public class AuthController {

    @Autowired
    private AuthService authService;


    // authenticate, i.e send login credentials. Receive a signed access-token (signed with client-application-id + server secret and username)
    @PostMapping("/authenticate")
    public ResponseEntity<AuthResponse> authenticate(@RequestBody AuthRequest authRequest) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        try {
            String accessToken = authService.generateAccessToken(
                    authRequest.getUsername(),
                    authRequest.getPassword(),
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
    public ResponseEntity<AuthResponse> authorize(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorization) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        try {
            String headerToken = authorization.split("Bearer ")[1];
            DecodedJWT decodedJWT = authService.getDecodedToken(headerToken, TokenServiceImpl.Type.AT, "SERVER_SECRET");

            // if token has been decoded then the token is OK and contents can be used safely
            String username = decodedJWT.getClaim("x-uname").asString();
            String applicationId = decodedJWT.getClaim("x-aid").asString();

            // STORE RT IN DB BEFORE SENDING IT OFF
            String refreshToken = authService.generateRefreshToken(username, applicationId, "SERVER_SECRET");

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(new AuthResponse(refreshToken));
        } catch (JWTVerificationException | JWTCreationException e) {
            throw new RuntimeException(e);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorization) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        try {
            String headerToken = authorization.split("Bearer ")[1];
            DecodedJWT decodedJWT = authService.getDecodedToken(headerToken, TokenServiceImpl.Type.RT, "SERVER_SECRET");

            // if token has been decoded then the token is OK and contents can be used safely
            String username = decodedJWT.getClaim("x-uname").asString();
            String applicationId = decodedJWT.getClaim("x-aid").asString();

            // CHECK RT FROM DB BEFORE RE-GENERATING NEW ONE, keyed on username or the token string itself
            // IF RT expired then respond with 403 or something and clear tokens for user.
            // Client APP logic will handle the flow for resetting state and user login page is shown again for re-login flow
            // FLOW = authenticate -> authorize -> login
            String refreshToken = authService.generateRefreshToken(username, applicationId, "SERVER_SECRET");

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(new AuthResponse(refreshToken));
        } catch (JWTVerificationException | JWTCreationException e) {
            throw new RuntimeException(e);
        }
    }
}