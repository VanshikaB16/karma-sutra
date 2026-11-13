package com.karmasutra.exception;

public class DivineServiceUnavailableException extends CosmicException {
    public DivineServiceUnavailableException(String message) {
        super(message);
    }

    public DivineServiceUnavailableException(String message, Throwable cause) {
        super(message, cause);
    }
}
