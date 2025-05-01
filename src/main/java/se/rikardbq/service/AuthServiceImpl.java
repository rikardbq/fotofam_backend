package se.rikardbq.service;

import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.Map;

public class AuthServiceImpl implements AuthService {

    @Autowired
    TokenService tokenService;

    @Override
    public boolean saveRefreshToken(String username, String token) {
        return false;
    }

    @Override
    public boolean updateRefreshToken(String username, String token) {
        return false;
    }

    // add more info for each specific case. AT / RT
    @Override
    public String generateAccessToken(String username, String password, String applicationId, String serverSecret) {
        return tokenService.encodeToken(TokenServiceImpl.Type.AT, username, applicationId, serverSecret);
    }

    @Override
    public String generateRefreshToken(String username, String applicationId, String serverSecret) throws JWTCreationException {
        return tokenService.encodeToken(TokenServiceImpl.Type.RT, username, applicationId, serverSecret);
    }

    @Override
    public DecodedJWT getDecodedToken(String token, TokenServiceImpl.Type type, String secret) throws JWTVerificationException {
        return tokenService.decodeToken(token, type, secret);
    }
}
