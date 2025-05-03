package se.rikardbq.util;

import org.apache.commons.codec.binary.Hex;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;

public class PasswordHasher {
    String password;
    private final byte[] salt = {
            48, 50, 102, 56,
            48, 50, 55, 51,
            48, 55, 51, 51,
            48, 98, 51, 53,
            97, 100, 97, 48,
            51, 55, 49, 51,
            97, 51, 54, 50,
            55, 57, 50, 97
    };

    public PasswordHasher(String password) {
        this.password = password;
    }

    public String encode() throws NoSuchAlgorithmException, InvalidKeySpecException {
        KeySpec spec = new PBEKeySpec(this.password.toCharArray(), this.salt, 64576, 256);
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");

        return Hex.encodeHexString(factory.generateSecret(spec).getEncoded());
    }

//    public static void main(String[] args) throws NoSuchAlgorithmException, InvalidKeySpecException {
//        PasswordHasher hasher = new PasswordHasher("rikard123");
//        System.out.println(hasher.encode());
//    }
}
