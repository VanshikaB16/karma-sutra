package com.karmasutra.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Central application configuration manager.
 * Priority order:
 * 1. System environment variables
 * 2. .env file in project root or user working directory
 * 3. application.properties from classpath
 */
public final class AppConfig {

    private static final Logger LOGGER = Logger.getLogger(AppConfig.class.getName());
    private static final Properties PROPS = new Properties();

    static {
        loadProperties();
    }

    private AppConfig() {
    }

    private static void loadProperties() {
        // 1. Load application.properties from classpath
        try (InputStream is = AppConfig.class.getClassLoader().getResourceAsStream("application.properties")) {
            if (is != null) {
                PROPS.load(is);
            }
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Failed to load application.properties from classpath", e);
        }

        // 2. Load .env file if available in working dir or parent
        Path envPath = Path.of(".env");
        if (!Files.exists(envPath)) {
            envPath = Path.of(System.getProperty("user.dir"), ".env");
        }
        if (Files.exists(envPath)) {
            try (InputStream is = new FileInputStream(envPath.toFile())) {
                Properties envProps = new Properties();
                envProps.load(is);
                for (String name : envProps.stringPropertyNames()) {
                    PROPS.setProperty(name, envProps.getProperty(name));
                }
            } catch (Exception e) {
                LOGGER.log(Level.WARNING, "Could not load .env file", e);
            }
        }
    }

    public static String get(String key, String defaultValue) {
        // Check System environment first
        String envKey = key.toUpperCase().replace('.', '_');
        String envVal = System.getenv(envKey);
        if (envVal != null && !envVal.isBlank()) {
            return envVal.trim();
        }
        // Check properties
        String propVal = PROPS.getProperty(key);
        if (propVal != null && !propVal.isBlank()) {
            return propVal.trim();
        }
        return defaultValue;
    }

    public static int getInt(String key, int defaultValue) {
        try {
            String val = get(key, String.valueOf(defaultValue));
            return Integer.parseInt(val);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    public static boolean getBoolean(String key, boolean defaultValue) {
        String val = get(key, String.valueOf(defaultValue));
        return Boolean.parseBoolean(val);
    }

    public static void setRuntimeProperty(String key, String value) {
        PROPS.setProperty(key, value);
    }
}
