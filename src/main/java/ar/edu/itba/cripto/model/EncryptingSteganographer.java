package ar.edu.itba.cripto.model;

import ar.edu.itba.cripto.model.steganography.SteganographyAlgorithm;
import org.apache.commons.io.EndianUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.security.InvalidParameterException;
import java.util.Arrays;

public class EncryptingSteganographer extends Steganographer {
    private final CipherHandle cipherHandle;

    @Override
    public int embed(final File inputFile, final File cover, final File outputFile) throws Exception {
        System.out.println("Steganographer.embed " + inputFile);

        byte[] data = Files.readAllBytes(inputFile.toPath());
        int messageLength = data.length;
        String extension = FilenameUtils.getExtension(String.valueOf(inputFile));

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        EndianUtils.writeSwappedInteger(baos, messageLength);
        baos.write(data);
        baos.write(".".getBytes(StandardCharsets.UTF_8));
        baos.write(extension.getBytes(StandardCharsets.UTF_8));
        baos.write('\0');

        byte[] cyphertext = this.cipherHandle.encrypt(baos.toByteArray());
        int cypherSize = cyphertext.length;

        ByteArrayOutputStream cypher = new ByteArrayOutputStream();
        EndianUtils.writeSwappedInteger(cypher, cypherSize);
        cypher.write(cyphertext);

        return this.stegAlgorithm.hideData(cypher.toByteArray(), cover, outputFile);
    }

    @Override
    public void extract(File cover) throws Exception {
        byte[] rawData = this.stegAlgorithm.extractRawData(cover);
        int cypherLength = EndianUtils.readSwappedInteger(rawData, 0);
        byte[] plaintext = cipherHandle.decrypt(Arrays.copyOfRange(rawData, 4, 4 + cypherLength));
        System.out.println("decryption: " + Arrays.toString(plaintext));

        int messageLength = readLength(plaintext);
        System.out.println("messageLength = " + messageLength);
        byte[] fileData = readFileData(plaintext, 4, 4 + messageLength);
        System.out.println("fileData = " + Arrays.toString(fileData));
        String extension = readExtension(plaintext, 4 + messageLength);
        System.out.println("extension = " + extension);

        FileUtils.writeByteArrayToFile(new File("../output" + extension), fileData);
    }

    public EncryptingSteganographer(final SteganographyAlgorithm stegAlgorithm, String cipher, String mode, String password) throws Exception {
        super(stegAlgorithm);
        try {
            this.cipherHandle = new CipherHandle(cipher, mode, password);
        } catch (Exception e) {
            throw e;
        }
    }

    private class CipherHandle {
        private final static String DEFAULT_PADDING = "PKCS5Padding";
        private static final byte[] FIXED_SALT = "FIXED_SALTA".getBytes();
        private final String password;
        private final Cipher cipher;
        private final String cipherName;
        private final String cipherMode;
        private final int keyLength;
        private SecretKey secretKey;

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
                    cipherName = "DES";
                    keyLength = 64;
                }
                default -> {
                    System.out.println("Default switch");
                    throw new InvalidParameterException("Invalid cipher: " + cipher);
                }
            }
            ;

            this.cipherMode = switch (mode) {
                case "cfb" -> "CFB8";
                case "ofb" -> "OFB";
                case "cbc" -> "CBC";
                case "ecb" -> "ECB";
                default -> throw new InvalidParameterException("Invalid mode: " + mode);
            };

            String transformation = cipherName + "/" + cipherMode + "/" + DEFAULT_PADDING;
            System.out.println("Transformation = " + transformation);
            this.cipher = Cipher.getInstance(transformation);
            this.secretKey = generateSecretKey(password, this.cipher.getAlgorithm());
            System.out.println(this);
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
            SecretKey secretKey = generateSecretKey(password, this.cipher.getAlgorithm());
            cipher.init(Cipher.ENCRYPT_MODE, this.secretKey);
            byte[] cyphertext = cipher.doFinal(data);
            return cyphertext;
        }

        private SecretKey generateSecretKey(String password, String algorithm) throws Exception {
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");

            PBEKeySpec keySpec = new PBEKeySpec(password.toCharArray(), FIXED_SALT, 1, keyLength);

            KeyGenerator keygen = KeyGenerator.getInstance("DES");
            SecretKey desKey = keygen.generateKey();

            return desKey;
        }

        public byte[] decrypt(final byte[] data) throws Exception {
            SecretKey secretKey = generateSecretKey(password, this.cipher.getAlgorithm());
            cipher.init(Cipher.DECRYPT_MODE, this.secretKey);
            byte[] plaintext = cipher.doFinal(data);
            return plaintext;
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
    }
}
