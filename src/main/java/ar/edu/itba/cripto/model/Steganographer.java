package ar.edu.itba.cripto.model;

import ar.edu.itba.cripto.steganography.SteganographyAlgorithm;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Arrays;

public class Steganographer {
    public static final int LENGTH_SIZE = 4;
    protected final SteganographyAlgorithm stegAlgorithm;

    public Steganographer(final SteganographyAlgorithm stegAlgorithm) {
        this.stegAlgorithm = stegAlgorithm;
    }

    static byte[] buildByteArray(final File inputFile) throws IOException {
        byte[] data = Files.readAllBytes(inputFile.toPath());
        int messageLength = data.length;
        String extension = FilenameUtils.getExtension(String.valueOf(inputFile));

        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        ByteBuffer buffer = ByteBuffer.allocate(Integer.BYTES);
        buffer.putInt(messageLength);
        buffer.rewind();
        byte[] lengthBytes = buffer.array();
        System.out.println("length bytes = " + Arrays.toString(lengthBytes) + " = " + messageLength);
        baos.write(lengthBytes);
        baos.write(data);
        baos.write(".".getBytes(StandardCharsets.UTF_8));
        baos.write(extension.getBytes(StandardCharsets.UTF_8));
        baos.write('\0');

        return baos.toByteArray();
    }

    public static Steganographer getSteganographer(String stegAlgo, String cipherName, String cipherModeName, String password) throws Exception {
        SteganographyAlgorithm algo = SteganographyAlgorithm.getInstance(stegAlgo);
        if (password == null) {
            return new Steganographer(algo);
        } else {
            return new EncryptingSteganographer(algo, cipherName, cipherModeName, password);
        }
    }

    public static Steganographer getSteganographer(final String steg) {
        return new Steganographer(SteganographyAlgorithm.getInstance(steg));
    }

    public void embed(File inputFile, File cover, File outputFile) throws Exception {
        // Construyo el mensaje tamaño + data + extension
        byte[] fileInfo = buildByteArray(inputFile);

        // Aplico el esteganografiado a través del algoritmo
        this.stegAlgorithm.hideData(fileInfo, cover, outputFile);

    }

    public void extract(File file, String outputFile) throws Exception {
        // Extraigo los bytes usando el algoritmo
        byte[] rawData = this.stegAlgorithm.extractData(file);

        // Leo los componentes del mensaje
        int messageLength = readLength(rawData);
        System.out.println("messageLength = " + messageLength);

        byte[] fileData = readFileData(rawData, LENGTH_SIZE, LENGTH_SIZE + messageLength);

        String extension = readExtension(rawData, LENGTH_SIZE + messageLength);

        // Genero el archivo de salida en base a lo extraído
        FileUtils.writeByteArrayToFile(new File(outputFile + extension), fileData);
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

    int readLength(final byte[] imageData) {
        byte[] lengthBytes = Arrays.copyOfRange(imageData, 0, LENGTH_SIZE);

        ByteBuffer buffer = ByteBuffer.allocate(Integer.BYTES);
        buffer.put(lengthBytes).rewind();
        return buffer.getInt();
    }
}
