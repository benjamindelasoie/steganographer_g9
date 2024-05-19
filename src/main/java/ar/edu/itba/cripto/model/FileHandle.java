package ar.edu.itba.cripto.model;

public record FileHandle(int length, byte[] data, String extension) {
}
