package ar.edu.itba.cripto.model.steganography;

import ar.edu.itba.cripto.model.BMPV3Image;

import java.awt.image.BufferedImage;
import java.io.File;

// TODO: Implement
public class LSBIAlgorithm extends SteganographyAlgorithm {

    @Override
    public void hideData(final byte[] data, final BufferedImage cover, final File outputFile) {

    }

    @Override
    byte[] extractData(final BufferedImage image) {
        return new byte[0];
    }
}
