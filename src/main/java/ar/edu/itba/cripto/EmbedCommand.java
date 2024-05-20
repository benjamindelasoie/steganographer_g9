package ar.edu.itba.cripto;

import ar.edu.itba.cripto.model.EncryptingSteganographer;
import ar.edu.itba.cripto.model.Steganographer;
import ar.edu.itba.cripto.model.steganography.LSB4Algorithm;
import ar.edu.itba.cripto.model.steganography.LSBAlgorithm;
import ar.edu.itba.cripto.model.steganography.SteganographyAlgorithm;
import picocli.CommandLine.Command;
import picocli.CommandLine.Model;
import picocli.CommandLine.Option;
import picocli.CommandLine.Spec;

import java.io.File;
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
            description = "Algoritmo de esteganografiado: <LSB1 | LSB4 | LSBI>")
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
        File output4 = new File("averga4.bmp");

        Steganographer steganographer = new Steganographer(new LSBAlgorithm());
        Steganographer steg4 = new Steganographer(new LSB4Algorithm());

        EncryptingSteganographer encrypter = new EncryptingSteganographer(new LSBAlgorithm(),
                "des",
                "ecb",
                "password");

        try {
            steganographer.embed(inputFile, cover, output);
            steganographer.extract(output);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        steg4.embed(inputFile, cover, output4);
        steg4.extract(output4);
    }

    @Override
    public Integer call() throws Exception {
        validate();

        // Instantiate steganographer
        SteganographyAlgorithm steganographyAlgorithm = SteganographyAlgorithm.getInstance(stegName);
        Steganographer steganographer;
        if (password != null) {
            steganographer = new EncryptingSteganographer(steganographyAlgorithm, cipherName, cipherModeName, password);
        } else {
            steganographer = new Steganographer(steganographyAlgorithm);
        }

        steganographer.embed(inputFile, coverImage, outputFile);

        return 0;
    }


    private void validate() {
        // TODO: Implementar validación de parámetros acá.
    }
}
