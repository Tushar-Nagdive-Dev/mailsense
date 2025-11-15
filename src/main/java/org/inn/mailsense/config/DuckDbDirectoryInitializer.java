package org.inn.mailsense.config;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.io.File;
import java.nio.file.Path;

@Configuration
public class DuckDbDirectoryInitializer {

    @Value("${DUCKDB_URL}")
    private String duckDbUrl;

    @PostConstruct
    public void init() {
        // Extract file path from JDBC URL: jdbc:duckdb:./data/mailsense.duckdb
        String raw = duckDbUrl.replace("jdbc:duckdb:", "").trim();

        if(raw.isEmpty() || raw.equals(":memory:")) {
            return; // nothing to create for in-memory mode
        }

        Path dbFile = Path.of(raw).normalize();
        File folder = dbFile.getParent().toFile();

        if (!folder.exists()) {
            if (folder.mkdirs()) {
                System.out.println("Created DuckDB directory: " + folder.getAbsolutePath());
            }
        }
    }
}
