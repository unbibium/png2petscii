package us.happynet.png2petscii;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Stream;
import javafx.scene.image.Image;
import javafx.scene.image.PixelFormat;
import javafx.scene.image.PixelReader;
import javafx.util.Pair;
import javax.imageio.ImageIO;

/**
 * Represents a CBM font of some kind.
 * in practice, there will be uppercase and lowercase.
 * 
 * @author nickb
 */
public class PetsciiFont {

    private final Raster[] charRasters = new Raster[256];
    private final PetsciiGlyph[] glyphs = new PetsciiGlyph[256];
    private final BufferedImage[] images = new BufferedImage[256];
    
    public PetsciiFont(File f) throws IOException {
        this(ImageIO.read(f));
    }
    
    public PetsciiFont(InputStream f) throws IOException {
        this(ImageIO.read(f));
    }
    
    public PetsciiFont(BufferedImage image) {
        if(image.getHeight() != 64 && image.getWidth() != 256) {
            throw new IllegalArgumentException("invalid image size");
        }
        int characterIndex = 0;
        for (int j=0; j<8; j++) {
            for (int i = 0; i<32; i++) {
                Rectangle r = new Rectangle(i*8, j*8, 8, 8);
                Raster raster = image.getData(r);
                BufferedImage bi = image.getSubimage(i*8, j*8, 8, 8);
                charRasters[characterIndex] = raster;
                images[characterIndex] = bi;
                glyphs[characterIndex] = new PetsciiGlyph(bi, characterIndex);
                characterIndex++;
            }
        }  
    }
    
    public Raster getRaster(int i) {
        return charRasters[i];
    }
    
    public BufferedImage getImage(int i) {
        return images[i];
    }
    
    public PetsciiGlyph getGlyph(int i) {
        return glyphs[i];
    }

    @Deprecated
    PetsciiScreen convert(Image image) {
        PetsciiScreen result = new PetsciiScreen(this);
        result.convert(image);
        return result;
    }
    
    PetsciiScreen convert(BufferedImage image) {
        PetsciiScreen result = new PetsciiScreen(this);
        result.convert(image);
        return result;
    }
    
    PetsciiGlyph findClosest(BufferedImage tile) {
        PetsciiGlyph result = null;
        double score = Double.POSITIVE_INFINITY;
        for(PetsciiGlyph glyph : glyphs) {
            double newscore = glyph.diff(tile);
            if (newscore < score) {
                score = newscore;
                result = glyph;
            }
        }
        return result;
    }
    
}