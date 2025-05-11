package se.rikardbq.util;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Base64;

public class PasswordHasher {
    private static final byte[] salt = {
            48, 50, 102, 56,
            48, 50, 55, 51,
            48, 55, 51, 51,
            48, 98, 51, 53,
            97, 100, 97, 48,
            51, 55, 49, 51,
            97, 51, 54, 50,
            55, 57, 50, 97
    };

    public PasswordHasher() {
    }

    public static Encoder getEncoder() {
        return new Encoder(PasswordHasher.salt);
    }

    public static Encoder getEncoder(byte[] salt) {
        return new Encoder(salt);
    }

    public static class Encoder {
        private final byte[] salt;

        public Encoder(byte[] salt) {
            this.salt = salt;
        }

        public String encode(String password) throws NoSuchAlgorithmException, InvalidKeySpecException {
            KeySpec spec = new PBEKeySpec(password.toCharArray(), this.salt, 131072, 256);
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");

            return Base64.getEncoder().encodeToString(factory.generateSecret(spec).getEncoded());
        }
    }
}
