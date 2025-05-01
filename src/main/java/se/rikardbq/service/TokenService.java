package se.rikardbq.service;

public interface TokenService {
    boolean saveToken(String username, String token);
    boolean updateToken(String username, String token);
    boolean checkToken(String username, String token);
    // some enum to describe the token type
    // enum TokenType {
    //  AT,
    //  RT
    // }
    // change below String tokenType to "TokenType type" later
    // String generateToken(String tokenType, String username, String applicationId, String someExtraSecret)
}
