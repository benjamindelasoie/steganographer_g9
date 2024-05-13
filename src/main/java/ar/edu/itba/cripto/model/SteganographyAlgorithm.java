package ar.edu.itba.cripto.model;

import java.awt.*;
import java.io.File;

public interface SteganographyAlgorithm {
    void hideData(byte[] data, BMPV3Image cover);
    byte[] extractData(BMPV3Image image);
}
