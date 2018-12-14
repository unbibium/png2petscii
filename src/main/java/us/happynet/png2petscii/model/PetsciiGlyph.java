package us.happynet.png2petscii.model;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import us.happynet.png2petscii.io.PetsciiOptimizingOutputStream;

/**
 * Represents a glyph in the PETSCII character set.
 *
 * Originally, this class was meant to refer to a PETSC
 * @author nickb
 */
public class PetsciiGlyph extends RenderedGlyph {

    public PetsciiGlyph(byte[] data, int screenCode, PetsciiColor foreground, PetsciiColor background) {
        super(data, screenCode, foreground, background);
        for (int y = 0; y<8; y++) { 
            for (int x = 0; x<8; x++) { 
                boolean bit = bitmap(x,y);
                rgbArray[x+y*8]=(bit ? foreground:background).getRGB();
            } 
        }
    }
    
    public PetsciiGlyph(byte[] data, int screenCode) {
        this(data,screenCode,PetsciiColor.WHITE,PetsciiColor.BLACK);
    }

    public PetsciiGlyph(Glyph aglyph, PetsciiColor foreground, PetsciiColor background) {
        this(aglyph.toBytes(), aglyph.screenCode, foreground, background);
    }
    
    
    /**
     * @param imgB
     * @return 
     */
    @Override
    public double diff(BufferedImage imgB) {
        return diff(imgB, PixelDiffStrategy.RGB_DIFF);
    }

    public static final byte RVS_ON = 0x12;
    public static final byte RVS_OFF = (byte) 0x92;
    public static final byte CRSR_RIGHT = 0x1D;

    /**
     * Writes the PETSCII bytes to display this glyph to the
     * specified output stream.  Will always write the color code
     * first, and then an RVS_ON or RVS_OFF byte, and then the
     * PETSCII character code itself.  Using an instance of
     * {@link PetsciiOptimizingOutputStream} as your output
     * stream will strip out these extra control codes when
     * they are redundant.
     * @param os Output stream to receive the bytes.
     * @throws java.io.IOException if the write fails.
     */
    @Override
    public void writeData(OutputStream os) throws IOException {
        if((byte) 0x20 == (screenCode & 0xBF)) {
            os.write(CRSR_RIGHT);
            return;
        } 
        os.write(foreground.getPetscii());
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
