package com.school.violationsystem.util;

import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.HexFormat;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public class PasswordUtil {
    private static final String ALGORITHM = "PBKDF2WithHmacSHA256";
    private static final String HASH_PREFIX = "pbkdf2_sha256";
    private static final int ITERATIONS = 310_000;
    private static final int KEY_LENGTH_BITS = 256;
    private static final int SALT_LENGTH_BYTES = 16;
    private static final int MIN_ACCEPTED_ITERATIONS = 100_000;
    private static final int MAX_ACCEPTED_ITERATIONS = 1_000_000;
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    private PasswordUtil() {
    }

    public static String hash(char[] password) {
        if (password == null || password.length == 0) {
            throw new IllegalArgumentException("Password must not be empty.");
        }

        byte[] salt = new byte[SALT_LENGTH_BYTES];
        SECURE_RANDOM.nextBytes(salt);
        byte[] derivedKey = deriveKey(password, salt, ITERATIONS);
        return HASH_PREFIX + "$" + ITERATIONS + "$"
                + Base64.getEncoder().withoutPadding().encodeToString(salt) + "$"
                + Base64.getEncoder().withoutPadding().encodeToString(derivedKey);
    }

    public static boolean matches(char[] password, String storedHash) {
        if (password == null || password.length == 0 || storedHash == null) {
            return false;
        }

        String[] components = storedHash.split("\\$", -1);
        if (components.length != 4 || !HASH_PREFIX.equals(components[0])) {
            return false;
        }

        try {
            int iterations = Integer.parseInt(components[1]);
            if (iterations < MIN_ACCEPTED_ITERATIONS || iterations > MAX_ACCEPTED_ITERATIONS) {
                return false;
            }

            Base64.Decoder decoder = Base64.getDecoder();
            byte[] salt = decoder.decode(components[2]);
            byte[] expectedKey = decoder.decode(components[3]);
            if (salt.length != SALT_LENGTH_BYTES || expectedKey.length != KEY_LENGTH_BITS / Byte.SIZE) {
                return false;
            }

            byte[] actualKey = deriveKey(password, salt, iterations);
            return MessageDigest.isEqual(expectedKey, actualKey);
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    public static boolean matchesLegacySha256(char[] password, String storedHash) {
        if (password == null || password.length == 0 || storedHash == null || storedHash.length() != 64) {
            return false;
        }

        try {
            byte[] expectedHash = HexFormat.of().parseHex(storedHash);
            byte[] actualHash = MessageDigest.getInstance("SHA-256")
                    .digest(new String(password).getBytes());
            return MessageDigest.isEqual(expectedHash, actualHash);
        } catch (IllegalArgumentException e) {
            return false;
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("Legacy password verification is unavailable.", e);
        }
    }

    private static byte[] deriveKey(char[] password, byte[] salt, int iterations) {
        PBEKeySpec keySpec = new PBEKeySpec(password, salt, iterations, KEY_LENGTH_BITS);
        try {
            return SecretKeyFactory.getInstance(ALGORITHM).generateSecret(keySpec).getEncoded();
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException("Password hashing is unavailable.", e);
        } finally {
            keySpec.clearPassword();
        }
    }
}
