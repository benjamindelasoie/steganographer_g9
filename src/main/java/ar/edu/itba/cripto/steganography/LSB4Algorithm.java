package ar.edu.itba.cripto.steganography;

public class LSB4Algorithm extends LSBAlgorithm {
    public LSB4Algorithm() {
        super(4, (byte) 0b00001111);
    }

    @Override
    public String getName() {
        return "LSB4";
    }
}
