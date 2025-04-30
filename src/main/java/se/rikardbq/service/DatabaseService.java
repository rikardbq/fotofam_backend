package se.rikardbq.service;

import se.rikardbq.exception.SerfConnectorException;
import se.rikardbq.models.MutationResponse;

import java.util.List;

public interface DatabaseService {

    <T> List<T> query(Class<T> valueType, String query, Object... parts) throws SerfConnectorException;

    MutationResponse mutate(String query, Object... parts) throws SerfConnectorException;
}
