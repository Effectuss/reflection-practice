package edu.school21.db;

import lombok.Getter;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

@Getter
public final class DbConfig {
    private static final String PROPERTIES_FILE = "application.properties";

    private final String url;

    private final String username;

    private final String password;

    public DbConfig() throws IOException {
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(PROPERTIES_FILE)) {
            Properties properties = new Properties();
            properties.load(inputStream);
            this.url = properties.getProperty("jdbc.url");
            this.username = properties.getProperty("jdbc.username");
            this.password = properties.getProperty("jdbc.password");
        }
    }
}
