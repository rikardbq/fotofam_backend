package se.rikardbq.service;

import com.google.protobuf.InvalidProtocolBufferException;
import org.springframework.stereotype.Component;
import se.rikardbq.connector.Connector;
import se.rikardbq.exception.SerfConnectorException;
import se.rikardbq.exception.ProtoPackageErrorException;
import se.rikardbq.models.MutationResponse;
import se.rikardbq.util.Env;

import java.io.IOException;
import java.util.List;

@Component
public class DatabaseService implements IDatabaseService {

    private final Connector connector = new Connector(
            Env.DB_BASE_PATH,
            Env.DB_NAME,
            Env.DB_USERNAME,
            Env.DB_PASSWORD
    );

    @Override
    public <T> List<T> query(Class<T> valueType, String query, Object... parts) throws SerfConnectorException {
        // handle potential exceptions and re-throw as domain specific error exceptions
        try {
            return connector.query(valueType, query, parts);
        } catch (ProtoPackageErrorException | IOException e) {
            throw new SerfConnectorException(e);
        }
    }

    @Override
    public MutationResponse mutate(String query, Object... parts) throws SerfConnectorException {
        // handle potential exceptions and re-throw as domain specific error exceptions
        try {
            return connector.mutate(query, parts);
        } catch (ProtoPackageErrorException | InvalidProtocolBufferException e) {
            throw new SerfConnectorException(e);
        }
    }
}
