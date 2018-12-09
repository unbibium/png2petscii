package us.happynet.png2petscii.model;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Represents a glyph in the PETSCII character set, with no color information.
 *
 * @author nickb
 */
public class PetsciiGlyph extends Glyph {

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
        super(screenCode,foreground,background);
        if(glyphImage.getHeight() != 8 || glyphImage.getWidth() != 8) {
            throw new IllegalArgumentException("need 8x8, was " + glyphImage.getWidth() + "x" + glyphImage.getHeight());
        }
        final int minx = glyphImage.getMinX();
        final int miny = glyphImage.getMinY();
        for (int y = 0; y<8; y++) { 
            for (int x = 0; x<8; x++) { 
                boolean bit = (glyphImage.getRGB(minx+x,miny+y) & 0xFFFFFF) != 0;
                bitmap[x][y] = bit;
                rgbArray[x+y*8]=(bit ? foreground:background).getRGB();
            } 
        }
        // needs to be stored as a JavaFX image
        
    }

    public PetsciiGlyph(byte[] data, int screenCode, PetsciiColor foreground, PetsciiColor background) {
        super(data, screenCode, foreground, background);
    }
    
    public PetsciiGlyph(byte[] data, int screenCode) {
        this(data,screenCode,PetsciiColor.WHITE,PetsciiColor.BLACK);
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
    @Override
    public void writeData(OutputStream os) throws IOException {
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
    
}
