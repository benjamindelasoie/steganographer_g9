package ar.edu.itba.cripto;

import ar.edu.itba.cripto.model.CipherHandle;
import ar.edu.itba.cripto.model.steganography.LSBAlgorithm;
import ar.edu.itba.cripto.model.Steganographer;
import ar.edu.itba.cripto.model.steganography.SteganographyAlgorithm;
import org.w3c.dom.css.RGBColor;
import picocli.CommandLine.*;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.security.MessageDigest;
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

    @Option(names = "-steg", paramLabel = "STEG",required = false,
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

        // Probando lectura y escritura de BMP
        BufferedImage image = ImageIO.read(coverImage);

        System.out.println("Image loaded: " + image.toString());

        System.out.println("Leyendo imagen");
        for (int x = 0; x < image.getWidth(); x++) {
            for (int y = 0; y < image.getHeight(); y++) {
                Color color = new Color(image.getRGB(x, y));
                image.setRGB(x, y, new Color(color.getBlue(), color.getRed(), color.getGreen()).getRGB());
            }
        }

        File salida = new File("../salida.bmp");
        System.out.println("Escribiendo bmp en: " + salida.getAbsolutePath());
        ImageIO.write(image, "bmp", salida);
        return 0;
    }
}
