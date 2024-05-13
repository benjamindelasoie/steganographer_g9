package ar.edu.itba.cripto.model.steganography;

import ar.edu.itba.cripto.model.BMPV3Image;

import java.awt.image.BufferedImage;
import java.io.File;

public abstract class SteganographyAlgorithm {


    public static SteganographyAlgorithm getInstance(String name) {
        switch(name) {
            case "LSB" -> {
                return new LSBAlgorithm(1);
            }
            case "LSB4" -> {
                return new LSBAlgorithm(4);
            }
            case "LSBI" -> {
                return new LSBIAlgorithm();
            }
            default -> throw new RuntimeException("Invalid steganography algorithm");
        }
    }

    public abstract void hideData(byte[] data, BufferedImage cover, File outputFile);

    abstract byte[] extractData(BufferedImage image);
}
