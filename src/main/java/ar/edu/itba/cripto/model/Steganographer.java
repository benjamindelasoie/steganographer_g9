package ar.edu.itba.cripto.model;

import ar.edu.itba.cripto.model.steganography.SteganographyAlgorithm;

import java.io.File;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Steganographer {
    private final CipherHandle cipherHandle;
    private final SteganographyAlgorithm stegAlgorithm;
    private final String password;

    public Steganographer(final CipherHandle cipher, final SteganographyAlgorithm stegAlgorithm, final String password) {
        this.cipherHandle = cipher;
        this.stegAlgorithm = stegAlgorithm;
        this.password = password;
    }

    public int embed(byte[] data, File cover, String outputFile) throws Exception {

        return 0;
    }

    public int extract() {

        return 0;
    }
}
