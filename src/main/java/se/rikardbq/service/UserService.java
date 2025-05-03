package se.rikardbq.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import se.rikardbq.exception.SerfConnectorException;
import se.rikardbq.models.User;

import java.util.List;

// check if UserDao needs to exist to manage relationships with other tables at some point
@Component
public class UserService implements IUserService<User> {

    @Autowired
    IDatabaseService databaseService;

    @Override
    public List<User> getUsers() throws SerfConnectorException {
        return databaseService.query(User.class, "SELECT * FROM users;");
    }

    @Override
    public User getUserWithId(int id) throws SerfConnectorException {
        List<User> listUser = databaseService.query(User.class, "SELECT * FROM users WHERE id = ?;", id);
        if (listUser.isEmpty()) {
            return null;
        }

        return listUser.getFirst();
    }

    @Override
    public User getUserWithUsername(String username) throws SerfConnectorException {
        List<User> listUser = databaseService.query(User.class, "SELECT * FROM users WHERE username = ?;", username);
        if (listUser.isEmpty()) {
            return null;
        }

        return listUser.getFirst();
    }
}
