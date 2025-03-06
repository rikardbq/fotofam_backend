package se.rikardbq.startup;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ApplicationStartupRunner implements ApplicationRunner {

    @Override
    public void run(ApplicationArguments args) throws Exception {
        System.out.println(args.getOptionNames());
        List<String> runMigrationsValues = args.getOptionValues("run-migrations");

        try {
            if (runMigrationsValues != null && runMigrationsValues.contains("true")) {
                ApplicationStartup.executeMigrations(args);
            }
        } catch (Exception e) {
            // throw custom exception that is to be implemented in the connector lib at some point
            throw new Exception(e.getMessage());
        }
    }
}