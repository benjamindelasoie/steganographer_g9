package ar.edu.itba.cripto.model;

import ar.edu.itba.cripto.model.steganography.SteganographyAlgorithm;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.io.File;
import java.nio.file.Files;
import java.security.InvalidParameterException;
import java.security.NoSuchAlgorithmException;

public class EncryptingSteganographer extends Steganographer {
    private final CipherHandle cipherHandle;

    @Override
    public int embed(final File inputFile, final File cover, final File outputFile) throws Exception {
        // TODO: Implement
        byte[] cyphertext = cipherHandle.encrypt(Files.readAllBytes(inputFile.toPath()));
        return this.stegAlgorithm.hideData(cyphertext, cover, outputFile);
    }

    @Override
    public void extract(File cover) throws Exception {
        // TODO: Implement
    }

    public EncryptingSteganographer(final SteganographyAlgorithm stegAlgorithm, String cipher, String mode, String password) {
        super(stegAlgorithm);
        try {
            this.cipherHandle = new CipherHandle(cipher, mode, password);
        } catch (Exception e) {
            throw new RuntimeException("Bad parameters for cipher");
        }
    }

    private class CipherHandle {
        private final static String DEFAULT_PADDING = "PKCS5Padding";

        private final String password;
        private final Cipher cipher;
        private final String cipherName;
        private final String cipherMode;
        private final int keyLength;

        public CipherHandle(String cipher, String mode, String password) throws NoSuchPaddingException, NoSuchAlgorithmException {
            this.password = password;

            switch (cipher) {
                case "aes128":
                    cipherName = "AES";
                    keyLength = 128;
                    break;
                case "aes192":
                    cipherName = "AES";
                    keyLength = 192;
                    break;
                case "aes256":
                    cipherName = "AES";
                    keyLength = 256;
                    break;
                case "des":
                    cipherName = "DES";
                    keyLength = 56;
                default:
                    throw new InvalidParameterException("Invalid cipher: " + cipher);
            }
            ;

            this.cipherMode = switch (mode) {
                case "cfb" -> "CFB8";
                case "ofb" -> "OFB";
                case "cbc" -> "CBC";
                case "ecb" -> "ECB";
                default -> throw new InvalidParameterException("Invalid mode: " + mode);
            };

            this.cipher = Cipher.getInstance(cipherName + "/" + cipherMode + "/" + DEFAULT_PADDING);
        }

        public Cipher getCipher() {
            return cipher;
        }

        public String getCipherName() {
            return cipherName;
        }

        public String getCipherMode() {
            return cipherMode;
        }

        public int getKeyLength() {
            return keyLength;
        }

        public byte[] encrypt(final byte[] data) throws Exception {
            SecretKey secretKey = generateSecretKey(password);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            byte[] cyphertext = cipher.doFinal(data);
            return cyphertext;
        }

        private SecretKey generateSecretKey(String password) throws Exception {
            if (password == null) {
                throw new RuntimeException("Password isn't defined.");
            }

            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            // TODO: Checkear si se genera bien (sin salt e iteration count = 1). Si no reemplazar por otro constructor.
            PBEKeySpec keySpec = new PBEKeySpec(password.toCharArray());

            return keyFactory.generateSecret(keySpec);
        }

        public byte[] decrypt(final byte[] data) throws Exception {
            SecretKey secretKey = generateSecretKey(password);
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            byte[] plaintext = cipher.doFinal(data);
            return plaintext;
        }
    }
}
