package se.rikardbq.service;

public interface AuthService {
    boolean saveRefreshToken(String username, String token);
    boolean updateRefreshToken(String username, String token);
    String generateAccessToken(String username, String password, String applicationId, String someExtraSecret);
    String generateRefreshToken(String username, String password, String applicationId, String someExtraSecret);
}
