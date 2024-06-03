package ar.edu.itba.cripto.commands;

import ar.edu.itba.cripto.exceptions.NotEnoughSpaceInImageException;
import ar.edu.itba.cripto.model.Steganographer;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.io.File;
import java.util.concurrent.Callable;

@Command(name = "-embed", sortOptions = false, sortSynopsis = false,
    mixinStandardHelpOptions = true,
    description = {"Embed any type of file on a .bmp file with optional encryption"})
public class EmbedCommand implements Callable<Integer> {

    @Option(names = "-in", paramLabel = "INPUT_FILE", required = true,
        description = "Archivo que se va a ocultar")
    File inputFile;

    @Option(names = "-p", paramLabel = "COVER", required = true,
        description = "Archivo bmp que será el portador")
    File coverImage;

    @Option(names = "-out", paramLabel = "OUTPUT_FILE", required = true,
        description = "Archivo bmp de salida")
    File outputFile;

    @Option(names = "-steg", paramLabel = "STEG", required = true,
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
        String cipherName = "des";
        String cipherMode = "cfb";

        Steganographer steganographer = Steganographer.getSteganographer("LSBI",
            "aes256",
            "cbc",
            "margarita");


        File input = new File("../inputs/funes.txt");
        File cover = new File("../bmp_images/lado.bmp");
        File embedded = new File("../embedded" + steganographer.getFilenameStub() + ".bmp");

        steganographer.embed(input, cover, embedded);

        String extractionFilename = "../extraccion" + steganographer.getFilenameStub();

        steganographer.extract(embedded, extractionFilename);

    }

    public static void ejemploCatedra() throws Exception {
        Steganographer steganographer = Steganographer.getSteganographer("LSBI",
            "des",
            "cfb",
            "margarita");

        steganographer.extract(new File("../ejemplo2024/ladoLSBIdescfb.bmp"), "../AVERGA");
    }

    @Override
    public Integer call() throws Exception {
        validate();

        Steganographer steganographer = Steganographer.getSteganographer(stegName, cipherName, cipherModeName, password);

        try {
            steganographer.embed(inputFile, coverImage, outputFile);
        } catch (NotEnoughSpaceInImageException notEnoughSpaceInImageException) {
            System.out.println(notEnoughSpaceInImageException.getMessage());
        }

        return 0;
    }


    private void validate() {
        // TODO: Implementar validación de parámetros acá.
    }
}
