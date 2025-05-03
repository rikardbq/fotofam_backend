package se.rikardbq.service;

import se.rikardbq.exception.SerfConnectorException;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.List;

public interface IUserService<User> {
    List<User> getUsers() throws SerfConnectorException;
    User getUserWithId(int id) throws SerfConnectorException;
    User getUserWithUsername(String username) throws SerfConnectorException;
    boolean checkUserCredentialsValid(User user, String password) throws NoSuchAlgorithmException, InvalidKeySpecException;
}
