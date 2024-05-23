package ar.edu.itba.cripto.commands;

import ar.edu.itba.cripto.exceptions.NotEnoughSpaceInImageException;
import ar.edu.itba.cripto.model.Steganographer;
import ar.edu.itba.cripto.steganography.LSBIAlgorithm;
import ar.edu.itba.cripto.steganography.SteganographyAlgorithm;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import picocli.CommandLine.Command;
import picocli.CommandLine.Model;
import picocli.CommandLine.Option;
import picocli.CommandLine.Spec;

import java.io.File;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.Callable;

@Command(name = "-embed", sortOptions = false)
public class EmbedCommand implements Callable<Integer> {
    @Spec
    Model.CommandSpec spec;

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

        File inputFile = new File("../funes.txt");
        File cover = new File("../bmp_images/blackbuck.bmp");
        SteganographyAlgorithm steg = new LSBIAlgorithm();

        File output = new File(FilenameUtils.removeExtension(String.valueOf(inputFile))
            + "_" + steg.getName() + "_" + LocalTime.now().truncatedTo(ChronoUnit.SECONDS) + ".bmp");

        String desencriptado = "../desencriptado";

        Steganographer steganographer = Steganographer.getSteganographer("LSBI",
            "aes128", "cbc", "turuleca");

        steganographer.embed(inputFile, cover, output);

        steganographer.extract(output, "../output");

        //EncryptingSteganographer encrypter = new EncryptingSteganographer(steg,
        //    "des",
        //    "ofb",
        //    "trigonomia");
        //
        //try {
        //    encrypter.embed(inputFile, cover, output);
        //    encrypter.extract(output, desencriptado);
        //} catch (NotEnoughSpaceInImageException exception) {
        //    System.out.println(exception.getMessage());
        //}

        if (FileUtils.contentEquals(inputFile, new File("../desencriptado.txt"))) {
            System.out.println("exito: input y output son iguales");
        } else {
            System.out.println("No son iguales");
        }

    }

    @Override
    public Integer call() throws Exception {
        validate();

        // Instantiate steganographer
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
