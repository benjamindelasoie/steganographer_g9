package ar.edu.itba.cripto.commands;

import ar.edu.itba.cripto.exceptions.NotEnoughSpaceInImageException;
import ar.edu.itba.cripto.model.Steganographer;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.io.File;
import java.util.concurrent.Callable;

@Command(name = "-embed", sortOptions = false, sortSynopsis = false,
    mixinStandardHelpOptions = true,
    description = {"Embed any type of file on a .bmp image, with optional encryption"})
public class EmbedCommand implements Callable<Integer> {

    @Option(names = "-in", paramLabel = "INPUT_FILE", required = true,
        description = "Archivo que se va a ocultar")
    File inputFile;

    @Option(names = {"-p", "-cover"}, paramLabel = "COVER", required = true,
        description = "Archivo bmp que será el portador")
    File coverImage;

    @Option(names = {"-o", "-out"}, paramLabel = "OUTPUT_FILE", required = true,
        description = "Archivo bmp de salida")
    File outputFile;

    @Option(names = {"-s", "-steg"}, paramLabel = "STEG", required = true,
        description = "Algoritmo de esteganografiado: <LSB1 | LSB4 | LSBI>")
    String stegName;

    @Option(names = {"-a", "-cipher"}, paramLabel = "CIPHER", defaultValue = "aes128",
        description = "Algoritmo de encriptado: <aes128 | aes192 | aes256 | des>")
    String cipherName;

    @Option(names = {"-m", "-mode"}, paramLabel = "MODE", defaultValue = "cbc",
        description = "Modo de cifrado de bloque: <ecb | cfb | ofb | cbc>")
    String cipherModeName;

    @Option(names = {"-c", "-pass"}, paramLabel = "PASSWORD", description = "Password de encripción")
    String password;

    public static void main(String[] args) throws Exception {
        Steganographer steganographer = Steganographer.getSteganographer("LSB1",
            "aes128", "cbc", "exitoso");

        File cover = new File("../grupo9/quito.bmp");
        String output = "../outputs/quito";

        steganographer.extract(cover, output);
    }

    @Override
    public Integer call() throws Exception {
        validate();

        Steganographer steganographer = Steganographer.getSteganographer(stegName, cipherName, cipherModeName, password);

        try {
            steganographer.embed(inputFile, coverImage, outputFile);
        } catch (NotEnoughSpaceInImageException notEnoughSpaceInImageException) {
            System.out.println(notEnoughSpaceInImageException.getMessage());
        } catch (RuntimeException e) {
            System.out.println("Unexpected exception: " + e.getMessage());
        }

        return 0;
    }


    private void validate() {
        // TODO: Implementar validación de parámetros acá.
    }
}
