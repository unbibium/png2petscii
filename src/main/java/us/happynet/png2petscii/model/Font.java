package us.happynet.png2petscii.model;

import java.awt.image.BufferedImage;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javafx.scene.image.Image;
import javax.imageio.ImageIO;

/**
 * generic Font class
 * 
 * @author nickb
 * @param <G> Glyph class
 */
abstract public class Font<G extends Glyph> {

    private static final Map<String,PetsciiFont> LOADED_FONTS = new HashMap<>();
    private static final List<String> FONT_NAMES = Arrays.asList(
            "lowercase color","uppercase color",
            "lowercase mono","uppercase mono",
            "ATASCII");
    
    /**
     * Factory method for fonts.
     * @param s
     * @return
     * @throws IOException 
     */
    public static Font get(String s) throws IOException {
        if(LOADED_FONTS.containsKey(s)) {
            return LOADED_FONTS.get(s);
        }
        ClassLoader cl = Font.class.getClassLoader();
        String filename = s.contains("ATASCII") 
                ? "atascii.bin" 
                : (s.contains("uppercase") 
                    ? "charset_upper.png" 
                    : "charset_lower.png");
        try (InputStream is = cl.getResourceAsStream(filename)) {
            if (is == null) {
                throw new FileNotFoundException(filename);
            }
            if(s.contains("ATASCII")) {
                byte[] bytes = new byte[1024];
                is.read(bytes);
                return new AtasciiFont(bytes);
            }
            BufferedImage im = ImageIO.read(is);
            PetsciiFont result = new PetsciiFont(im);
            if(s.contains("color") ) {
                result = new PetsciiColorFont(result);
            }
            LOADED_FONTS.put(s, result);
            return result;
        }    
    }
    
    public static List getFontNames() {
        return FONT_NAMES;
    }

    
    public G findClosest(BufferedImage tile) {
        G result = null;
        double score = Double.POSITIVE_INFINITY;
        for(G glyph : getAvailableGlyphs()) {
            double newscore = glyph.diff(tile);
            if (newscore < score) {
                score = newscore;
                result = glyph;
            }
        }
        return result;
    }

    /**
     * Provides all glyphs used by the given instnace.
     * In the most ordinary case, will return every glyph in the
     * entire font.  In some instances, will filter out ugly
     * characters that probably wouldn't look good in the finished
     * product.  In others, will provide several different versions
     * of each glyph.
     * @return all glyphs that are allowed to be used in output files
     */
    abstract public Iterable<G> getAvailableGlyphs();

    public Screen<? extends Font,G> convert(Image image) {
        Screen<? extends Font,G> result = newScreen();
        result.convert(image);
        return result;
    }
    
    public Screen<? extends Font,G> convert(BufferedImage image) {
        Screen<? extends Font,G> result = newScreen();
        result.convert(image);
        return result;
    }

    abstract public Screen<? extends Font,G> newScreen();

    abstract public byte[] getNewline();
    
}
