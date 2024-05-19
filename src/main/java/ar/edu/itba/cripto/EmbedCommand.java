package ar.edu.itba.cripto;

import ar.edu.itba.cripto.model.EncryptingSteganographer;
import ar.edu.itba.cripto.model.Steganographer;
import ar.edu.itba.cripto.model.steganography.LSB4Algorithm;
import ar.edu.itba.cripto.model.steganography.SteganographyAlgorithm;
import picocli.CommandLine.Command;
import picocli.CommandLine.Model;
import picocli.CommandLine.Option;
import picocli.CommandLine.Spec;

import java.io.File;
import java.nio.file.Files;
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

    @Option(names = "-steg", paramLabel = "STEG", required = false,
            description = "Algoritmo de esteganografiado: <LSB | LSB4 | LSBI>")
    String stegName;

    @Option(names = "-a", paramLabel = "CIPHER", defaultValue = "aes128",
            description = "Algoritmo de encriptado: <aes128 | aes192 | aes256 | des>")
    String cipherName;

    @Option(names = "-m", paramLabel = "MODE", defaultValue = "cbc",
            description = "Modo de cifrado de bloque: <ecb | cfb | ofb | cbc>")
    String cipherModeName;

    @Option(names = "-pass", paramLabel = "PASSWORD", description = "Password de encripción")
    String password;

    public static void main(String[] args) throws Exception {

        File inputFile = new File("../archivo.txt");
        File cover = new File("../bmp_images/bmp_24.bmp");
        File output = new File("../averga.bmp");

        EncryptingSteganographer steg = new EncryptingSteganographer(new LSB4Algorithm(),
                "des",
                "ecb",
                "password");

        steg.embed(inputFile, cover, output);

        steg.extract(output);

    }

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
        try {
            return steganographer.embed(inputFile, coverImage, outputFile);
        } catch (RuntimeException e) {
            throw e;
        }
    }
}
