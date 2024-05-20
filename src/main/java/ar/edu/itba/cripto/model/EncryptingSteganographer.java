package ar.edu.itba.cripto.model;

import ar.edu.itba.cripto.steganography.SteganographyAlgorithm;
import org.apache.commons.io.EndianUtils;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.security.InvalidParameterException;
import java.util.Arrays;

public class EncryptingSteganographer extends Steganographer {
    private final CipherHandle cipherHandle;
    public static final Logger logger = LoggerFactory.getLogger(EncryptingSteganographer.class);

    @Override
    public void embed(final File inputFile, final File cover, final File outputFile) throws Exception {
        // Construimos el mensaje a esconder con el formato pedido
        byte[] fileInfo = buildByteArray(inputFile);

        // Encriptado
        byte[] cyphertext = this.cipherHandle.encrypt(fileInfo);

        // Construimos el array del texto cifrado.
        byte[] cypherMessage = buildCypherByteArray(cyphertext);

        this.stegAlgorithm.hideData(cypherMessage, cover, outputFile);
    }

    static byte[] buildCypherByteArray(byte[] cyphertext) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        EndianUtils.writeSwappedInteger(baos, cyphertext.length);
        baos.write(cyphertext);

        return baos.toByteArray();
    }

    @Override
    public void extract(File cover, String outputFile) throws Exception {
        System.out.println("EncryptingSteganographer.extract");
        System.out.println("cover = " + cover + ", outputFile = " + outputFile);

        byte[] coverData = this.stegAlgorithm.extractData(cover);

        // Descompongo el mensaje cifrado: tamaño | cifrado
        int cypherLength = EndianUtils.readSwappedInteger(coverData, 0);
        System.out.println("cypherLength = " + cypherLength);
        byte[] plaintext = cipherHandle.decrypt(Arrays.copyOfRange(coverData, LENGTH_SIZE, LENGTH_SIZE + cypherLength));

        // Descompongo el mensaje plano: tamaño | datos | extensión
        int messageLength = readLength(plaintext);
        System.out.println("messageLength = " + messageLength);
        byte[] fileData = readFileData(plaintext, LENGTH_SIZE, LENGTH_SIZE + messageLength);
        String extension = readExtension(plaintext, 4 + messageLength);
        System.out.println("extension = " + extension);

        System.out.println("Escribiendo resultado en: " + outputFile + extension);
        FileUtils.writeByteArrayToFile(new File(outputFile + extension), fileData);
    }

    public EncryptingSteganographer(final SteganographyAlgorithm stegAlgorithm, String cipher, String mode, String password) throws Exception {
        super(stegAlgorithm);
        this.cipherHandle = new CipherHandle(cipher, mode, password);
        System.out.println("cipherHandle = " + cipherHandle);
    }

    private class CipherHandle {
        private final static String DEFAULT_PADDING = "PKCS5Padding";
        public static final String KEY_FACTORY_ALGORITHM = "PBKDF2WithHmacSHA256";
        private static final byte[] FIXED_SALT = "FIXED_SALT".getBytes();
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
                    cipherName = "DES";
                    keyLength = 64;
                }
                default -> {
                    throw new InvalidParameterException("Invalid cipher: " + cipher);
                }
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
            System.out.println(String.format("key = %d bytes | iv = %d bytes", keyAndIv.key.length, keyAndIv.iv.length));
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

            byte[] cyphertext = cipher.doFinal(data);

            return cyphertext;
        }

        private KeyAndIv generateSecretKey(String password, String algorithm, String mode) throws Exception {
            System.out.println("CipherHandle.generateSecretKey");
            System.out.println("password = " + password + ", algorithm = " + algorithm + ", mode = " + mode);

            SecretKeyFactory skf = SecretKeyFactory.getInstance(KEY_FACTORY_ALGORITHM);
            PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), FIXED_SALT, 1,
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
            if (cipherMode.equals("ECB")) {
                cipher.init(Cipher.DECRYPT_MODE,
                    new SecretKeySpec(keyAndIv.key(), cipherName));
            } else {
                cipher.init(Cipher.DECRYPT_MODE,
                    new SecretKeySpec(keyAndIv.key(), cipherName),
                    new IvParameterSpec(keyAndIv.iv()));
            }
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

        protected record KeyAndIv(byte[] key, byte[] iv) {
        }

        ;
    }
}
