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
    public static final String[] clientApplications = {
            "FFFE",
            "8ad421cf-4fa7-42f1-8924-32b18a9104d1"
    };

    public static DecodedJWT decodeToken(String token, Token.Type type, String secret) throws JWTVerificationException {
        Algorithm algorithm = Algorithm.HMAC256(secret);
        JWTVerifier verifier = JWT.require(algorithm)
                .withIssuer(Constants.Token.ISSUER_ID)
                .withAnyOfAudience(clientApplications)
                .withSubject(type.name())
                .withClaimPresence(Constants.Token.Claim.X_AID)
                .withClaimPresence(Constants.Token.Claim.X_UNAME)
                .withClaimPresence(Constants.Token.Claim.X_RNAME)
                .build();

        return verifier.verify(token);
    }

    public static String encodeToken(Token.Type type, String username, String realName, String applicationId, String secret) throws JWTCreationException {
        Algorithm algorithm = Algorithm.HMAC256(secret);
        Instant now = Instant.now();
        Instant exp = type == Token.Type.RT ? now.plus(30, ChronoUnit.DAYS) : now.plusSeconds(30);

        return JWT.create()
                .withIssuer(Constants.Token.ISSUER_ID)
                .withAudience(applicationId)
                .withSubject(type.name())
                .withIssuedAt(now)
                .withExpiresAt(exp)
                .withClaim(Constants.Token.Claim.X_UNAME, username)
                .withClaim(Constants.Token.Claim.X_RNAME, realName)
                .withClaim(Constants.Token.Claim.X_AID, applicationId)
                .sign(algorithm);
    }

    public enum Type {
        AT,
        RT
    }
}
