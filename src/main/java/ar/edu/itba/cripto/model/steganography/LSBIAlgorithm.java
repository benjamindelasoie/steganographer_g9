package ar.edu.itba.cripto.model.steganography;

import ar.edu.itba.cripto.model.BMPV3Image;
import ar.edu.itba.cripto.model.FileHandle;

import java.io.File;
import java.io.IOException;

// TODO: Implement
public class LSBIAlgorithm extends SteganographyAlgorithm {

    @Override
    public int hideData(final byte[] data, final File cover, final File outputFile) throws IOException {
        return 0;
    }

    @Override
    public FileHandle extractData(final File image) throws IOException {
        return null;
    }

    @Override
    public byte[] extractRawData(final File coverFile, final int length) throws IOException {
        return new byte[0];
    }

    @Override
    boolean canHideData(final byte[] data, final BMPV3Image coverImage) {
        return true;
    }
}
