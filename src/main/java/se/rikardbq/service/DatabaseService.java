package se.rikardbq.service;

import com.fasterxml.jackson.core.JsonProcessingException;

import java.util.List;

public interface DatabaseService {

    <T> List<T> query(Class<T> typeClass, String query, Object... parts) throws JsonProcessingException;

    long mutate(String query, Object... parts) throws JsonProcessingException;
}
