package ar.edu.itba.cripto.model.steganography;

import ar.edu.itba.cripto.model.BMPV3Image;
import ar.edu.itba.cripto.model.FileHandle;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public abstract class SteganographyAlgorithm {


    public static SteganographyAlgorithm getInstance(String name) {
        switch (name) {
            case "LSB" -> {
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

    public abstract int hideData(byte[] data, File cover, File outputFile) throws IOException;

    public abstract FileHandle extractData(File image) throws IOException;

    public abstract byte[] extractRawData(File coverFile, int length) throws IOException;

    abstract boolean canHideData(final byte[] data, BMPV3Image coverImage);

    protected static String byteArrayToString(byte[] bytes) {
        int nullIndex = 0;
        while (nullIndex < bytes.length && bytes[nullIndex] != '\0') {
            nullIndex++;
        }
        return new String(Arrays.copyOfRange(bytes, 0, nullIndex), StandardCharsets.UTF_8);
    }

    protected static void printLSB(byte[] arr) {
        StringBuffer sb = new StringBuffer("lsbs: ");
        for (int i = 0; i < arr.length; i++) {
            if (i != 0 && i % 8 == 0) {
                sb.append(" ");
            }
            sb.append(((arr[i] & 1) != 0) ? "1" : "0");
        }

        System.out.println(sb);
    }
}
