package ar.edu.itba.cripto.commands;

import ar.edu.itba.cripto.model.EncryptingSteganographer;
import ar.edu.itba.cripto.model.Steganographer;
import ar.edu.itba.cripto.steganography.SteganographyAlgorithm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.io.File;
import java.util.concurrent.Callable;

@Command(name = "-extract")
public class ExtractCommand implements Callable<Integer> {
    @Option(names = "-p", paramLabel = "COVER", required = true,
            description = "Archivo bmp portador")
    File bitmapFile;

    @Option(names = "-out", paramLabel = "OUTPUT_FILE", required = true,
            description = "Archivo de salida obtenido")
    String outputFile;

    @Option(names = "-steg", paramLabel = "STEG", required = true,
            description = "Algoritmo de esteganografiado: <LSB1 | LSB4 | LSBI>")
    String stegName;

    @Option(names = "-a", paramLabel = "CIPHER", defaultValue = "aes128",
            description = "Algoritmo de encriptado: <aes128 | aes192 | aes256 | des")
    String cipherName;

    @Option(names = "-m", paramLabel = "MODE", defaultValue = "cbc",
            description = "Modo de cifrado de bloque: <ecb | cfb | ofb | cbc>")
    String cipherModeName;

    @Option(names = "-pass", paramLabel = "PASSWORD",
            description = "Password de encripción")
    String password;

    public static final Logger logger = LoggerFactory.getLogger(ExtractCommand.class);

    @Override
    public Integer call() throws Exception {
        logger.info("Start of extract command with parameters: {} {} {} {} {} {}",
                bitmapFile, outputFile, stegName, cipherName, cipherModeName, password);

        validate();

        Steganographer steg = getSteganographer(stegName, cipherName, cipherModeName, password);

        steg.extract(bitmapFile, outputFile);

        return 0;
    }

    private Steganographer getSteganographer(String stegAlgo, String cipherName, String cipherModeName, String password) throws Exception {
        SteganographyAlgorithm algo = SteganographyAlgorithm.getInstance(stegAlgo);
        if (password == null) {
            return new Steganographer(algo);
        } else {
            return new EncryptingSteganographer(algo, cipherName, cipherModeName, password);
        }
    }

    private void validate() {
        //TODO: Implementar validación de los parámetros.
    }
}
