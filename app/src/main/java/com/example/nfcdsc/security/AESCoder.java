package com.example.nfcdsc.security;

import java.security.Key;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import android.util.Base64;

public final class AESCoder {
    private static final String KEY_ALGORITHM = "AES";
    private static final int KEY_SIZE = 128;
    private static final String CIPHER_ALGORITHM = "AES/ECB/PKCS5Padding";
    private static final String ENCODING = "UTF-8";

    public static byte[] generateKey() throws Exception {
        KeyGenerator keyGenerator = KeyGenerator.getInstance(KEY_ALGORITHM);
        keyGenerator.init(KEY_SIZE);
        SecretKey secretKey = keyGenerator.generateKey();
        return secretKey.getEncoded();
    }

    public static String encrypt(String plaintext, byte[] key) throws Exception {
        byte[] data = plaintext.getBytes(ENCODING);
        SecretKey secretKey = new SecretKeySpec(key, KEY_ALGORITHM);
        Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        String ciphertext = base64(cipher.doFinal(data));
        return ciphertext;
    }

    public static String decrypt(String ciphertext, byte[] key) throws Exception {
        byte[] data = base64(ciphertext);
        SecretKey secretKey = new SecretKeySpec(key, KEY_ALGORITHM);
        Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        String plaintext = new String(cipher.doFinal(data), ENCODING);
        return plaintext;
    }

    public static String base64(byte[] data) {
        return Base64.encodeToString(data, Base64.DEFAULT);
    }

    public static byte[] base64(String data) {
        return Base64.decode(data, Base64.DEFAULT);
    }

}
