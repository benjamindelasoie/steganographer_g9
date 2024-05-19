package ar.edu.itba.cripto.model;

import ar.edu.itba.cripto.model.steganography.SteganographyAlgorithm;
import org.apache.commons.io.EndianUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Arrays;

public class Steganographer {
    protected final SteganographyAlgorithm stegAlgorithm;

    public Steganographer(final SteganographyAlgorithm stegAlgorithm) {
        this.stegAlgorithm = stegAlgorithm;
    }


    public int embedRaw(File inputFile, File cover, File outputFile) throws Exception {
        byte[] inputData = Files.readAllBytes(inputFile.toPath());
        return this.stegAlgorithm.hideData(inputData, cover, outputFile);
    }

    public int embed(File inputFile, File cover, File outputFile) throws Exception {
        System.out.println("Steganographer.embed " + inputFile);

        byte[] data = Files.readAllBytes(inputFile.toPath());
        int messageLength = data.length;
        String extension = FilenameUtils.getExtension(String.valueOf(inputFile));

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        EndianUtils.writeSwappedInteger(baos, messageLength);
        baos.write(data);
        baos.write(".".getBytes(StandardCharsets.UTF_8));
        baos.write(extension.getBytes(StandardCharsets.UTF_8));
        baos.write('\0');

        return this.stegAlgorithm.hideData(baos.toByteArray(), cover, outputFile);
    }

    // TODO: Implement.
    public void extract(File file) throws Exception {
        byte[] rawData = this.stegAlgorithm.extractRawData(file);
        int messageLength = readLength(rawData);
        byte[] fileData = readFileData(rawData, 4, 4 + messageLength);
        String extension = readExtension(rawData, 4 + messageLength);
        System.out.printf("Extracted %d bytes with extension %s", messageLength, extension);

        FileUtils.writeByteArrayToFile(new File("../output" + extension), fileData);
    }

    public void extract(byte[] data) throws Exception {
        byte[] rawData = this.stegAlgorithm.extractRawData(data, 54, data.length);
        int messageLength = readLength(rawData);
        byte[] fileData = readFileData(rawData, 4, 4 + messageLength);
        String extension = readExtension(rawData, 4 + messageLength);
        System.out.printf("Extracted %d bytes with extension %s", messageLength, extension);

        FileUtils.writeByteArrayToFile(new File("../output" + extension), fileData);
    }

    public void extract(byte[] data, int from, int to) throws IOException {
        byte[] rawData = this.stegAlgorithm.extractRawData(data, from, to);
        int messageLength = readLength(rawData);
        System.out.println("messageLength = " + messageLength);
        byte[] fileData = readFileData(rawData, 4, 4 + messageLength);
        System.out.println("fileData = " + Arrays.toString(fileData));
        String extension = readExtension(rawData, 4 + messageLength);
        System.out.println("extension = " + extension);

        FileUtils.writeByteArrayToFile(new File("../output" + extension), fileData);
    }

    String readExtension(final byte[] rawData, final int offset) {
        int nullIndex = offset;
        while (nullIndex < rawData.length && rawData[nullIndex] != '\0') {
            nullIndex++;
        }

        return new String(Arrays.copyOfRange(rawData, offset, nullIndex), StandardCharsets.UTF_8);
    }

    byte[] readFileData(final byte[] rawData, final int from, final int to) {
        return Arrays.copyOfRange(rawData, from, to);
    }

    public void extractRawData(File file, int length) throws IOException {
        byte[] rawData = stegAlgorithm.extractRawData(file);
        System.out.println("rawData = " + Arrays.toString(rawData));
        FileUtils.writeByteArrayToFile(new File("../salidita.txt"), rawData);
    }

    int readLength(final byte[] imageData) {
        return EndianUtils.readSwappedInteger(imageData, 0);
    }
}
