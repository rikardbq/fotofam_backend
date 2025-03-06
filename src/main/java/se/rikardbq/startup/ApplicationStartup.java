package se.rikardbq.startup;

import org.springframework.boot.ApplicationArguments;
import se.rikardbq.connector.Connector;
import se.rikardbq.connector.Migrator;

import java.util.List;
import java.util.Objects;

class ApplicationStartup {

    private static final String DB_BASE_PATH = Objects.requireNonNullElse(System.getenv("DB_BASE_PATH"), "unset");
    private static final String DB_NAME = Objects.requireNonNullElse(System.getenv("DB_NAME"), "unset");
    private static final String DB_USERNAME = Objects.requireNonNullElse(System.getenv("DB_USERNAME"), "unset");
    private static final String DB_PASSWORD = Objects.requireNonNullElse(System.getenv("DB_PASSWORD"), "unset");

    static void executeMigrations(ApplicationArguments args) throws Exception {
        if (
                !Objects.equals(DB_BASE_PATH, "unset")
                        && !Objects.equals(DB_NAME, "unset")
                        && !Objects.equals(DB_USERNAME, "unset")
                        && !Objects.equals(DB_PASSWORD, "unset")
        ) {
            Connector connector = new Connector(DB_BASE_PATH, DB_NAME, DB_USERNAME, DB_PASSWORD);
            List<String> migrationsPathValues = args.getOptionValues("migrations-path");
            String migrationsPath = migrationsPathValues != null && !migrationsPathValues.isEmpty()
                    ? migrationsPathValues.getFirst()
                    : "./migrations";
            Migrator migrator = new Migrator(migrationsPath);

            System.out.println("IM IN");
            migrator.run(connector);
        }
    }
}
