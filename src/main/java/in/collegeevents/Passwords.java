package in.collegeevents;

import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public final class Passwords {
    private static final SecureRandom RANDOM = new SecureRandom();
    private Passwords() {}
    public static String newSalt() {
        byte[] salt = new byte[16]; RANDOM.nextBytes(salt);
        return Base64.getEncoder().encodeToString(salt);
    }
    public static String hash(String password, String saltText) {
        PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), Base64.getDecoder().decode(saltText), 120000, 256);
        try {
            return Base64.getEncoder().encodeToString(SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256").generateSecret(spec).getEncoded());
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException("Password hashing is unavailable", e);
        } finally { spec.clearPassword(); }
    }
    public static boolean matches(String password, String salt, String expectedHash) {
        return MessageDigest.isEqual(hash(password, salt).getBytes(java.nio.charset.StandardCharsets.UTF_8),
                                     expectedHash.getBytes(java.nio.charset.StandardCharsets.UTF_8));
    }
}