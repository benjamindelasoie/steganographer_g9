package ar.edu.itba.cripto.steganography;

import ar.edu.itba.cripto.model.BMPV3Image;

import java.io.File;
import java.io.IOException;

// TODO: Implement
public class LSBIAlgorithm extends SteganographyAlgorithm {
    @Override
    public void hideData(final byte[] data, final File cover, final File outputFile) throws IOException {
    }

    @Override
    public byte[] extractData(final File image) throws IOException {
        return new byte[0];
    }

    @Override
    boolean canHideData(final byte[] data, final BMPV3Image coverImage) {
        return false;
    }
}
