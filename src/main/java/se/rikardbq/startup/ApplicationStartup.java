package se.rikardbq.startup;

import org.springframework.boot.ApplicationArguments;
import se.rikardbq.connector.Connector;
import se.rikardbq.connector.Migrator;
import se.rikardbq.util.Env;

import java.util.List;
import java.util.Objects;

class ApplicationStartup {

    static void executeMigrations(ApplicationArguments args) throws Exception {
        if (
                !Objects.equals(Env.DB_BASE_PATH, "unset")
                        && !Objects.equals(Env.DB_NAME, "unset")
                        && !Objects.equals(Env.DB_USERNAME, "unset")
                        && !Objects.equals(Env.DB_PASSWORD, "unset")
        ) {
            Connector connector = new Connector(
                    Env.DB_BASE_PATH,
                    Env.DB_NAME,
                    Env.DB_USERNAME,
                    Env.DB_PASSWORD
            );
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
