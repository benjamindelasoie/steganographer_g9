package ar.edu.itba.cripto.commands;

import ar.edu.itba.cripto.exceptions.NotEnoughSpaceInImageException;
import ar.edu.itba.cripto.model.EncryptingSteganographer;
import ar.edu.itba.cripto.model.Steganographer;
import ar.edu.itba.cripto.steganography.LSBAlgorithm;
import ar.edu.itba.cripto.steganography.SteganographyAlgorithm;
import org.apache.commons.io.FileUtils;
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
        SteganographyAlgorithm steg = new LSBAlgorithm();

        File output = new File("../" + inputFile.getName() + steg.getName() + ".bmp");

        String desencriptado = "../desencriptado";

        EncryptingSteganographer encrypter = new EncryptingSteganographer(steg,
            "des",
            "ofb",
            "trigonomia");

        try {
            encrypter.embed(inputFile, cover, output);
            encrypter.extract(output, desencriptado);
        } catch (NotEnoughSpaceInImageException exception) {
            System.out.println(exception.getMessage());
        }

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
