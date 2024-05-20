package ar.edu.itba.cripto.steganography;

public class LSB4Algorithm extends LSBAlgorithm {
    public LSB4Algorithm() {
        super(4, (byte) 0b00001111);
    }

    //@Override
    //public int hideData(final byte[] msg, final File cover, final File outputFile) throws IOException {
    //    BMPV3Image bmp = new BMPV3Image();
    //    bmp.loadFromFile(cover.getPath());
    //
    //    if (!this.canHideData(msg, bmp)) {
    //        throw new NotEnoughSpaceInImageException(bmp.getHeight() * bmp.getWidth() * 3);
    //    }
    //
    //    byte[] imageData = bmp.getImageData();
    //    byte[] outputData = imageData.clone();
    //    int offset = bmp.getDataOffset();
    //    for (int i = 0; i < msg.length; i++) {
    //        for (int j = 0; j < 2; j++) {
    //
    //            // Limpio los ultimos 4 bits del byte de la imagen.
    //            byte imageByte = imageData[offset + (i * 2) + j];
    //            imageByte &= (byte) 0xF0;
    //
    //            byte mask;
    //            if (j % 2 == 0) {
    //                mask = (byte) (msg[i] >> 4);
    //            } else {
    //                mask = (byte) (msg[i] & 0x0F);
    //            }
    //
    //            imageByte |= mask;
    //            outputData[offset + i * 2 + j] = imageByte;
    //        }
    //    }
    //
    //    FileUtils.writeByteArrayToFile(outputFile, outputData);
    //    return 0;
    //
    //}

}
