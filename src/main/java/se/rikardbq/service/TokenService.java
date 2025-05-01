package se.rikardbq.service;

import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;

import java.util.Map;

public interface TokenService {
    boolean saveToken(String username, String token);
    boolean updateToken(String username, String token);
    boolean checkToken(String username, String token);
    DecodedJWT decodeToken(String token, TokenServiceImpl.Type type, String secret) throws JWTVerificationException;
    String encodeToken(TokenServiceImpl.Type type, String username, String applicationId, String secret) throws JWTCreationException;
}
