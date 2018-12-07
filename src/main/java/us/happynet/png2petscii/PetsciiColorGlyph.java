package us.happynet.png2petscii;

import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;

/**
 * Represents a PETSCII glyph that has been assigned a color.
 * 
 * @author nickb
 */
public class PetsciiColorGlyph extends PetsciiGlyph {

    private final PetsciiColor bgColor;
    private final PetsciiColor fgColor;
    private final int fgBgDifference;
    
    public PetsciiColorGlyph(byte[] data, int screenCode, PetsciiColor bgColor, PetsciiColor fgColor) {
        super(data, screenCode);
        this.bgColor = bgColor;
        this.fgColor = fgColor;
        fgBgDifference = bgColor.diff(fgColor);
    }
    
    public PetsciiColorGlyph(PetsciiGlyph aglyph, PetsciiColor bgColor, PetsciiColor fgColor) {
        this(aglyph.toBytes(), aglyph.screenCode, bgColor, fgColor);
    }
    
    /**
     * Converts an integer color into an array for overloading.
     * @param rgbB
     * @return
     */
    private static int[] intToArray(int rgbB) {
        int[] array = new int[3];
        array[0] = (rgbB >> 16) & 255;
        array[1] = (rgbB >> 8) & 255;
        array[2] = (rgbB) & 255;
        return array;
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
                    PetsciiColor rgbA = bitmap(x,y) ? fgColor : bgColor;
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
    
}
