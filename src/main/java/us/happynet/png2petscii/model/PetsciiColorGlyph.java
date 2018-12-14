package us.happynet.png2petscii.model;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Represents a PETSCII glyph that may be a different color from others on
 * screen.
 * <p>
 * The main difference is that {@link #diff(java.awt.image.BufferedImage) }
 * compares color channels individually and not just luma.
 * 
 * @author nickb
 */
public class PetsciiColorGlyph extends PetsciiGlyph {

    public PetsciiColorGlyph(byte[] data, int screenCode, PetsciiColor foreground, PetsciiColor background) {
        super(data, screenCode, foreground, background);
    }
    
    public PetsciiColorGlyph(Glyph aglyph, PetsciiColor foreground, PetsciiColor background) {
        this(aglyph.toBytes(), aglyph.screenCode, foreground, background);
    }
    
    public PetsciiColorGlyph(RenderedGlyph aglyph, PetsciiColor foreground) {
        this(aglyph.toBytes(), aglyph.screenCode, foreground, aglyph.getBackgroundColor());
    }

    /**
     * @param imgB
     * @return difference of luma values between this glyph and imgB.
     */
    @Override
    public double diff(BufferedImage imgB) {
        return diff(imgB, PixelDiffStrategy.RGB_DIFF);
    }

    /**
     * Writes the PETSCII bytes to display this glyph to the
     * specified output stream.  Will always write the color
     * code, followed by RVS_ON or RVS_OFF byte, then the
     * character itself.
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
        super.writeData(os);
    }
}
