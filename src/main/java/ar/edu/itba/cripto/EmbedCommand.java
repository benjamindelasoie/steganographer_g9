package ar.edu.itba.cripto;

import ar.edu.itba.cripto.model.BMPV3Image;
import ar.edu.itba.cripto.model.CipherHandle;
import ar.edu.itba.cripto.model.EncryptingSteganographer;
import ar.edu.itba.cripto.model.steganography.LSBAlgorithm;
import ar.edu.itba.cripto.model.Steganographer;
import ar.edu.itba.cripto.model.steganography.SteganographyAlgorithm;
import org.apache.commons.io.EndianUtils;
import org.w3c.dom.css.RGBColor;
import picocli.CommandLine.*;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.StandardOpenOption;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.concurrent.Callable;

@Command(name = "-embed", sortOptions = false)
public class EmbedCommand implements Callable<Integer> {
    @Spec
    Model.CommandSpec spec;

    @Option(names = "-in", paramLabel = "INPUT_FILE", required = false,
            description = "Archivo que se va a ocultar")
    File inputFile;

    @Option(names = "-p", paramLabel = "COVER", required = false,
            description = "Archivo bmp que será el portador")
    File coverImage;

    @Option(names = "-out", paramLabel = "OUTPUT_FILE", required = false,
            description = "Archivo bmp de salida")
    File outputFile;

    @Option(names = "-steg", paramLabel = "STEG",required = false,
            description = "Algoritmo de esteganografiado: <LSB | LSB4 | LSBI>")
    String stegName;

    @Option(names = "-a", paramLabel = "CIPHER",defaultValue = "aes128",
            description = "Algoritmo de encriptado: <aes128 | aes192 | aes256 | des>")
    String cipherName;

    @Option(names = "-m", paramLabel = "MODE", defaultValue = "cbc",
            description = "Modo de cifrado de bloque: <ecb | cfb | ofb | cbc>")
    String cipherModeName;

    @Option(names = "-pass", paramLabel = "PASSWORD",description = "Password de encripción")
    String password;

    @Override
    public Integer call() throws Exception {
        System.out.println("Hello from Embedder");


        // Instantiate steganographer
        SteganographyAlgorithm steganographyAlgorithm = SteganographyAlgorithm.getInstance(stegName);
        Steganographer steganographer;
        if (password != null) {
            steganographer = new EncryptingSteganographer(steganographyAlgorithm, cipherName, cipherModeName, password);
        } else {
            steganographer = new Steganographer(steganographyAlgorithm);
        }

        // Do call
        byte[] msgBytes = Files.readAllBytes(inputFile.toPath());
        return steganographer.embed(msgBytes, coverImage, outputFile);
    }

    public static void main(String[] args) throws IOException {

        File inputFile = new File("../archivo.txt");
        File cover = new File("../bmp_images/bmp_24.bmp");
        File output = new File("../averga.bmp");

        Steganographer steg = new Steganographer(new LSBAlgorithm());
        try {
            steg.embed(Files.readAllBytes(inputFile.toPath()), cover, output);
        } catch (Exception e) {
            throw new RuntimeException("No se puede leer el archivo");
        }



    }
}
