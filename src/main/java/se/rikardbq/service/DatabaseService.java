package se.rikardbq.service;

import se.rikardbq.exception.MyCustomException;

import java.util.List;

public interface DatabaseService {

    <T> List<T> query(Class<T> valueType, String query, Object... parts) throws MyCustomException;

    long mutate(String query, Object... parts) throws MyCustomException;
}
