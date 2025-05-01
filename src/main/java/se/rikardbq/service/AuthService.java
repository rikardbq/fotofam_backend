package se.rikardbq.service;

import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;

public interface AuthService {
    boolean saveRefreshToken(String username, String token);
    boolean updateRefreshToken(String username, String token);
    String generateAccessToken(String username, String password, String applicationId, String serverSecret);
    String generateRefreshToken(String username, String applicationId, String serverSecret) throws JWTCreationException;
    DecodedJWT getDecodedToken(String token, TokenServiceImpl.Type type, String secret) throws JWTVerificationException;
}
