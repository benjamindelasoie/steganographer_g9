package ar.edu.itba.cripto.exceptions;

public class NotEnoughSpaceInImageException extends RuntimeException {
    public int maxMessageLength;

    public NotEnoughSpaceInImageException(int maxMessageLength) {
        super("There is not enough space in the image for this message. Max space: " + maxMessageLength);
        this.maxMessageLength = maxMessageLength;
    }

    public int getMaxMessageLength() {
        return maxMessageLength;
    }
}
