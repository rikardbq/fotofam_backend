package se.rikardbq.service;

import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import se.rikardbq.exception.SerfConnectorException;
import se.rikardbq.models.dao.TokenDao;
import se.rikardbq.util.Env;
import se.rikardbq.util.Token;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@Component
public class AuthService implements IAuthService<DecodedJWT> {

    @Autowired
    ITokenService<TokenDao> tokenService;

    @Override
    public void saveRefreshToken(String username, String token) throws SerfConnectorException {
        tokenService.saveToken(username, token);

    }

    @Override
    public long removeRefreshTokenByUsername(String username) throws SerfConnectorException {
        return tokenService.removeTokenByUsername(username);
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

    // add header missing exception type or something here
    @Override
    public String getHeaderToken(Map<String, String> requestHeaders) {
        String authorization = requestHeaders.get("authorization");

        return authorization.split("Bearer ")[1];
    }

    // add header missing exception type or something here
    @Override
    public boolean checkApiKeyValid(Map<String, String> requestHeaders) {
        String xApiKey = requestHeaders.get("x-api-key");
        String envApiKey = Env.FFFE_AK;

        return !Objects.isNull(xApiKey) && !Env.isUnset(envApiKey) && Objects.equals(xApiKey, envApiKey);
    }
}
