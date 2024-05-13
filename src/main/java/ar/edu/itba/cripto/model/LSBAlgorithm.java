package ar.edu.itba.cripto.model;

import java.io.File;

public class LSBAlgorithm implements SteganographyAlgorithm {

    @Override
    public void hideData(final byte[] data, final BMPV3Image cover) {

    }

    @Override
    public byte[] extractData(final BMPV3Image image) {
        return new byte[0];
    }
}
