package ar.edu.itba.cripto.commands;

import ar.edu.itba.cripto.model.Steganographer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.io.File;
import java.util.concurrent.Callable;

@Command(name = "-extract", sortOptions = false, sortSynopsis = false, mixinStandardHelpOptions = true,
    description = {"Extract a hidden file out of a bmp image"})
public class ExtractCommand implements Callable<Integer> {
    public static final Logger logger = LoggerFactory.getLogger(ExtractCommand.class);

    @Option(names = {"-p", "-cover"}, paramLabel = "COVER", required = true,
        description = "The .bmp cover file")
    File bitmapFile;

    @Option(names = {"-o", "-out"}, paramLabel = "OUTPUT_FILE", required = true,
        description = "Desired output file name")
    String outputFile;

    @Option(names = {"-s", "-steg"}, paramLabel = "STEG", required = true,
        description = "Steganography algorithm < LSB1 | LSB4 | LSBI >")
    String stegName;

    @Option(names = {"-a", "-cipher"}, paramLabel = "CIPHER", defaultValue = "aes128",
        description = "Cipher algorithm: <aes128 | aes192 | aes256 | des")
    String cipherName;

    @Option(names = {"-m", "-mode"}, paramLabel = "MODE", defaultValue = "cbc",
        description = "Cipher block mode of operation: <ecb | cfb | ofb | cbc>")
    String cipherModeName;

    @Option(names = {"-c", "-pass"}, paramLabel = "PASSWORD",
        description = "Cipher password")
    String password;

    @Override
    public Integer call() throws Exception {
        logger.info("Start of extract command with parameters: {} {} {} {} {} {}",
            bitmapFile, outputFile, stegName, cipherName, cipherModeName, password);

        validate();

        Steganographer steganographer = Steganographer.getSteganographer(stegName,
            cipherName,
            cipherModeName,
            password);

        steganographer.extract(bitmapFile, outputFile);

        return 0;
    }

    private void validate() {
        //TODO: Implementar validación de los parámetros.
    }
}
