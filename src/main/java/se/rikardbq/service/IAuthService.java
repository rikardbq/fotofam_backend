package se.rikardbq.service;

import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import se.rikardbq.exception.SerfConnectorException;
import se.rikardbq.util.Token;

import java.util.Map;

public interface IAuthService<DecodedJWT> {
    void saveRefreshToken(String username, String token) throws SerfConnectorException;
    long removeRefreshTokenByUsername(String username) throws SerfConnectorException;
    String getRefreshToken(String username, String token) throws SerfConnectorException;
    String generateToken(Token.Type type, String username, String applicationId, String serverSecret) throws JWTCreationException;
    DecodedJWT getDecodedToken(String token, Token.Type type, String secret) throws JWTVerificationException;
    String getHeaderToken(Map<String, String> requestHeaders);
    boolean checkApiKeyValid(Map<String, String> requestHeaders);
    boolean checkOriginValid(Map<String, String> requestHeaders);
}
