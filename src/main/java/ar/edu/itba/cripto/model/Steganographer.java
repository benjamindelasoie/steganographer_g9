package ar.edu.itba.cripto.model;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.File;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Steganographer {
    private final MessageDigest digest;
    private final CipherHandle cipherHandle;
    private final SteganographyAlgorithm stegAlgorithm;
    private final String password;

    public Steganographer(final CipherHandle cipher, final SteganographyAlgorithm stegAlgorithm, final String password, final MessageDigest digest)
            throws NoSuchAlgorithmException {
        this.cipherHandle = cipher;
        this.stegAlgorithm = stegAlgorithm;
        this.password = password;
        this.digest = digest;
    }

    public String getPassword() {
        return password;
    }

    public int embed(byte[] data, File cover, String outputFile) throws Exception {
        if (encryptionRequested()) {
            byte[] cyphertext = cipherHandle.encrypt(data, this.password, this.digest);
        }

        return 0;
    }

    public int extract() {

        return 0;
    }


    private boolean encryptionRequested() {
        return password != null;
    }
}
