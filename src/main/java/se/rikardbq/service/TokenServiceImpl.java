package se.rikardbq.service;

import org.springframework.beans.factory.annotation.Autowired;

public class TokenServiceImpl implements TokenService {

    @Autowired
    DatabaseService databaseService;

    @Override
    public boolean saveToken(String username, String token) {

        // do some saving to DB
        return true;
    }

    @Override
    public boolean updateToken(String username, String token) {

        // do some upsert to DB
        return true;
    }

    @Override
    public boolean checkToken(String username, String token) {

        // do some check on the DB variant of the token
        return true;
    }
}
