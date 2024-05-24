package ar.edu.itba.cripto.steganography;

import ar.edu.itba.cripto.model.BMPV3Image;

import java.io.File;
import java.io.IOException;

public abstract class SteganographyAlgorithm {

    public static SteganographyAlgorithm getInstance(String name) {
        switch (name) {
            case "LSB1" -> {
                return new LSBAlgorithm();
            }
            case "LSB4" -> {
                return new LSB4Algorithm();
            }
            case "LSBI" -> {
                return new LSBIAlgorithm();
            }
            default -> throw new RuntimeException("Invalid steganography algorithm");
        }
    }

    public abstract String getName();

    public abstract void hideData(byte[] data, File cover, File outputFile) throws IOException;

    public abstract byte[] extractData(File image) throws IOException;

    abstract boolean canHideData(final byte[] data, BMPV3Image coverImage);
}
