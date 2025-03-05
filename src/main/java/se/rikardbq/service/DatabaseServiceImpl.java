package se.rikardbq.service;

import org.springframework.stereotype.Component;
import se.rikardbq.SomeDataClass;
import se.rikardbq.connector.Connector;

import java.util.List;
import java.util.Objects;

@Component
public class DatabaseServiceImpl implements DatabaseService<SomeDataClass> {
    private static final String DB_BASE_PATH = Objects.requireNonNullElse(System.getenv("DB_BASE_PATH"), "http://localhost:8080");
    private static final String DB_NAME = Objects.requireNonNullElse(System.getenv("DB_NAME"), "TEST");
    private static final String DB_USERNAME = Objects.requireNonNullElse(System.getenv("DB_USERNAME"), "TEST");
    private static final String DB_PASSWORD = Objects.requireNonNullElse(System.getenv("DB_PASSWORD"), "TEST");

    private Connector connector = new Connector(DB_BASE_PATH, DB_NAME, DB_USERNAME, DB_PASSWORD);


    @Override
    public List<SomeDataClass> getData() {

        return List.of();
    }
}
