package ar.edu.itba.cripto.model.steganography;

import java.io.File;
import java.io.IOException;

// TODO: Implement
public class LSB4Algorithm extends SteganographyAlgorithm {
    @Override
    public int hideData(final byte[] data, final File cover, final File outputFile) throws IOException {
        return 0;
    }

    @Override
    public byte[] extractData(final File image) throws IOException {
        return new byte[0];
    }
}
