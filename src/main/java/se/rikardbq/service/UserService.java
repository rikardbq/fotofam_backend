package se.rikardbq.service;

import se.rikardbq.exception.SerfConnectorException;

import java.util.List;

public interface UserService<User> {
    List<User> getUsers() throws SerfConnectorException;
    User getUserWithId(int id) throws SerfConnectorException;
}
