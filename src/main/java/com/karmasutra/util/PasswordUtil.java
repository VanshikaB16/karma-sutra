package com.karmasutra.util;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.HexFormat;

/**
 * Enterprise cryptographic password hashing utility using PBKDF2WithHmacSHA256.
 * Zero external dependencies, NIST compliant, timing-attack resistant.
 */
public final class PasswordUtil {

    private static final int ITERATIONS = 65_536;
    private static final int KEY_LENGTH = 256;
    private static final String ALGORITHM = "PBKDF2WithHmacSHA256";
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    private PasswordUtil() {
        // Utility class
    }

    /**
     * Generates a secure cryptographic random hex salt.
     */
    public static String generateSalt() {
        byte[] salt = new byte[16];
        SECURE_RANDOM.nextBytes(salt);
        return HexFormat.of().formatHex(salt);
    }

    /**
     * Computes the PBKDF2 hash of a password using the given hex salt.
     */
    public static String hashPassword(String password, String hexSalt) {
        if (password == null || hexSalt == null) {
            throw new IllegalArgumentException("Password and salt cannot be null");
        }
        try {
            byte[] saltBytes = HexFormat.of().parseHex(hexSalt);
            PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), saltBytes, ITERATIONS, KEY_LENGTH);
            SecretKeyFactory factory = SecretKeyFactory.getInstance(ALGORITHM);
            byte[] hash = factory.generateSecret(spec).getEncoded();
            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new IllegalStateException("Cryptographic algorithm unavailable", e);
        }
    }

    /**
     * Verifies whether a candidate password matches the expected hash.
     */
    public static boolean verifyPassword(String candidatePassword, String salt, String expectedHash) {
        if (candidatePassword == null || salt == null || expectedHash == null) {
            return false;
        }
        String candidateHash = hashPassword(candidatePassword, salt);
        return java.security.MessageDigest.isEqual(
                candidateHash.getBytes(java.nio.charset.StandardCharsets.UTF_8),
                expectedHash.getBytes(java.nio.charset.StandardCharsets.UTF_8)
        );
    }
}
