package ar.edu.itba.cripto.model;

import ar.edu.itba.cripto.model.steganography.SteganographyAlgorithm;

import java.io.File;

public class Steganographer {
    private final SteganographyAlgorithm stegAlgorithm;

    public Steganographer(final SteganographyAlgorithm stegAlgorithm) {
        this.stegAlgorithm = stegAlgorithm;
    }

    public int embed(byte[] msg, File cover, File outputFile) throws Exception {
        return this.stegAlgorithm.hideData(msg, cover, outputFile);
    }

    // TODO: Implement.
    public byte[] extract(File cover) throws Exception {
        return "Falta".getBytes();
    }
}
