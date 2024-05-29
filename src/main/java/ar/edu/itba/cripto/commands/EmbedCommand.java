package ar.edu.itba.cripto.commands;

import ar.edu.itba.cripto.exceptions.NotEnoughSpaceInImageException;
import ar.edu.itba.cripto.model.Steganographer;
import ar.edu.itba.cripto.steganography.LSBIAlgorithm;
import ar.edu.itba.cripto.steganography.SteganographyAlgorithm;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.io.File;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.Callable;

@SuppressWarnings("ALL")
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

        File inputFile = new File("../inputs/itba.png");
        File cover = new File("../bmp_images/lado.bmp");


        System.out.println("input file length = " + inputFile.length());
        SteganographyAlgorithm steg = new LSBIAlgorithm();

        File embedded = new File(FilenameUtils.removeExtension(String.valueOf(inputFile))
            + "_" + steg.getName() + "_" + LocalTime.now().format(DateTimeFormatter.ofPattern("HH_mm_ss")) + ".bmp");

        File extractionFile = new File(FilenameUtils.removeExtension(String.valueOf(embedded)) + "_extraction");

        Steganographer steganographer = Steganographer.getSteganographer(steg.getName(),
            "des", "cbc", null);

        steganographer.embed(inputFile, cover, embedded);
        steganographer.extract(embedded, extractionFile.getPath());

        if (FileUtils.contentEquals(inputFile, extractionFile)) {
            System.out.println("exito: input y output son iguales");
        } else {
            System.out.println("No son iguales");
        }

        //File bmp = new File("../ejemplo2024/ladoLSB4.bmp");
        //Steganographer steganographer = Steganographer.getSteganographer("LSB4");
        //steganographer.extract(bmp, "../ejemplosalidalsbi_eee");

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
