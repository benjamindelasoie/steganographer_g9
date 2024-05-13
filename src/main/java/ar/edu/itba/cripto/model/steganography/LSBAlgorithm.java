package ar.edu.itba.cripto.model.steganography;

import ar.edu.itba.cripto.model.BMPV3Image;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

public class LSBAlgorithm extends SteganographyAlgorithm {
    private final int significantBits;

    public LSBAlgorithm() {
        this.significantBits = 1;
    }

    public LSBAlgorithm(final int nBits) {
        this.significantBits = nBits;
    }

    @Override
    public void hideData(final byte[] data, BufferedImage cover, File outputFile) {
        if (!canHideData(data, cover)) {
            throw new RuntimeException("Image doesn't have enough space for the data");
        }

    }

    @Override
    public byte[] extractData(final BufferedImage image) {
        return new byte[0];
    }

    private boolean canHideData(final byte[] data, final BufferedImage cover) {
        return data.length <= cover.getHeight() * cover.getWidth() * 3;
    }
}
