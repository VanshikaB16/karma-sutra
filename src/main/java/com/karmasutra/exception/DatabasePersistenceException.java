package com.karmasutra.exception;

public class DatabasePersistenceException extends CosmicException {
    public DatabasePersistenceException(String message) {
        super(message);
    }

    public DatabasePersistenceException(String message, Throwable cause) {
        super(message, cause);
    }
}
