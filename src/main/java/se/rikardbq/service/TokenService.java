package se.rikardbq.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import se.rikardbq.exception.SerfConnectorException;
import se.rikardbq.models.MutationResponse;
import se.rikardbq.models.dao.TokenDao;

import java.time.Instant;
import java.util.List;

@Component
public class TokenService implements ITokenService<TokenDao> {

    @Autowired
    IDatabaseService databaseService;

    @Override
    public void saveToken(String username, String token) throws SerfConnectorException {
        long now = Instant.now().getEpochSecond();

        databaseService.mutate("""
                INSERT INTO tokens(username, rt, created_at, updated_at) VALUES(?, ?, ?, ?)
                ON CONFLICT(username) DO UPDATE SET rt = excluded.rt, updated_at = excluded.updated_at
                WHERE excluded.username = username;
                """, username, token, now, now);
    }

    @Override
    public long removeTokenByUsername(String username) throws SerfConnectorException {
        MutationResponse mutation = databaseService.mutate("DELETE FROM tokens WHERE username = ?;", username);

        return mutation.getRowsAffected();
    }

    @Override
    public List<TokenDao> getToken(String username, String token) {
        return databaseService.query(TokenDao.class, "SELECT * FROM tokens WHERE username = ? and rt = ?;", username, token);
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
