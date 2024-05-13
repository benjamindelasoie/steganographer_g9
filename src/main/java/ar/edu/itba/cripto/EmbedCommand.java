package ar.edu.itba.cripto;

import ar.edu.itba.cripto.model.CipherHandle;
import ar.edu.itba.cripto.model.LSBAlgorithm;
import ar.edu.itba.cripto.model.Steganographer;
import picocli.CommandLine.*;

import java.io.File;
import java.security.MessageDigest;
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

    @Option(names = "-steg", paramLabel = "STEG",required = true,
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

        CipherHandle cipherHandle = new CipherHandle(cipherName, cipherModeName);

        Steganographer steg = new Steganographer(cipherHandle, new LSBAlgorithm(), password, MessageDigest.getInstance("SHA256"));

        return 0;
    }
}
