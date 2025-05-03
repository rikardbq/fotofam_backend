package se.rikardbq.util;

import java.util.Objects;

public class Env {

    public static String getEnv(String name) {
        return Objects.requireNonNullElse(System.getenv(name), "unset");
    }

    public static boolean isUnset(String envValue) {
        return Objects.equals(envValue, "unset");
    }

    public static final String FFBE_S = getEnv("FFBE_S");
    public static final String FFFE_AK = getEnv("FFFE_AK");

    public static final String DB_BASE_PATH = getEnv("DB_BASE_PATH");
    public static final String DB_NAME = getEnv("DB_NAME");
    public static final String DB_USERNAME = getEnv("DB_USERNAME");
    public static final String DB_PASSWORD = getEnv("DB_PASSWORD");
}
