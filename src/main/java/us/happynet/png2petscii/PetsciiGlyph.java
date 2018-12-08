package us.happynet.png2petscii;

import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.IOException;
import java.io.OutputStream;
import javafx.scene.image.Image;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;

/**
 * Represents a glyph in the PETSCII character set, with no color information.
 *
 * @author nickb
 */
public class PetsciiGlyph {

    protected final boolean[][] bitmap = new boolean[8][8];
    protected final int screenCode;
    protected final WritableImage image = new WritableImage(8,8);
    private PetsciiColor background;
    private PetsciiColor foreground;
    
    public PetsciiColor getBackgroundColor() {
        return background;
    }

    public PetsciiColor getForegroundColor() {
        return foreground;
    }

    protected boolean bitmap(int x, int y) {
        return bitmap[x][y];
    }
    
    public int getScreenCode() {
        return screenCode;
    }
    
    public PetsciiGlyph(BufferedImage glyphImage, int screenCode) {
        this(glyphImage, screenCode, PetsciiColor.WHITE, PetsciiColor.BLACK);
    }
    
    /**
     * build glyph from buffered sub-image
     * @param glyphImage an 8x8 BufferedImage that can be read
     * @param screenCode 
     * @param foreground 
     * @param background 
     */
    public PetsciiGlyph(BufferedImage glyphImage, int screenCode, PetsciiColor foreground, PetsciiColor background) {
        this(screenCode,foreground,background);
        if(glyphImage.getHeight() != 8 || glyphImage.getWidth() != 8) {
            throw new IllegalArgumentException("need 8x8, was " + glyphImage.getWidth() + "x" + glyphImage.getHeight());
        }
        final PixelWriter pw = image.getPixelWriter();
        final int minx = glyphImage.getMinX();
        final int miny = glyphImage.getMinY();
        for (int y = 0; y<8; y++) { 
            for (int x = 0; x<8; x++) { 
                boolean bit = (glyphImage.getRGB(minx+x,miny+y) & 0xFFFFFF) != 0;
                bitmap[x][y] = bit;
                pw.setArgb(x, y, (bit ? foreground:background).getRGB());
            } 
        }
        // needs to be stored as a JavaFX image
        
    }
    
    public PetsciiGlyph(byte[] data, int screenCode) {
        this(data,screenCode,PetsciiColor.WHITE,PetsciiColor.BLACK);
    }
    
    /**
     * Create a PetsciiGlyph from eight bytes
     * @param data an array of eight bytes
     * @param screenCode 
     * @param foreground 
     * @param background 
     */
    public PetsciiGlyph(byte[] data, int screenCode, PetsciiColor foreground, PetsciiColor background) {
        this(screenCode,foreground,background);
        if(data.length != 8) {
            throw new IllegalArgumentException("need 8 bytes, was " + data.length);
        }
        final PixelWriter pw = image.getPixelWriter();
        assert pw != null;
        for(int y=0; y<8; y++) {
            for(int x=0; x<8; x++) {
                boolean bit = (data[y] & (1<<x)) != 0;
                bitmap[7-x][y] = bit;
                pw.setArgb(7-x, y, (bit ? foreground:background).getRGB());
            }
        }
    }
    
    private PetsciiGlyph(int screenCode, PetsciiColor foreground, PetsciiColor background) {
        this.screenCode = screenCode;
        this.background = background;
        this.foreground = foreground;
    }
    
    public void dump() {
        for (int y = 0; y < 8; y++) { 
            for (int x = 0; x < 8; x++) { 
                System.out.print(bitmap(x,y) ? "*" : ".");
            } 
            System.out.print("\n");
        } 
    }
    
    /**
     * Returns the eight bytes that make up this character.
     * @return 
     */
    public byte[] toBytes() {
        byte[] result = new byte[8];
        for(int y=0; y<8; y++) {
            byte row = 0;
            for (int x=0; x<8; x++) {
                row <<= 1;
                if(bitmap(x,y)) {
                    row |= (byte) 1;
                }
            }
            result[y]=row;
        }
        return result;
    }
    
    public double diff(BufferedImage imgB) {
        int fgBgDifference = 255 * 3;
        final WritableRaster alphaRaster = imgB.getAlphaRaster();
        long difference = 0;
        for (int y = 0; y < 8; y++) {
            for (int x = 0; x < 8; x++) {
                if (alphaRaster != null && alphaRaster.getSample(x, y, 0) > 0) {
                    if (bitmap(x,y)) {
                        difference += fgBgDifference;
                    }
                } else {
                    int rgbB = imgB.getRGB(x, y);
                    int lumaB = (rgbB >> 16) & 255; 
                    lumaB +=  (rgbB >> 8) & 255;
                    lumaB +=  (rgbB) & 255;
                    int lumaA = bitmap(x,y) ? fgBgDifference : 0;
                    difference += Math.abs(lumaA - lumaB);
                }
            }
        }
        // Normalizing the value of different pixels
        // for accuracy(average pixels per color
        // component)
        double avg_different_pixels = difference / 192;
        // There are 255 values of pixels in total
        double percentage = (avg_different_pixels / 255) * 100;
        return percentage;

    }
    
    public static final byte RVS_ON = 0x12;
    public static final byte RVS_OFF = (byte) 0x92;
    
    /**
     * Writes the PETSCII bytes to display this glyph to the
     * specified output stream.  Will always write a RVS_ON
     * or RVS_OFF byte first.
     * @param os Output stream to receive the bytes.
     * @throws java.io.IOException if the write fails.
     */
    public void writePetscii(OutputStream os) throws IOException {
        os.write ( (screenCode > 128) ? RVS_ON : RVS_OFF );
        byte lowBits = (byte) (screenCode & 0x7F);
        switch(lowBits & 0x60) {
            case 0x20: // punctuation range
                os.write(lowBits);
                break;
            case 0x40: // uppercase or shifted graphics
                os.write(lowBits | 0x80);
                break;
            case 0x00: // lowercase or unshifted uppercase
            case 0x60: // Commodore-key graphics
                os.write(lowBits + 0x40);
                break;                
        }
    }

    Image getImage() {
        return image;
    }
    
}
