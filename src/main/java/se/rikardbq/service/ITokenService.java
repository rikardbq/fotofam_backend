package se.rikardbq.service;

import se.rikardbq.exception.SerfConnectorException;

import java.util.List;

public interface ITokenService<TokenDao> {
    void saveToken(String username, String token) throws SerfConnectorException;
    void updateToken(String username, String token) throws SerfConnectorException;
    List<TokenDao> getToken(String username, String token);
}
