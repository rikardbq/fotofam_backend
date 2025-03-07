package se.rikardbq.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.stereotype.Component;
import se.rikardbq.connector.Connector;
import se.rikardbq.exception.MyCustomException;
import se.rikardbq.exception.TokenPayloadErrorException;
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
    public <T> List<T> query(Class<T> valueType, String query, Object... parts) throws MyCustomException {
        // handle potential exceptions and re-throw as domain specific error exceptions
        try {
            return connector.query(valueType, query, parts);
        } catch (JsonProcessingException | TokenPayloadErrorException e) {
            throw new MyCustomException(e.getMessage());
        }
    }

    @Override
    public long mutate(String query, Object... parts) throws MyCustomException {
        // handle potential exceptions and re-throw as domain specific error exceptions
        try {
            return connector.mutate(query, parts);
        } catch (JsonProcessingException | TokenPayloadErrorException e) {
            throw new MyCustomException(e.getMessage());
        }
    }
}
