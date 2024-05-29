package ar.edu.itba.cripto.steganography;

public class LSB4Algorithm extends LSBAlgorithm {
    public static final int SIGNIFICANT_BITS = 4;
    public static final byte MASK = 0b00001111;

    public LSB4Algorithm() {
        super(SIGNIFICANT_BITS, MASK);
    }

    @Override
    public String getName() {
        return "LSB4";
    }
}
