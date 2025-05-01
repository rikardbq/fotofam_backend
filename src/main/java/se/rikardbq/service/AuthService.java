package se.rikardbq.service;

import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.beans.factory.annotation.Autowired;
import se.rikardbq.exception.SerfConnectorException;
import se.rikardbq.models.dao.TokenDao;
import se.rikardbq.util.Token;

import java.util.List;

public class AuthService implements IAuthService<DecodedJWT> {

    @Autowired
    ITokenService<TokenDao> tokenService;

    @Override
    public boolean saveRefreshToken(String username, String token) throws SerfConnectorException {
        tokenService.saveToken(username, token);

        return true;
    }

    @Override
    public boolean updateRefreshToken(String username, String token) throws SerfConnectorException {
        tokenService.updateToken(username, token);

        return true;
    }

    @Override
    public String getRefreshToken(String username, String token) throws SerfConnectorException {
        List<TokenDao> listDao = tokenService.getToken(username, token);
        if (listDao.isEmpty()) {
            return null;
        }

        return listDao.getFirst().getRefreshToken();
    }

    @Override
    public String generateToken(Token.Type type, String username, String applicationId, String serverSecret) throws JWTCreationException {
        return Token.encodeToken(type, username, applicationId, serverSecret);
    }

    @Override
    public DecodedJWT getDecodedToken(String token, Token.Type type, String secret) throws JWTVerificationException {
        return Token.decodeToken(token, type, secret);
    }
}
