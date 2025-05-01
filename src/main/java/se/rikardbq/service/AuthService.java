package se.rikardbq.service;

import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import se.rikardbq.exception.SerfConnectorException;
import se.rikardbq.util.Token;

public interface AuthService<DecodedJWT> {
    boolean saveRefreshToken(String username, String token) throws SerfConnectorException;
    boolean updateRefreshToken(String username, String token) throws SerfConnectorException;
    String getRefreshToken(String username, String token) throws SerfConnectorException;
    String generateToken(Token.Type type, String username, String applicationId, String serverSecret) throws JWTCreationException;
    DecodedJWT getDecodedToken(String token, Token.Type type, String secret) throws JWTVerificationException;
}
