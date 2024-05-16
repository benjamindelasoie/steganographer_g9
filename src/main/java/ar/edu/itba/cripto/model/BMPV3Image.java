package ar.edu.itba.cripto.model;

import org.apache.commons.io.EndianUtils;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Iterator;

public class BMPV3Image implements Iterable<Byte> {
    private BMPV3HeaderInfo header;
    private byte[] imageData;

    public void loadFromFile(String filePath) throws IOException {
        try (FileInputStream fis = new FileInputStream(filePath)) {
            byte[] data = fis.readAllBytes();
            BMPV3HeaderInfo header = parseHeader(data);

            if (header.isCompressed) {
                throw new IOException("BMP shouldn't be compressed");
            }

            this.header = header;
            this.imageData = data;

        }
    }

    public int getDataOffset() {
        return header.dataOffset;
    }

    public byte[] getImageData() {
        return imageData;
    }

    @Override
    public Iterator<Byte> iterator() {
        return new Iterator<>() {
            final int i = getDataOffset();

            @Override
            public boolean hasNext() {
                return i < imageData.length;
            }

            @Override
            public Byte next() {
                return imageData[i];
            }
        };
    }

    @Override
    public String toString() {
        return "BMPV3Image{" +
                "header=" + header +
                ", imageData=" + imageData.length + " bytes" +
                '}';
    }

    private record BMPV3HeaderInfo(int size, int dataOffset,
                                   int width, int height,
                                   int bitsPerPixel, boolean isCompressed){}

    private BMPV3HeaderInfo parseHeader (byte[] dataBytes) {
        int size = EndianUtils.readSwappedInteger(dataBytes, 2);
        int dataOffset = EndianUtils.readSwappedInteger(dataBytes, 10);
        int width = EndianUtils.readSwappedInteger(dataBytes, 18);
        int height = EndianUtils.readSwappedInteger(dataBytes, 22);
        short bitsPerPixel = EndianUtils.readSwappedShort(dataBytes, 28);
        boolean isCompressed = EndianUtils.readSwappedInteger(dataBytes, 30) != 0;

        return new BMPV3HeaderInfo(size, dataOffset, width, height, bitsPerPixel, isCompressed);
    }
}
