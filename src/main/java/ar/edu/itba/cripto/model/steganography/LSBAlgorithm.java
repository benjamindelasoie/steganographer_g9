package ar.edu.itba.cripto.model.steganography;

import ar.edu.itba.cripto.model.BMPV3Image;
import org.apache.commons.io.FileUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.DataBufferByte;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class LSBAlgorithm extends SteganographyAlgorithm {

    public int hideData(final byte[] msg, File cover, File outputFile) throws IOException {
        BMPV3Image bmp = new BMPV3Image();
        bmp.loadFromFile(cover.getPath());

        System.out.println("Received request to hide msg: " + new String(msg, StandardCharsets.UTF_8));
        System.out.println("Byte array: " + Arrays.toString(msg));
        System.out.println("lenghts in bytes: " + msg.length);
        System.out.println("length in bits: " + msg.length * 8);


        byte[] outputData = bmp.getImageData().clone();
        int msgIndex = 0;
        boolean termino = false;
        for (int i = bmp.getDataOffset(); i < outputData.length && !termino; i++) {
            byte outputByte = outputData[i];
            for (int j = 0; j < 8; j++) {
                if (msgIndex < msg.length * 8) {
                    boolean bitToHide = (msg[msgIndex / 8] & (1 << (msgIndex % 8))) != 0;
                    System.out.println((msgIndex) + "-th bit to hide=" + bitToHide);
                    if (bitToHide) {
                        outputByte |= (byte) (1 << j); // Set bit to 1
                    } else {
                        outputByte &= (byte) ~(1 << j); // Clear bit (set to 0)
                    }
                    msgIndex++;
                } else {
                    System.out.println("Termino");
                    termino = true;
                    break;
                }
            }
            outputData[i] = outputByte;
        }

        System.out.println("Escribiendo output en: " + outputFile);
        System.out.println("Son iguales?: " + (Arrays.equals(bmp.getImageData(), outputData) ? "si" : "no"));

        FileUtils.writeByteArrayToFile(outputFile, outputData);

        return 0;
    }

    public byte[] extractData(final File image) throws IOException {
        BufferedImage img = ImageIO.read(image);
        byte[] imageData = ((DataBufferByte) img.getData().getDataBuffer()).getData();

        byte[] extractedData = new byte[(imageData.length + 7) / 8]; // Assuming 1 bit of data per byte

        int dataIndex = 0;
        for (int i = 0; i < imageData.length; i++) {
            for (int j = 0; j < 8; j++) {
                // Retrieve the LSB of the byte
                boolean lsb = (imageData[i] & (1 << j)) != 0;
                // Set the corresponding bit in the extracted data
                if (dataIndex < extractedData.length * 8) {
                    if (lsb) {
                        extractedData[dataIndex / 8] |= (1 << (dataIndex % 8));
                    } else {
                        extractedData[dataIndex / 8] &= ~(1 << (dataIndex % 8));
                    }
                    dataIndex++;
                } else {
                    break;
                }
            }
        }
        return extractedData;
    }

    private boolean canHideData(final byte[] data, final File cover) {
        try {
            BufferedImage img = ImageIO.read(cover.getAbsoluteFile());
            return data.length * 8 <= img.getHeight() * img.getWidth() * 3;
        } catch (IOException ioe) {
            throw new RuntimeException("Can't open cover file");
        }
    }

    public static BufferedImage copy(BufferedImage bi) {
        ColorModel cm = bi.getColorModel();
        boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
        WritableRaster raster = bi.copyData(bi.getRaster().createCompatibleWritableRaster());
        return new BufferedImage(cm, raster, isAlphaPremultiplied, null);
    }
}
