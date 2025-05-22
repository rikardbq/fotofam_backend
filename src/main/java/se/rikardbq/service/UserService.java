package se.rikardbq.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import se.rikardbq.exception.SerfConnectorException;
import se.rikardbq.models.User;
import se.rikardbq.models.UserDao;
import se.rikardbq.util.PasswordHasher;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.List;
import java.util.Objects;

// check if UserDao needs to exist to manage relationships with other tables at some point
@Component
public class UserService implements IUserService<UserDao> {

    @Autowired
    IDatabaseService databaseService;

    @Override
    public List<UserDao> getUsers() throws SerfConnectorException {
        return databaseService.query(UserDao.class, "SELECT * FROM users;");
    }

    @Override
    public UserDao getUserWithId(int id) throws SerfConnectorException {
        List<UserDao> listUser = databaseService.query(UserDao.class, "SELECT * FROM users WHERE id = ?;", id);
        if (listUser.isEmpty()) {
            return null;
        }

        return listUser.getFirst();
    }

    @Override
    public UserDao getUserWithUsername(String username) throws SerfConnectorException {
        List<UserDao> listUser = databaseService.query(UserDao.class, "SELECT * FROM users WHERE username = ?;", username);
        if (listUser.isEmpty()) {
            return null;
        }

        return listUser.getFirst();
    }

    @Override
    public boolean checkUserCredentialsValid(String dbPassword, String password) throws NoSuchAlgorithmException, InvalidKeySpecException {
        return Objects.equals(dbPassword, PasswordHasher.getEncoder().encode(password));
    }
}
