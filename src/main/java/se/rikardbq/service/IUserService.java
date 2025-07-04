package se.rikardbq.service;

import se.rikardbq.exception.SerfConnectorException;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.List;
import java.util.Optional;

public interface IUserService<UserDao> {
    List<UserDao> getUsers() throws SerfConnectorException;

    Optional<UserDao> getUserWithId(int id) throws SerfConnectorException;

    Optional<UserDao> getUserWithUsername(String username) throws SerfConnectorException;

    boolean checkUserCredentialsValid(String dbPassword, String password) throws NoSuchAlgorithmException, InvalidKeySpecException;
}
