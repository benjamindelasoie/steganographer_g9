package ar.edu.itba.cripto;

import picocli.CommandLine;
import picocli.CommandLine.*;

import java.io.File;
import java.util.concurrent.Callable;

@Command(name = "-extract")
public class ExtractCommand implements Callable<Integer> {
    @Option(names = "-p", paramLabel = "COVER", required = true, description = "Archivo bmp portador")
    File bitmapFile;

    @Option(names = "-out", paramLabel = "OUTPUT_FILE", required = true, description = "Archivo de salida obtenido")
    File outputFile;

    @Option(names = "-steg", paramLabel = "STEG",required = true, description = "Algoritmo de esteganografiado: <LSB | LSB4 | LSBI>")
    String stegName;

    @Option(names = "-a", paramLabel = "CIPHER",defaultValue = "aes128", description = "Algoritmo de encriptado: <aes128 | aes192 | aes256 | des")
    String cipherName;

    @Option(names = "-m", paramLabel = "MODE", defaultValue = "cbc", description = "Modo de cifrado de bloque: <ecb | cfb | ofb | cbc>")
    String cipherModeName;

    @Option(names = "-pass", paramLabel = "PASSWORD",description = "Password de encripción")
    String password;


    @Override
    public Integer call() throws Exception {
        validate();

        System.out.println("Hello from Detacher");


        return 0;
    }

    private void validate() {

    }
}
