package ar.edu.itba.cripto.model;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidParameterException;
import java.util.Arrays;

public class CipherHandle {
    public static final String KEY_FACTORY_ALGORITHM = "PBKDF2WithHmacSHA256";
    private final static String DEFAULT_PADDING = "PKCS5Padding";
    private static final byte[] FIXED_SALT = "FIXED_SALT".getBytes();
    private static final int ITERATION_COUNT = 1000;
    private final String password;
    private final Cipher cipher;
    private final String cipherName;
    private final String cipherMode;
    private final int keyLength;
    private boolean requiresIv = true;

    public CipherHandle(String cipher, String mode, String password) throws Exception {
        this.password = password;

        switch (cipher) {
            case "aes128" -> {
                cipherName = "AES";
                keyLength = 128;
            }
            case "aes192" -> {
                cipherName = "AES";
                keyLength = 192;
            }
            case "aes256" -> {
                cipherName = "AES";
                keyLength = 256;
            }
            case "des" -> {
                cipherName = "DESede";
                keyLength = 192;
            }
            default -> throw new InvalidParameterException("Invalid cipher: " + cipher);
        }

        switch (mode) {
            case "cfb" -> {
                this.cipherMode = "CFB8";
            }
            case "ofb" -> {
                this.cipherMode = "OFB";
            }
            case "cbc" -> {
                this.cipherMode = "CBC";
            }
            case "ecb" -> {
                this.cipherMode = "ECB";
                this.requiresIv = false;
            }
            default -> throw new InvalidParameterException("Invalid mode: " + mode);
        }

        String transformation = cipherName + "/" + cipherMode + "/" + DEFAULT_PADDING;
        this.cipher = Cipher.getInstance(transformation);
    }

    public byte[] encrypt(final byte[] data) throws Exception {

        KeyAndIv keyAndIv = generateSecretKey(password, cipherName, cipherMode);
        System.out.println("keyAndIv = " + keyAndIv);
        System.out.printf("key = %d bytes | iv = %d bytes%n", keyAndIv.key.length, keyAndIv.iv.length);
        if (requiresIv) {
            cipher.init(Cipher.ENCRYPT_MODE,
                new SecretKeySpec(keyAndIv.key, cipherName),
                new IvParameterSpec(keyAndIv.iv()));
        } else {
            System.out.println("key = " + Arrays.toString(keyAndIv.key));
            SecretKeySpec keySpec = new SecretKeySpec(keyAndIv.key, cipherName);
            System.out.println("keySpec = " + Arrays.toString(keySpec.getEncoded()) + "  " + keySpec.getAlgorithm());
            cipher.init(Cipher.ENCRYPT_MODE, keySpec);
        }

        return cipher.doFinal(data);
    }

    private KeyAndIv generateSecretKey(String password, String algorithm, String mode) throws Exception {
        System.out.println("CipherHandle.generateSecretKey");
        System.out.println("password = " + password + ", algorithm = " + algorithm + ", mode = " + mode);

        SecretKeyFactory skf = SecretKeyFactory.getInstance(KEY_FACTORY_ALGORITHM);
        PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), FIXED_SALT, ITERATION_COUNT,
            keyLength + cipher.getBlockSize() * (requiresIv ? 1 : 0));
        SecretKey secretKey = skf.generateSecret(spec);
        SecretKeySpec secretKeySpec = new SecretKeySpec(secretKey.getEncoded(), algorithm);
        byte[] bytes = secretKeySpec.getEncoded();
        System.out.println("bytes.length = " + bytes.length);

        byte[] key = Arrays.copyOfRange(bytes, 0, keyLength / 8);
        byte[] iv = new byte[0];
        System.out.println("cipher.getBlockSize() = " + cipher.getBlockSize());
        if (requiresIv) {
            iv = Arrays.copyOfRange(bytes, keyLength / 8, (keyLength / 8 + cipher.getBlockSize()));
        }
        return new KeyAndIv(key, iv);
    }

    public byte[] decrypt(final byte[] data) throws Exception {
        KeyAndIv keyAndIv = generateSecretKey(password, cipherName, this.cipherMode);
        if (!this.requiresIv) {
            cipher.init(Cipher.DECRYPT_MODE,
                new SecretKeySpec(keyAndIv.key(), cipherName));
        } else {
            cipher.init(Cipher.DECRYPT_MODE,
                new SecretKeySpec(keyAndIv.key(), cipherName),
                new IvParameterSpec(keyAndIv.iv()));
        }
        return cipher.doFinal(data);
    }

    @Override
    public String toString() {
        return "CipherHandle{" +
            "password='" + password + '\'' +
            ", cipher=" + cipher +
            ", cipherName='" + cipherName + '\'' +
            ", cipherMode='" + cipherMode + '\'' +
            ", keyLength=" + keyLength +
            '}';
    }


    protected record KeyAndIv(byte[] key, byte[] iv) {
    }

}

