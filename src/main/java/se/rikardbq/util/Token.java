package se.rikardbq.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

public class Token {
    private static final String ISSUER_ID = "FFBE-X-FFFE";

    public enum Type {
        AT,
        RT
    }

    public static DecodedJWT decodeToken(String token, Token.Type type, String secret) throws JWTVerificationException {
        Algorithm algorithm = Algorithm.HMAC256(secret);
        JWTVerifier verifier = JWT.require(algorithm)
                .withIssuer(ISSUER_ID)
                .withSubject(type.name())
                .withClaimPresence("x-aid")
                .withClaimPresence("x-uname")
                .build();

        return verifier.verify(token);
    }

    public static String encodeToken(Token.Type type, String username, String applicationId, String secret) throws JWTCreationException {
        Algorithm algorithm = Algorithm.HMAC256(secret);
        Instant now = Instant.now();
        Instant exp = type == Token.Type.RT ? now.plus(30, ChronoUnit.DAYS) : now.plusSeconds(30);

        return JWT.create()
                .withIssuer(ISSUER_ID)
                .withSubject(type.name())
                .withIssuedAt(now)
                .withExpiresAt(exp)
                .withClaim("x-uname", username)
                .withClaim("x-aid", applicationId)
                .sign(algorithm);
    }
}
