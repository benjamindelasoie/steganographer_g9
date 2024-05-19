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

public class LSBAlgorithm extends SteganographyAlgorithm {

    public int hideData(final byte[] msg, File cover, File outputFile) throws IOException {
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
            for (int j = 0; j < 8; j++) {

                byte imageByte = imageData[offset + (i * 8) + j]; // el byte 0 del mensaje se codifica en los bytes 54 + 0 + 0,...,7 de la imagen.
                //System.out.printf("\toriginal imageByte[%d] = %d = %s\n", offset + i + j, imageByte, Integer.toBinaryString(imageByte));

                boolean bitToHide = (msg[i] & (1 << (7 - j))) != 0;
                //System.out.printf("\t%d-th bit to hide = %s", i * 8 + j, (bitToHide ? "1\n" : "0\n"));

                if (bitToHide) {
                    imageByte |= (byte) (1);
                } else {
                    imageByte &= (byte) ~(1);
                }

                //System.out.printf("\tresulting imageByte[%d] = %d = %s\n\n", offset + i + j, imageByte, Integer.toBinaryString(imageByte));
                outputData[offset + i * 8 + j] = imageByte;
            }
        }

        System.out.println("outputData: " + Arrays.toString(Arrays.copyOfRange(outputData, 54, 54 + msg.length * 8)));

        printLSB(Arrays.copyOfRange(outputData, 54, 54 + msg.length * 8));

        System.out.println("Son iguales?: " + (Arrays.equals(bmp.getImageData(), outputData) ? "si" : "no"));

        System.out.println("Escribiendo output en: " + outputFile);
        FileUtils.writeByteArrayToFile(outputFile, outputData);
        return 0;
    }

    public FileHandle extractData(final File coverFile) throws IOException {
        System.out.println("Extract data " + coverFile);
        BMPV3Image img = new BMPV3Image();
        img.loadFromFile(coverFile.getPath());


        byte[] imageData = img.getImageData();
        int offset = img.getDataOffset();
        int messageLength = extractLength(imageData, offset);
        System.out.println("messageLength = " + messageLength);
        byte[] extractedData = new byte[messageLength];
        printLSB(Arrays.copyOfRange(imageData, offset, offset + 4 + 2 + 5));

        for (int i = 0; i < messageLength; i++) {
            byte extractByte = 0;

            for (int j = 0; j < 8; j++) {
                int imgByte = imageData[offset + (4 + i) * 8 + j];
                //System.out.printf("current imgByte[%d] = %s\n", offset + i * 8 + j, Integer.toBinaryString(imgByte));

                boolean lsb = (imgByte & 1) != 0;
                //System.out.println("lsb = " + (lsb ? "1" : "0"));

                if (lsb) {
                    extractByte |= (byte) (1 << (7 - j));
                } else {
                    extractByte &= (byte) ~(1 << (7 - j));
                }
            }

            //System.out.println("extracted byte: " + extractByte);
            //System.out.println(Integer.toBinaryString(extractByte));
            extractedData[i] = (byte) extractByte;
        }
        System.out.println("extractedData = " + Arrays.toString(extractedData));

        String extension = extractExtension(54 + (4 + messageLength) * 8, imageData);
        System.out.println("Extension : " + extension);
        return new FileHandle(messageLength, extractedData, extension);
    }

    @Override
    public byte[] extractRawData(File coverFile) throws IOException {
        System.out.println("Extract raw data " + coverFile);
        BMPV3Image img = new BMPV3Image();
        img.loadFromFile(coverFile.getPath());


        byte[] imageData = img.getImageData();
        int offset = img.getDataOffset();
        byte[] extractedData = new byte[img.getHeight() * img.getWidth() * 3 / 8];

        for (int i = 0; i < extractedData.length; i++) {
            byte extractByte = 0;
            for (int j = 0; j < 8; j++) {
                byte imgByte = imageData[offset + i * 8 + j];
                boolean lsb = (imgByte & 1) != 0;
                if (lsb) {
                    extractByte |= (byte) (1 << (7 - j));
                } else {
                    extractByte &= (byte) ~(1 << (7 - j));
                }
            }
            extractedData[i] = extractByte;
        }

        return extractedData;
    }

    public byte[] extractRawData(byte[] embeddedData, int from, int to) {
        if (to <= from) {
            throw new IllegalArgumentException("to <= from");
        }

        byte[] extractedData = new byte[(to - from) / 8];
        for (int i = 0; i < extractedData.length; i++) {
            byte extractByte = 0;
            for (int j = 0; j < 8; j++) {
                byte imgByte = embeddedData[from + i * 8 + j];
                boolean lsb = (imgByte & 1) != 0;
                if (lsb) {
                    extractByte |= (byte) (1 << (7 - j));
                } else {
                    extractByte &= (byte) ~(1 << (7 - j));
                }
            }
            extractedData[i] = extractByte;
        }

        return extractedData;
    }

    private int extractLength(byte[] imageData, int offset) {
        byte[] lengthData = new byte[4];
        // La consigna dice que los primeros 4 bytes son la longitud del mensaje en little Endian.
        for (int i = 0; i < 4; i++) {
            byte extractByte = 0;
            for (int j = 0; j < 8; j++) {
                byte imgByte = imageData[offset + i * 8 + j];

                boolean lsb = (imgByte & 1) != 0;
                if (lsb) {
                    extractByte |= (byte) (1 << (7 - j));
                } else {
                    extractByte &= (byte) ~(1 << (7 - j));
                }
            }

            lengthData[i] = extractByte;
        }

        return EndianUtils.readSwappedInteger(lengthData, 0);
    }

    protected boolean canHideData(final byte[] data, BMPV3Image coverImage) {
        return data.length * 8 <= coverImage.getHeight() * coverImage.getWidth() * 3;
    }

    private String extractExtension(int offset, byte[] imgData) {
        System.out.printf("extract extension: offset %d\n", offset);
        System.out.println(Arrays.toString(Arrays.copyOfRange(imgData, offset, offset + 5 * 8)));
        byte[] extensionData = new byte[5];
        printLSB(Arrays.copyOfRange(imgData, offset, offset + 5 * 8));
        boolean foundNull = false;

        for (int i = 0; i < 5 && !foundNull; i++) {
            byte currentByte = 0;
            for (int j = 0; j < 8; j++) {
                byte imgByte = imgData[offset + i * 8 + j];

                boolean lsb = (imgByte & 1) != 0;
                if (lsb) {
                    currentByte |= (byte) (1 << (7 - j));
                } else {
                    currentByte &= (byte) ~(1 << (7 - j));
                }
            }

            if (currentByte == '\0') {
                foundNull = true;
            } else {
                extensionData[i] = currentByte;
            }
        }

        System.out.println("extension data: " + Arrays.toString(extensionData));
        return byteArrayToString(extensionData);
    }

    private String byteToHexa(byte b) {
        return String.format("%02X ", b);
    }

}
