package ar.edu.itba.cripto.model.steganography;

import ar.edu.itba.cripto.exceptions.NotEnoughSpaceInImageException;
import ar.edu.itba.cripto.model.BMPV3Image;
import ar.edu.itba.cripto.model.FileHandle;
import org.apache.commons.io.EndianUtils;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

// TODO: Implement
public class LSB4Algorithm extends SteganographyAlgorithm {
    private static final int BITS_HIDDEN_PER_BYTE = 4;

    @Override
    public int hideData(final byte[] msg, final File cover, final File outputFile) throws IOException {
        BMPV3Image bmp = new BMPV3Image();
        bmp.loadFromFile(cover.getPath());

        if (!this.canHideData(msg, bmp)) {
            throw new NotEnoughSpaceInImageException(bmp.getHeight() * bmp.getWidth() * 3);
        }

        System.out.println("Received request to hide msg: " + new String(msg, StandardCharsets.UTF_8));
        FileUtils.writeByteArrayToFile(new File("../pruebita.txt"), msg);
        System.out.println("Byte array: " + Arrays.toString(msg));
        System.out.println("lenghts in bytes: " + msg.length);
        System.out.println("length in bits: " + msg.length * 8);

        byte[] imageData = bmp.getImageData();
        byte[] outputData = imageData.clone();
        int offset = bmp.getDataOffset();
        for (int i = 0; i < msg.length; i++) {
            System.out.println("byte to hide = " + Integer.toBinaryString(msg[i]));
            for (int j = 0; j < 2; j++) {

                // Limpio los ultimos 4 bits del byte de la imagen.
                byte imageByte = imageData[offset + (i * 2) + j];
                System.out.println("Original imageByte = " + Integer.toBinaryString(imageByte));
                imageByte &= (byte) 0xF0;
                System.out.println("Cleaned imageByte = " + Integer.toBinaryString(imageByte));

                byte mask;
                if (j % 2 == 0) {
                    mask = (byte) (msg[i] >> 4);
                } else {
                    mask = (byte) (msg[i] & 0x0F);
                }
                System.out.println("mask = " + Integer.toBinaryString(mask));

                imageByte |= mask;
                System.out.println("resulting imageByte = " + Integer.toBinaryString(imageByte));
                outputData[offset + i * 2 + j] = imageByte;
            }
        }

        System.out.println("outputData: " + Arrays.toString(Arrays.copyOfRange(outputData, 54, 54 + msg.length * 2)));

        printLSB4(Arrays.copyOfRange(outputData, 54, 54 + msg.length * 2));

        System.out.println("Son iguales?: " + (Arrays.equals(bmp.getImageData(), outputData) ? "si" : "no"));

        System.out.println("Escribiendo output en: " + outputFile);
        FileUtils.writeByteArrayToFile(outputFile, outputData);
        return 0;

    }

    @Override
    public FileHandle extractData(final File coverFile) throws IOException {
        System.out.println("Extract data " + coverFile);
        BMPV3Image img = new BMPV3Image();
        img.loadFromFile(coverFile.getPath());


        byte[] imageData = img.getImageData();
        int offset = img.getDataOffset();
        int messageLength = extractLength(imageData, offset);
        System.out.println("messageLength = " + messageLength);
        byte[] extractedData = new byte[messageLength];

        for (int i = 0; i < messageLength; i++) {
            byte extractByte = 0;
            for (int j = 0; j < 2; j++) {
                byte imgByte = imageData[offset + (4 + i) * 2 + j];

                byte lsb4 = (byte) (imgByte & 0x0F);

                if (j % 2 == 0) {
                    extractByte |= (byte) (lsb4 << 4);
                } else {
                    extractByte |= (lsb4);
                }
            }

            extractedData[i] = extractByte;
        }

        System.out.println("extractedData = " + Arrays.toString(extractedData));

        String extension = extractExtension(54 + (4 + messageLength) * 2, imageData);
        System.out.println("Extension : " + extension);
        return new FileHandle(messageLength, extractedData, extension);

    }

    private String extractExtension(final int offset, final byte[] imgData) {
        System.out.printf("extract extension: offset %d\n", offset);
        byte[] extensionData = new byte[16];
        boolean foundNull = false;

        for (int i = 0; i < extensionData.length && !foundNull; i++) {
            byte extractByte = 0;
            for (int j = 0; j < 2; j++) {
                byte imgByte = imgData[offset + i * 2 + j];

                byte lsb4 = (byte) (imgByte & 0x0F);

                if (j % 2 == 0) {
                    extractByte |= (byte) (lsb4 << 4);
                } else {
                    extractByte |= (lsb4);
                }
            }

            if (extractByte == '\0') {
                foundNull = true;
            } else {
                extensionData[i] = extractByte;
            }
        }

        System.out.println("extension data: " + Arrays.toString(extensionData));
        return byteArrayToString(extensionData);
    }

    private int extractLength(final byte[] imageData, final int offset) {
        byte[] lengthData = new byte[4];

        // La consigna dice que los primeros 4 bytes son la longitud del mensaje en little Endian.
        for (int i = 0; i < 4; i++) {
            byte extractByte = 0;
            for (int j = 0; j < 2; j++) {
                byte imgByte = imageData[offset + i * 2 + j];

                byte lsb4 = (byte) (imgByte & 0x0F);

                if (j % 2 == 0) {
                    extractByte |= (byte) (lsb4 << 4);
                } else {
                    extractByte |= (lsb4);
                }
            }

            lengthData[i] = extractByte;
        }

        return EndianUtils.readSwappedInteger(lengthData, 0);
    }

    @Override
    public byte[] extractRawData(final File coverFile, final int length) throws IOException {
        return new byte[0];
    }

    @Override
    boolean canHideData(final byte[] data, final BMPV3Image coverImage) {
        return data.length * (8 / BITS_HIDDEN_PER_BYTE) <= coverImage.getHeight() * coverImage.getWidth() * 3;
    }

    protected static void printLSB4(byte[] arr) {
        StringBuffer sb = new StringBuffer("lsbs: ");
        for (int i = 0; i < arr.length; i++) {
            if (i != 0 && i % 2 == 0) {
                sb.append(" ");
            }
            byte lsb4 = (byte) (arr[i] & 0x0F);
            sb.append(String.format("%4s", Integer.toBinaryString(lsb4)).replace(' ', '0'));
        }
        System.out.println(sb);
    }
}
