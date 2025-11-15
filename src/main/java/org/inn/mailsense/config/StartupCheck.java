package org.inn.mailsense.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.Statement;

@Configuration
public class StartupCheck {

    @Bean
    public CommandLineRunner infoDB(DataSource dataSource) {
        return args -> {
          try (Connection connection = dataSource.getConnection()) {
              try (Statement statement = connection.createStatement()) {
                statement.execute("CREATE TABLE IF NOT EXISTS info_db(id INTEGER)");
              }
              System.out.println("DuckDB (file-based) connected: " + connection.getMetaData().getURL());
          } catch (Exception e) {
              System.err.println("Failed to connect to DuckDB: " + e.getMessage());
          }
        };
    }
}
