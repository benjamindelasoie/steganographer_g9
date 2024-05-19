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
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        byte[] data = Files.readAllBytes(inputFile.toPath());
        int messageLength = data.length;
        String extension = FilenameUtils.getExtension(String.valueOf(inputFile));
        System.out.printf("Embedding length = %d ext = %s\n", messageLength, extension);

        EndianUtils.writeSwappedInteger(baos, messageLength);
        baos.write(data);
        baos.write(".".getBytes(StandardCharsets.UTF_8));
        baos.write(extension.getBytes(StandardCharsets.UTF_8));
        baos.write('\0');
        System.out.println("baos " + baos);

        return this.stegAlgorithm.hideData(baos.toByteArray(), cover, outputFile);
    }

    // TODO: Implement.
    public void extract(File file) throws Exception {
        FileHandle fileHandle = this.stegAlgorithm.extractData(file);
        FileUtils.writeByteArrayToFile(new File("../output" + fileHandle.extension()), fileHandle.data());
    }

    public void extractRawData(File file, int length) throws IOException {
        byte[] rawData = stegAlgorithm.extractRawData(file, length);
        System.out.println("rawData = " + Arrays.toString(rawData));
        FileUtils.writeByteArrayToFile(new File("../salidita.txt"), rawData);
    }
    
}
