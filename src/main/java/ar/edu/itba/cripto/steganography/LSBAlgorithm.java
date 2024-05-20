package ar.edu.itba.cripto.steganography;

import ar.edu.itba.cripto.exceptions.NotEnoughSpaceInImageException;
import ar.edu.itba.cripto.model.BMPV3Image;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

public class LSBAlgorithm extends SteganographyAlgorithm {
    private int significantBits = 1;
    private byte mask = 0b00000001;
    private int msgToCoverRatio = 8 / significantBits;

    protected LSBAlgorithm(final int significantBits, final byte mask) {
        this.significantBits = significantBits;
        this.mask = mask;
        this.msgToCoverRatio = 8 / significantBits;
    }

    public LSBAlgorithm() {
    }

    public void hideData(final byte[] msg, File cover, File outputFile) throws IOException {
        BMPV3Image bmp = new BMPV3Image();
        bmp.loadFromFile(cover.getPath());


        if (!this.canHideData(msg, bmp)) {
            throw new NotEnoughSpaceInImageException(bmp.getHeight() * bmp.getWidth() * 3);
        }

        byte[] imageData = bmp.getImageData();
        byte[] outputData = imageData.clone();
        int offset = bmp.getDataOffset();

        for (int i = 0; i < msg.length; i++) {
            for (int j = 0; j < msgToCoverRatio; j++) {

                // Agarro el byte de la imagen indicado
                byte imageByte = imageData[offset + (i * msgToCoverRatio) + j];

                // Limpio los ultimos SIGNIFICANT_BITS bits del byte de la imagen.
                imageByte &= (byte) ~(mask);

                // Agarro los bits que tengo que esconder.
                byte bitsToHide = (byte) ((msg[i] >> (8 - (j + 1) * significantBits)) & (mask));

                // El esteganografiado propiamente dicho
                imageByte |= bitsToHide;

                // Seteo el output
                outputData[offset + i * msgToCoverRatio + j] = imageByte;
            }
        }

        FileUtils.writeByteArrayToFile(outputFile, outputData);
    }

    @Override
    public byte[] extractData(File coverFile) throws IOException {
        BMPV3Image img = new BMPV3Image();
        img.loadFromFile(coverFile.getPath());


        byte[] imageData = img.getImageData();
        int offset = img.getDataOffset();
        byte[] extractedData = new byte[img.getSize() / msgToCoverRatio];

        for (int i = 0; i < extractedData.length; i++) {
            byte extractByte = 0;
            for (int j = 0; j < msgToCoverRatio; j++) {
                byte imgByte = imageData[offset + i * msgToCoverRatio + j];

                byte lsbn = (byte) (imgByte & (mask));

                extractByte |= (byte) (lsbn << (8 - (j + 1) * significantBits));
            }
            extractedData[i] = extractByte;
        }

        return extractedData;
    }

    protected boolean canHideData(final byte[] data, BMPV3Image coverImage) {
        return data.length * msgToCoverRatio <= coverImage.getSize();
    }
}
