package se.rikardbq.service;

import org.springframework.beans.factory.annotation.Autowired;
import se.rikardbq.exception.SerfConnectorException;
import se.rikardbq.models.User;

import java.util.List;

// check if UserDao needs to exist to manage relationships with other tables at some point
public class UserServiceImpl implements UserService<User> {

    @Autowired
    DatabaseService databaseService;

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
}
