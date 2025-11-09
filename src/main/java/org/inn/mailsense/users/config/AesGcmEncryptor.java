package org.inn.mailsense.users.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Objects;

@Component
public class AesGcmEncryptor {

    private final byte[] key;

    /**
     * Accepts:
     *  - a Base64-encoded 32-byte key (recommended)
     *  - OR a raw string whose UTF-8 byte length is exactly 32 (less recommended)
     *
     * To avoid ambiguity, prefer Base64 keys (generate with `openssl rand -base64 32`).
     */
    public AesGcmEncryptor(@Value("${encryption.key:}") String baseKey) {
        if (baseKey == null || baseKey.isEmpty()) {
            throw new IllegalArgumentException(
                    "Missing encryption.key property. Provide a 32-byte key (Base64 recommended).");
        }

        byte[] k = tryDecodeBase64(baseKey);
        if (k == null) {
            // fallback to raw UTF-8 bytes
            k = baseKey.getBytes(StandardCharsets.UTF_8);
        }

        if (k.length != 32) {
            throw new IllegalArgumentException(
                    "Encryption key must be 32 bytes (256 bits). Provided key has " + k.length + " bytes. " +
                            "Recommend using a Base64-encoded key generated with: openssl rand -base64 32");
        }

        this.key = k.clone();
    }

    private byte[] tryDecodeBase64(String s) {
        try {
            byte[] decoded = Base64.getDecoder().decode(s);
            // if decode produced something, return it; else return null to try raw fallback
            return (decoded != null && decoded.length > 0) ? decoded : null;
        } catch (IllegalArgumentException ex) {
            return null;
        }
    }

    public String encrypt(String plaintext) throws Exception {
        byte[] iv = new byte[12];
        SecureRandom random = new SecureRandom();
        random.nextBytes(iv);

        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        SecretKeySpec keySpec = new SecretKeySpec(key, "AES");
        GCMParameterSpec spec = new GCMParameterSpec(128, iv);
        cipher.init(Cipher.ENCRYPT_MODE, keySpec, spec);
        byte[] cipherText = cipher.doFinal(Objects.requireNonNull(plaintext).getBytes(StandardCharsets.UTF_8));
        byte[] combined = new byte[iv.length + cipherText.length];
        System.arraycopy(iv, 0, combined, 0, iv.length);
        System.arraycopy(cipherText, 0, combined, iv.length, cipherText.length);
        return Base64.getEncoder().encodeToString(combined);
    }

    public String decrypt(String base64) throws Exception {
        byte[] combined = Base64.getDecoder().decode(base64);
        if (combined.length < 13) {
            throw new IllegalArgumentException("Invalid cipher text");
        }
        byte[] iv = new byte[12];
        System.arraycopy(combined, 0, iv, 0, 12);
        byte[] cipherText = new byte[combined.length - 12];
        System.arraycopy(combined, 12, cipherText, 0, cipherText.length);

        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        SecretKeySpec keySpec = new SecretKeySpec(key, "AES");
        GCMParameterSpec spec = new GCMParameterSpec(128, iv);
        cipher.init(Cipher.DECRYPT_MODE, keySpec, spec);
        byte[] plain = cipher.doFinal(cipherText);
        return new String(plain, StandardCharsets.UTF_8);
    }
}
