package ar.edu.itba.cripto.model.steganography;

import ar.edu.itba.cripto.exceptions.NotEnoughSpaceInImageException;
import ar.edu.itba.cripto.model.BMPV3Image;
import ar.edu.itba.cripto.model.FileHandle;
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


                if (j % 2 == 0) {
                    imageByte |= (byte) (msg[i] >> 4);
                } else {
                    imageByte |= (byte) (msg[i] & 0x0F);
                }


                outputData[offset + i * 2 + j] = imageByte;
            }

        }

        System.out.println("outputData: " + Arrays.toString(Arrays.copyOfRange(outputData, 54, 54 + msg.length * 8)));

        printLSB(Arrays.copyOfRange(outputData, 54, 54 + msg.length * 8));

        System.out.println("Son iguales?: " + (Arrays.equals(bmp.getImageData(), outputData) ? "si" : "no"));

        System.out.println("Escribiendo output en: " + outputFile);
        FileUtils.writeByteArrayToFile(outputFile, outputData);
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
        return data.length * (8 / BITS_HIDDEN_PER_BYTE) <= coverImage.getHeight() * coverImage.getWidth() * 3;
    }
}
