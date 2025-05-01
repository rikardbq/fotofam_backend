package se.rikardbq.service;

import org.springframework.beans.factory.annotation.Autowired;

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

    @Override
    public String generateAccessToken(String username, String password, String applicationId, String someExtraSecret) {
        // auth0.jwt stuff
        return "";
    }

    @Override
    public String generateRefreshToken(String username, String password, String applicationId, String someExtraSecret) {
        // auth0.jwt stuff
        return "";
    }
}
