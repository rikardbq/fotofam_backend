package se.rikardbq.util;

import java.util.Objects;

public class Env {

    public static final String DB_BASE_PATH = Objects.requireNonNullElse(System.getenv("DB_BASE_PATH"), "unset");
    public static final String DB_NAME = Objects.requireNonNullElse(System.getenv("DB_NAME"), "unset");
    public static final String DB_USERNAME = Objects.requireNonNullElse(System.getenv("DB_USERNAME"), "unset");
    public static final String DB_PASSWORD = Objects.requireNonNullElse(System.getenv("DB_PASSWORD"), "unset");
}
