package se.rikardbq.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

public class TokenServiceImpl implements TokenService {
    private static final String ISSUER_ID = "FFBE-X-FFFE";

    @Autowired
    DatabaseService databaseService;

    @Override
    public boolean saveToken(String username, String token) {

        // do some saving to DB
        return true;
    }

    @Override
    public boolean updateToken(String username, String token) {

        // do some upsert to DB
        return true;
    }

    @Override
    public boolean checkToken(String username, String token) {

        // do some check on the DB variant of the token
        return true;
    }

    @Override
    public DecodedJWT decodeToken(String token, Type type, String secret) throws JWTVerificationException {
        Algorithm algorithm = Algorithm.HMAC256(secret);
        JWTVerifier verifier = JWT.require(algorithm)
                .withIssuer(ISSUER_ID)
                .withSubject(type.name())
                .build();

        return verifier.verify(token);
    }

    @Override
    public String encodeToken(Type type, String username, String applicationId, String secret) throws JWTCreationException {
        Algorithm algorithm = Algorithm.HMAC256(secret);
        Instant now = Instant.now();
        Instant exp = type == Type.RT ? now.plus(30, ChronoUnit.DAYS) : now.plusSeconds(30);

        return JWT.create()
                .withIssuer(ISSUER_ID)
                .withSubject(type.name())
                .withIssuedAt(now)
                .withExpiresAt(exp)
                .withClaim("x-uname", username)
                .withClaim("x-aid", applicationId)
                .sign(algorithm);
    }

//    private JWTCreator.Builder applyClaims(JWTCreator.Builder jwtBuilder, Map<String, Map<String, Object>> claims) {
//        Set<Map.Entry<String, Map<String, Object>>> claimsSet = claims.entrySet();
//        if (!claimsSet.isEmpty()) {
//            for (Map.Entry<String, Map<String, Object>> claim : claimsSet) {
//                jwtBuilder.withClaim(claim.getKey(), claim.getValue());
//            }
//        }
//
//        return jwtBuilder;
//    }

    public enum Type {
        AT,
        RT
    }
//    public static void main(String[] args) {
//
//        TokenServiceImpl tokenService = new TokenServiceImpl();
//        String token = tokenService.encodeToken(Type.AT, "rikardbq2", "x-appl-id", "SERVER_SECRET");
//        DecodedJWT decodedJWT = tokenService.decodeToken("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJGRkJFLVgtRkZGRSIsInN1YiI6IkFUIiwiaWF0IjoxNzQ2MTExMDcyLCJleHAiOjE3NDYxMTExMDIsIngtdW5hbWUiOiJyaWthcmRicTIiLCJ4LWFpZCI6IngtYXBwbC1pZCJ9.jGCovdGuSNRk_Usk13VApqF4qjYlQVs6I6ytQgfRZMs", Type.AT, "SERVER_SECRET");
//        System.out.println(decodedJWT.getClaims());
//        System.out.println(decodedJWT);
//        System.out.println(token);
//    }
}
