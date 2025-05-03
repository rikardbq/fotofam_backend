package se.rikardbq.startup;

import org.springframework.boot.ApplicationArguments;
import se.rikardbq.connector.Connector;
import se.rikardbq.connector.Migrator;
import se.rikardbq.util.Env;

import java.util.List;

class ApplicationStartup {

    static void executeMigrations(ApplicationArguments args) throws Exception {
        if (!Env.isUnset(Env.FFBE_S)
                && !Env.isUnset(Env.DB_BASE_PATH)
                && !Env.isUnset(Env.DB_NAME)
                && !Env.isUnset(Env.DB_USERNAME)
                && !Env.isUnset(Env.DB_PASSWORD)
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
        } else {
            throw new Exception("One or several env vars missing", new Throwable("missing env vars"));
        }
    }
}
