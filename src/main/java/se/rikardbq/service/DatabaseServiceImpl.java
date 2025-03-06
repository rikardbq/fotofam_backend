package se.rikardbq.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.stereotype.Component;
import se.rikardbq.connector.Connector;
import se.rikardbq.util.Env;

import java.util.List;

@Component
public class DatabaseServiceImpl implements DatabaseService {

    private final Connector connector = new Connector(
            Env.DB_BASE_PATH,
            Env.DB_NAME,
            Env.DB_USERNAME,
            Env.DB_PASSWORD
    );

    @Override
    public <T> List<T> query(Class<T> typeClass, String query, Object... parts) throws JsonProcessingException {
        return connector.query(typeClass, query, parts);
    }

    @Override
    public long mutate(String query, Object... parts) throws JsonProcessingException {
        return connector.mutate(query, parts);
    }
}
