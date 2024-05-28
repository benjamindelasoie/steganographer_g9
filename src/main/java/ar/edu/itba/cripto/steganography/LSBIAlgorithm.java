package ar.edu.itba.cripto.steganography;

import ar.edu.itba.cripto.exceptions.NotEnoughSpaceInImageException;
import ar.edu.itba.cripto.model.BMPV3Image;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

// TODO: Implement
public class LSBIAlgorithm extends SteganographyAlgorithm {
    private static final int PATTERN_SIZE = 4;
    private final int msgToCoverRatio = 8;

    @Override
    public String getName() {
        return "LSBI";
    }

    @Override
    public void hideData(final byte[] msg, final File coverFile, final File outputFile) throws IOException {
        BMPV3Image cover = new BMPV3Image();
        cover.loadFromFile(coverFile.getPath());

        if (!canHideData(msg, cover)) {
            throw new NotEnoughSpaceInImageException(cover.getSize(), msg.length * msgToCoverRatio + 4);
        }

        byte[] imageData = cover.getImageData();
        int offset = cover.getDataOffset();
        byte[] result = imageData.clone();
        int imageIndex = offset;
        byte pattern;
        boolean imageLSB;
        boolean bitToHide;
        PatternOccurrence patternOccurrence;
        Map<Byte, PatternOccurrence> patternMap = new TreeMap<>();
        byte imageByte;


        // Realizamos la primer pasada llevando cuenta de que patrones cambiamos y cuales no.
        for (int i = 0; i < msg.length; i++) {
            for (int j = 0; j < msgToCoverRatio; j++) {
                imageIndex = offset + PATTERN_SIZE + (i * msgToCoverRatio) + j;

                // Agarro el byte de la imagen indicado
                imageByte = imageData[imageIndex];

                pattern = (byte) ((imageByte >> 1) & 0b00000011);
                imageLSB = (imageByte & 1) != 0;
                bitToHide = (msg[i] >> (7 - j) & 1) != 0;

                patternMap.putIfAbsent(pattern, new PatternOccurrence(pattern));

                patternOccurrence = patternMap.get(pattern);
                if (imageLSB == bitToHide) {
                    patternOccurrence.addSame();
                } else {
                    patternOccurrence.addChanged();
                }

                if (bitToHide) {
                    imageByte |= 1;
                } else {
                    imageByte &= ~(1);
                }

                // Seteo el output
                result[imageIndex] = imageByte;
            }
        }

        // Despues de los bytes del mensaje, el resto el bmp quedó igual.
        while (imageIndex < imageData.length) {
            pattern = (byte) ((imageData[imageIndex] >> 1) & 0b00000011);
            patternMap.putIfAbsent(pattern, new PatternOccurrence(pattern));
            patternMap.get(pattern).addSame();
            imageIndex++;
        }


        // Seteo el lsb de los primeros 4 bytes para el patron de inversion.
        imageIndex = offset;
        for (byte b = 0; b < 4; b++) {
            imageByte = imageData[imageIndex];

            if (patternMap.getOrDefault(b, new PatternOccurrence(b)).shouldInvert()) {
                imageByte |= 1;
            } else {
                imageByte &= ~(1);
            }

            System.out.println("pattern " + b + " " + patternMap.getOrDefault(b, new PatternOccurrence(b)));

            result[imageIndex++] = imageByte;
        }

        // Realizo la inversión donde corresponda, si es que hay que hacer una al menos.
        if (patternMap.values().stream().anyMatch(PatternOccurrence::shouldInvert)) {
            for (int i = offset; i < result.length; i++) {
                pattern = (byte) ((result[i] >> 1) & 0b00000011);
                if (patternMap.getOrDefault(pattern, new PatternOccurrence(pattern)).shouldInvert()) {
                    result[i] = (byte) (result[i] ^ 1);
                }
            }
        }

        System.out.println("Writing result on... " + outputFile);
        FileUtils.writeByteArrayToFile(outputFile, result);
    }

    @Override
    public byte[] extractData(final File image) throws IOException {
        BMPV3Image img = new BMPV3Image();
        img.loadFromFile(image.getPath());


        byte[] imageData = img.getImageData();
        int offset = img.getDataOffset();
        byte imageByte;

        Map<Byte, Boolean> inversionMap = new HashMap<>();
        boolean lsb;

        // Leo los patrones de inversion
        for (byte i = 0; i < 4; i++) {
            imageByte = imageData[offset + i];
            lsb = (imageByte & 1) != 0;
            System.out.printf("Byte %d inverts : %b\n", i, lsb);
            inversionMap.put(i, lsb);
        }

        int imgIndex;
        byte pattern;
        byte[] extractedData = new byte[(img.getSize() - 4) / msgToCoverRatio];
        for (int i = 0; i < extractedData.length; i++) {
            byte extractByte = 0;
            for (int j = 0; j < msgToCoverRatio; j++) {
                // Agarro el byte de la imagen
                imgIndex = offset + 4 + i * msgToCoverRatio + j;
                imageByte = imageData[imgIndex];

                // Me fijo el patron y el lsb.
                pattern = (byte) ((imageByte >> 1) & 0b00000011);
                lsb = (imageByte & 1) != 0;
                System.out.println("pattern = " + pattern + " invert = " + inversionMap.get(pattern));
                System.out.println("lsb = " + lsb);
                // Si el patron se invirtio, invierto el lsb.
                if (inversionMap.get(pattern)) {
                    lsb = !lsb;
                }
                System.out.println("lsb = " + lsb);
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

    public boolean canHideData(final byte[] data, final BMPV3Image bmp) {
        return data.length * msgToCoverRatio + 4 <= bmp.getSize();
    }

    private static class PatternOccurrence {
        private final Byte pattern;
        private long same = 0;
        private long changed = 0;

        public PatternOccurrence(final Byte pattern) {
            this.pattern = pattern;
        }

        private void addChanged() {
            this.changed++;
        }

        private void addSame() {
            this.same++;
        }

        public long getSame() {
            return same;
        }

        public long getChanged() {
            return changed;
        }

        public boolean shouldInvert() {
            return changed > same;
        }

        @Override
        public String toString() {
            return "PatternOccurrence{" +
                "pattern=" + pattern +
                ", same=" + same +
                ", changed=" + changed +
                '}';
        }
    }
}
