package us.happynet.png2petscii.model;

import java.awt.image.BufferedImage;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;

/**
 * Color glyph superclass.
 *
 * This is an intermediate step; I made color the responsibility of
 * glyphs when they should be the responsibility of the screen.
 * In time, all the glyph subclasses should go away.
 *
 * @author nickb
 */
abstract public class RenderedGlyph extends Glyph {

    protected final PetsciiColor background;
    protected final PetsciiColor foreground;
    
    protected final WritableImage image = new WritableImage(8,8);
    protected final int[] rgbArray = new int[64];

    /**
     * Creates a version of the source glyph with a fixed color scheme.
     * @param sourceGlyph
     * @param foreground
     * @param background
     */
    public RenderedGlyph(Glyph sourceGlyph, PetsciiColor foreground, PetsciiColor background) {
        this(sourceGlyph.toBytes(), sourceGlyph.getScreenCode(), foreground, background);
    }
    
    /**
     * Create a glyph from eight bytes with a defined foreground and background
     * @param data an array of eight bytes
     * @param screenCode 
     * @param foreground 
     * @param background 
     */
    public RenderedGlyph(byte[] data, int screenCode, PetsciiColor foreground, PetsciiColor background) {
        super(data,screenCode);
        this.background = background;
        this.foreground = foreground;
        if(data.length != 8) {
            throw new IllegalArgumentException("need 8 bytes, was " + data.length);
        }
        final PixelWriter pw = image.getPixelWriter();
        assert pw != null;
        for(int y=0; y<8; y++) {
            for(int x=0; x<8; x++) {
                boolean bit = (data[y] & (1<<x)) != 0;
                pw.setArgb(7-x, y, (bit ? foreground:background).getRGB());
                rgbArray[7-x+y*8]=(bit ? foreground:background).getRGB();
            }
        }
    }

    public PetsciiColor getBackgroundColor() {
        return background;
    }

    public PetsciiColor getForegroundColor() {
        return foreground;
    }

    @Override
    public int[] getRGBArray() {
        return rgbArray;
    }
    
    public int getRGB(int x, int y) {
        return (bitmap(x, y) ? foreground : background).getRGB();
    }

    public int getRGB(int index) {
        return (bitmap(index) ? foreground : background).getRGB();
    }

    static final PixelDiffStrategy STRATEGY = new LumaDiffStrategy();
    
    /**
     * @param imgB
     * @return difference of luma values between this glyph and imgB.
     */
    @Override
    public double diff(BufferedImage imgB) {
        return diff(imgB, STRATEGY);
    }
    
    
    protected double diff(BufferedImage imgB, PixelDiffStrategy strategy) {
        if(imgB == null) {
            throw new NullPointerException("passed null image to diff()");
        }
        long difference = 0;
        for (int y = 0; y < 8; y++) {
            for (int x = 0; x < 8; x++) {
                int rgbA = this.getRGB(x, y);
                int rgbB = imgB.getRGB(x, y);
                difference += strategy.applyAsInt(rgbA, rgbB);
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
