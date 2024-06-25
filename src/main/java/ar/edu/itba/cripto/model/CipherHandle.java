package ar.edu.itba.cripto.model;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Arrays;
import java.util.Base64;

public class CipherHandle {
    public static final String KEY_FACTORY_ALGORITHM = "PBKDF2WithHmacSHA256";
    private static final String DEFAULT_PADDING = "PKCS5Padding";
    public static final String NO_PADDING = "NoPadding";
    private static final byte[] FIXED_SALT = new byte[]{0, 0, 0, 0, 0, 0, 0, 0};
    private static final int ITERATION_COUNT = 10000;

    private final String password;
    private final Cipher cipher;
    private final String cipherName;
    private final String cipherMode;
    private final int keyLength;
    private boolean requiresIv = true;

    public String getCipherName() {
        return cipherName;
    }

    public String getCipherMode() {
        return cipherMode;
    }

    public CipherHandle(String cipher, String mode, String password) {
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

        String transformation = cipherName + "/" + cipherMode + "/" + NO_PADDING;
        try {
            this.cipher = Cipher.getInstance(transformation);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            throw new InvalidParameterException(e);
        }
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

    public void printKeyAndIv() throws NoSuchAlgorithmException, InvalidKeySpecException {
        KeyAndIv keyAndIv = generateSecretKey(password, cipherName, cipherMode);
        System.out.println(keyAndIv);
    }

    private KeyAndIv generateSecretKey(String password, String algorithm, String mode)
        throws NoSuchAlgorithmException, InvalidKeySpecException {

        System.out.println("CipherHandle.generateSecretKey");
        System.out.println("password = " + password + ", algorithm = " + algorithm + ", mode = " + mode + "keyLength = " + keyLength);

        SecretKeyFactory skf = SecretKeyFactory.getInstance(KEY_FACTORY_ALGORITHM);
        KeySpec spec = new PBEKeySpec(password.toCharArray(), FIXED_SALT, ITERATION_COUNT,
            keyLength + cipher.getBlockSize() * 8);

        byte[] hash = skf.generateSecret(spec).getEncoded();

        byte[] key = Arrays.copyOfRange(hash, 0, keyLength / 8);
        byte[] iv = Arrays.copyOfRange(hash, keyLength / 8, keyLength / 8 + cipher.getBlockSize());

        return new KeyAndIv(key, iv);
    }

    public byte[] decrypt(final byte[] data) throws Exception {
        KeyAndIv keyAndIv = generateSecretKey(password, cipherName, this.cipherMode);
        System.out.println("keyAndIv = " + keyAndIv);

        if (this.requiresIv) {
            cipher.init(Cipher.DECRYPT_MODE,
                new SecretKeySpec(keyAndIv.key(), cipherName),
                new IvParameterSpec(keyAndIv.iv()));
        } else {
            cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(keyAndIv.key(), cipherName));
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


    public record KeyAndIv(byte[] key, byte[] iv) {
        @Override
        public boolean equals(final Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            final KeyAndIv keyAndIv = (KeyAndIv) o;
            return Arrays.equals(key, keyAndIv.key) && Arrays.equals(iv, keyAndIv.iv);
        }

        @Override
        public int hashCode() {
            int result = Arrays.hashCode(key);
            result = 31 * result + Arrays.hashCode(iv);
            return result;
        }

        @Override
        public String toString() {
            return "key = " + Base64.getEncoder().encodeToString(key) + "\n"
                + "iv = " + Base64.getEncoder().encodeToString(iv);
        }
    }
}

