package us.happynet.png2petscii;

import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
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

    private final int fgBgDifference;
    
    public PetsciiColorGlyph(byte[] data, int screenCode, PetsciiColor foreground, PetsciiColor background) {
        super(data, screenCode, foreground, background);
        fgBgDifference = background.diff(foreground);
    }
    
    public PetsciiColorGlyph(PetsciiGlyph aglyph, PetsciiColor foreground, PetsciiColor background) {
        this(aglyph.toBytes(), aglyph.screenCode, foreground, background);
    }
    
    public PetsciiColorGlyph(PetsciiGlyph aglyph, PetsciiColor foreground) {
        this(aglyph.toBytes(), aglyph.screenCode, foreground, aglyph.getBackgroundColor());
    }

    @Override
    public double diff(BufferedImage otherImage) {
        WritableRaster alphaRaster = otherImage.getAlphaRaster();
        long difference = 0;
        for (int y = 0; y < 8; y++) {
            for (int x = 0; x < 8; x++) {
                if (alphaRaster != null && alphaRaster.getSample(x, y, 0) > 0) {
                    if (bitmap(x,y)) {
                        difference += fgBgDifference;
                    }
                } else {
                    int rgbB = otherImage.getRGB(x, y);
                    PetsciiColor rgbA = bitmap(x,y) ? foreground : background;
                    difference += rgbA.diff(rgbB);
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
        os.write(foreground.getPetscii());
        super.writeData(os);
    }
}
