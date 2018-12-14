package us.happynet.png2petscii.model;

import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javafx.scene.image.Image;

/**
 * generic Font class
 * 
 * @author nickb
 */
abstract public class Font {

    private static final Map<String,Font> LOADED_FONTS = new HashMap<>();
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
                    ? "c64upper.bin" 
                    : "c64lower.bin");
        try (InputStream is = cl.getResourceAsStream(filename)) {
            if (is == null) {
                throw new FileNotFoundException(filename);
            }
            if(s.contains("ATASCII")) {
                byte[] bytes = new byte[1024];
                is.read(bytes);
                return new AtasciiFont(bytes);
            }
            PetsciiFont result = new PetsciiFont(is);
            if(s.contains("color") ) {
                result = new PetsciiColorFont(result);
            }
            LOADED_FONTS.put(s, result);
            return result;
        }    
    }
    
    public static List<String> getFontNames() {
        return FONT_NAMES;
    }
    
    protected static byte[] read1K(File binFile) throws IOException {
        try(FileInputStream fis = new FileInputStream(binFile);
            BufferedInputStream bis = new BufferedInputStream(fis)) {
            return read1K(bis);
        }
    }
    
    protected static byte[] read1K(InputStream is) throws IOException {
        byte[] result = new byte[1024];
        int readResult = is.read(result);
        return result;
    }


    
    public RenderedGlyph findClosest(BufferedImage tile) {
        RenderedGlyph result = null;
        double score = Double.POSITIVE_INFINITY;
        for(RenderedGlyph glyph : getAvailableGlyphs()) {
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
    abstract public Iterable<RenderedGlyph> getAvailableGlyphs();

    public Screen<? extends Font> convert(Image image) {
        Screen<? extends Font> result = newScreen();
        result.convert(image);
        return result;
    }
    
    public Screen<? extends Font> convert(BufferedImage image) {
        Screen<? extends Font> result = newScreen();
        result.convert(image);
        return result;
    }

    abstract public Screen<? extends Font> newScreen();

    abstract public byte[] getNewline();
    
}
