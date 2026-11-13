package com.karmasutra.config;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Manages JDBC connections and automated schema initialization for MySQL.
 */
public final class DatabaseConfig {

    private static final Logger LOGGER = Logger.getLogger(DatabaseConfig.class.getName());

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            LOGGER.log(Level.SEVERE, "MySQL JDBC Driver not found on classpath", e);
        }
    }

    private DatabaseConfig() {
    }

    public static String getJdbcUrl() {
        String host = AppConfig.get("db.host", "127.0.0.1");
        int port = AppConfig.getInt("db.port", 3307);
        String name = AppConfig.get("db.name", "karma_sutra_db");
        String params = AppConfig.get("db.params", "useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC&createDatabaseIfNotExist=true&autoReconnect=true&connectTimeout=5000&socketTimeout=30000&tcpKeepAlive=true");
        return String.format("jdbc:mysql://%s:%d/%s?%s", host, port, name, params);
    }

    public static String getRootServerJdbcUrl() {
        String host = AppConfig.get("db.host", "127.0.0.1");
        int port = AppConfig.getInt("db.port", 3307);
        String params = "useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC&connectTimeout=5000&autoReconnect=true";
        return String.format("jdbc:mysql://%s:%d/?%s", host, port, params);
    }

    public static String getDbUser() {
        return AppConfig.get("db.user", "root");
    }

    public static String getDbPassword() {
        return AppConfig.get("db.password", "");
    }

    /**
     * Gets a fresh connection to the Karma Sutra database.
     */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(getJdbcUrl(), getDbUser(), getDbPassword());
    }

    /**
     * Diagnostic connectivity test method.
     */
    public static boolean testConnection() {
        try (Connection conn = getConnection()) {
            return conn != null && !conn.isClosed() && conn.isValid(3);
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "Database connectivity probe failed: " + e.getMessage());
            return false;
        }
    }

    /**
     * Verifies connectivity and auto-initializes tables from schema.sql if needed.
     */
    public static synchronized boolean initializeDatabase() {
        String dbName = AppConfig.get("db.name", "karma_sutra_db");
        // Step 1: Ensure database exists
        try (Connection rootConn = DriverManager.getConnection(getRootServerJdbcUrl(), getDbUser(), getDbPassword());
             Statement rootStmt = rootConn.createStatement()) {
            rootStmt.executeUpdate("CREATE DATABASE IF NOT EXISTS " + dbName + " CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;");
            LOGGER.info("Verified existence of database: " + dbName);
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "Direct root connection failed, attempting default connection: " + e.getMessage());
        }

        // Step 2: Connect to database and check if tables exist
        try (Connection conn = getConnection()) {
            if (!tablesExist(conn)) {
                LOGGER.info("Tables not found. Running automated schema.sql migration...");
                executeSchemaScript(conn);
                LOGGER.info("Database schema initialized successfully.");
            } else {
                LOGGER.info("Database schema already verified and active.");
            }

            // Ensure default seed devotees have valid PBKDF2 hashes
            try {
                String devHash = com.karmasutra.util.PasswordUtil.hashPassword("Devotee@108", "1a2b3c4d5e6f7a8b");
                String adminHash = com.karmasutra.util.PasswordUtil.hashPassword("CosmicAdmin@108", "7f8a9b0c1d2e3f4a");
                try (Statement stmt = conn.createStatement()) {
                    stmt.executeUpdate("UPDATE users SET password_hash = '" + devHash + "' WHERE username = 'devotee1' AND salt = '1a2b3c4d5e6f7a8b'");
                    stmt.executeUpdate("UPDATE users SET password_hash = '" + adminHash + "' WHERE username = 'admin' AND salt = '7f8a9b0c1d2e3f4a'");
                }
            } catch (Exception ex) {
                LOGGER.warning("Could not synchronize seed user password hashes: " + ex.getMessage());
            }

            return true;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Failed to initialize or connect to MySQL database: " + e.getMessage(), e);
            return false;
        }
    }

    private static boolean tablesExist(Connection conn) {
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SHOW TABLES LIKE 'complaints'")) {
            return rs.next();
        } catch (SQLException e) {
            return false;
        }
    }

    private static void executeSchemaScript(Connection conn) {
        try (InputStream is = DatabaseConfig.class.getClassLoader().getResourceAsStream("schema.sql")) {
            if (is == null) {
                LOGGER.severe("schema.sql resource not found on classpath!");
                return;
            }
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
                StringBuilder sql = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    String trimmed = line.trim();
                    if (trimmed.startsWith("--") || trimmed.startsWith("/*") || trimmed.isEmpty()) {
                        continue;
                    }
                    sql.append(line).append("\n");
                    if (trimmed.endsWith(";")) {
                        String statementToRun = sql.toString().trim();
                        // Strip trailing semicolon
                        if (statementToRun.endsWith(";")) {
                            statementToRun = statementToRun.substring(0, statementToRun.length() - 1);
                        }
                        // Skip USE commands since connection is already bound
                        if (!statementToRun.toUpperCase().startsWith("USE ") && !statementToRun.toUpperCase().startsWith("CREATE DATABASE ")) {
                            try (Statement stmt = conn.createStatement()) {
                                stmt.execute(statementToRun);
                            } catch (SQLException ex) {
                                LOGGER.warning("Ignored script execution note: " + ex.getMessage());
                            }
                        }
                        sql.setLength(0);
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error executing schema.sql", e);
        }
    }
}
