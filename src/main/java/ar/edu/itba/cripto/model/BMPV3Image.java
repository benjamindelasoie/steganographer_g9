package ar.edu.itba.cripto.model;

import org.apache.commons.io.EndianUtils;

import java.io.FileInputStream;
import java.io.IOException;

public class BMPV3Image {
    private BMPV3HeaderInfo header;
    private byte[] imageData;

    public void loadFromFile(final String filePath) throws IOException {
        try (FileInputStream fis = new FileInputStream(filePath)) {
            byte[] data = fis.readAllBytes();
            BMPV3HeaderInfo parseHeader = parseHeader(data);

            if (parseHeader.isCompressed) {
                throw new IOException("BMP shouldn't be compressed");
            }
            if (parseHeader.bitsPerPixel != 24) {
                throw new IOException("BMP should be 24 bits-per-pixel only");
            }

            this.header = parseHeader;
            this.imageData = data;
        }
    }

    public int getHeight() {
        return this.header.height;
    }

    public int getWidth() {
        return this.header.width;
    }

    public int getDataOffset() {
        return header.dataOffset;
    }

    public int getSize() {
        return header.width * header.height * header.bitsPerPixel / 8;
    }

    public byte[] getImageData() {
        return imageData;
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
                                   int bitsPerPixel, boolean isCompressed) {
    }

    private BMPV3HeaderInfo parseHeader(byte[] dataBytes) {
        int size = EndianUtils.readSwappedInteger(dataBytes, 2);
        int dataOffset = EndianUtils.readSwappedInteger(dataBytes, 10);
        int width = EndianUtils.readSwappedInteger(dataBytes, 18);
        int height = EndianUtils.readSwappedInteger(dataBytes, 22);
        short bitsPerPixel = EndianUtils.readSwappedShort(dataBytes, 28);
        boolean isCompressed = EndianUtils.readSwappedInteger(dataBytes, 30) != 0;

        return new BMPV3HeaderInfo(size, dataOffset, width, height, bitsPerPixel, isCompressed);
    }
}
