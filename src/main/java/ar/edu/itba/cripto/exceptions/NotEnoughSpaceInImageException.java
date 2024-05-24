package ar.edu.itba.cripto.exceptions;

public class NotEnoughSpaceInImageException extends RuntimeException {
    public final int maxMessageLength;
    public final int receivedMessageLength;

    public NotEnoughSpaceInImageException(int maxMessageLength, int receivedMessageLength) {
        super(String.format("Received request to encode %d bytes in image with available size = %d bytes",
            receivedMessageLength, maxMessageLength));
        this.maxMessageLength = maxMessageLength;
        this.receivedMessageLength = receivedMessageLength;
    }

    public int getMaxMessageLength() {
        return maxMessageLength;
    }
}
