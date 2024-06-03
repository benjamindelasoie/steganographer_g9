package ar.edu.itba.cripto.model;

import ar.edu.itba.cripto.steganography.SteganographyAlgorithm;
import org.apache.commons.io.FileUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;

public class EncryptingSteganographer extends Steganographer {
    private final CipherHandle cipherHandle;

    public EncryptingSteganographer(final SteganographyAlgorithm stegAlgorithm, String cipher, String mode, String password) throws Exception {
        super(stegAlgorithm);
        this.cipherHandle = new CipherHandle(cipher, mode, password);
    }

    static byte[] buildCypherByteArray(byte[] cyphertext) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        int cyphertextLength = cyphertext.length;
        ByteBuffer buffer = ByteBuffer.allocate(Integer.BYTES);
        buffer.putInt(cyphertextLength);
        buffer.rewind();
        byte[] lengthBytes = buffer.array();
        baos.write(lengthBytes);
        baos.write(cyphertext);

        return baos.toByteArray();
    }

    @Override
    public void embed(final File inputFile, final File cover, final File outputFile) throws Exception {
        // Construimos el mensaje a esconder con el formato pedido
        byte[] fileInfo = buildByteArray(inputFile);

        // Encriptado
        byte[] cyphertext = this.cipherHandle.encrypt(fileInfo);

        // Construimos el array del texto cifrado.
        byte[] cypherMessage = buildCypherByteArray(cyphertext);

        this.stegAlgorithm.hideData(cypherMessage, cover, outputFile);
    }

    @Override
    public void extract(File cover, String outputFile) throws Exception {
        System.out.println("EncryptingSteganographer.extract");
        System.out.println("cover = " + cover + ", outputFile = " + outputFile);

        byte[] coverData = this.stegAlgorithm.extractData(cover);

        // Descompongo el mensaje cifrado: tamaño | cifrado
        int cypherLength = readLength(coverData);
        System.out.println("cypherLength = " + cypherLength);
        byte[] plaintext = cipherHandle.decrypt(Arrays.copyOfRange(coverData, LENGTH_SIZE, LENGTH_SIZE + cypherLength));

        // Descompongo el mensaje plano: tamaño | datos | extensión
        int messageLength = readLength(plaintext);
        System.out.println("messageLength = " + messageLength);
        byte[] fileData = readFileData(plaintext, LENGTH_SIZE, LENGTH_SIZE + messageLength);
        String extension = readExtension(plaintext, 4 + messageLength);
        System.out.println("extension = " + extension);

        System.out.println("Escribiendo resultado en: " + outputFile + extension);
        FileUtils.writeByteArrayToFile(new File(outputFile + extension), fileData);
    }

    @Override
    public String getFilenameStub() {
        return "_" + stegAlgorithm.getName()
            + "_" + cipherHandle.getCipherName()
            + "_" + cipherHandle.getCipherMode();
    }
}
