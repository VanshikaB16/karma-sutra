package com.karmasutra.exception;

/**
 * Base unchecked exception for all cosmic bureaucratic failures.
 */
public class CosmicException extends RuntimeException {
    public CosmicException(String message) {
        super(message);
    }

    public CosmicException(String message, Throwable cause) {
        super(message, cause);
    }
}
