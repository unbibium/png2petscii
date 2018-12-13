package us.happynet.png2petscii.model;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;

/**
 *
 * @author nickb
 */
abstract public class Glyph {

    protected final int screenCode;
    private final byte[] bytes;

    public Glyph(byte[] data, int screenCode) {
        this.screenCode = screenCode;
        if (data.length != 8) {
            throw new IllegalArgumentException("need 8 bytes, was " + data.length);
        }
        this.bytes=data;
    }

    public final boolean bitmap(int x, int y) {
        return (bytes[y] & (128>>x)) != 0;
    }

    public final boolean bitmap(int index) {
        return (bytes[index/8] & (128>>(index%8))) != 0;
    }

    public final int getScreenCode() {
        return screenCode;
    }

    public final void dump() {
        for (int y = 0; y < 8; y++) {
            for (int x = 0; x < 8; x++) {
                System.out.print(bitmap(x, y) ? "*" : ".");
            }
            System.out.print("\n");
        }
    }
    
    /**
     * Returns the eight bytes that make up this character.
     * @return 
     */
    public byte[] toBytes() {
        return bytes;
    }
    
    abstract void writeData(OutputStream os) throws IOException;
    abstract double diff(BufferedImage tile);
    abstract int[] getRGBArray();

    
}
