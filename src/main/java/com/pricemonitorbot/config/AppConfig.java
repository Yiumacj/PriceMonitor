package com.pricemonitorbot.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class AppConfig {

    private static final AppConfig INSTANCE = new AppConfig();

    private final Properties properties = new Properties();

    private AppConfig() {
        try (InputStream in = getClass().getClassLoader()
                .getResourceAsStream("env.properties")) {

            if (in == null) {
                throw new RuntimeException("env.properties not found in resources");
            }

            properties.load(in);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load config.properties", e);
        }
    }

    public static AppConfig getInstance() {
        return INSTANCE;
    }


    public boolean isConsoleEnabled() {
        return Boolean.parseBoolean(properties.getProperty("view.console.enabled", "false"));
    }

    public boolean isTelegramEnabled() {
        return Boolean.parseBoolean(properties.getProperty("view.telegram.enabled", "false"));
    }

    public String getTelegramToken() {
        return properties.getProperty("telegram.bot.token");
    }

    public String getTelegramUsername() {
        return properties.getProperty("telegram.bot.username");
    }

    public String getDbUrl() {
        return properties.getProperty("db.url");
    }

    public String getDbUser() {
        return properties.getProperty("db.user");
    }

    public String getDbPassword() {
        return properties.getProperty("db.password");
    }

    public String getOzonClientId() {
        return properties.getProperty("ozon.clientId");
    }

    public String getOzonApiKey() {
        return properties.getProperty("ozon.apiKey");
    }

    public String getOzonBaseUrl() {
        return properties.getProperty("ozon.baseUrl", "https://api-seller.ozon.ru");
    }


    public String get(String key) {
        return properties.getProperty(key);
    }
}
