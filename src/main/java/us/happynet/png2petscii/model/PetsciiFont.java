package us.happynet.png2petscii.model;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import static us.happynet.png2petscii.model.Font.read1K;

/**
 * Represents a CBM font of some kind.
 * in practice, there will be uppercase and lowercase.
 * 
 * @author nickb
 */
public class PetsciiFont extends Font<PetsciiGlyph> {

    protected final List<PetsciiGlyph> glyphs;
    private final BufferedImage[] images = new BufferedImage[256];

    /**
     * @param binFile a 1024-byte font bitmap
     * @throws java.io.IOException
     */
    public PetsciiFont(File binFile) throws IOException {
        this(read1K(binFile));
    }
        
    public PetsciiFont(InputStream is) throws IOException {
        this(read1K(is));
    }
    
    public PetsciiFont(byte[] bitmap) {
        if(bitmap.length != 1024) {
            throw new IllegalArgumentException("bitmap is wrong size");
        }
        glyphs = new ArrayList<>(256);
        for(int i=0; i<128; i++) {
            byte[] glyphBitmap = Arrays.copyOfRange(bitmap, i*8, i*8+8);
            glyphs.add(new PetsciiGlyph(glyphBitmap, i));
        }
        for(int i=0; i<128; i++) {
            byte[] glyphBitmap = Arrays.copyOfRange(bitmap, i*8, i*8+8);
            byte[] inverseBitmap = new byte[8];
            for(int y=0; y<8; y++) {
                inverseBitmap[y]=(byte) (0xFF ^ glyphBitmap[y]);
            }
            glyphs.add(new PetsciiGlyph(inverseBitmap, i+128));
        }
    }
    
    protected PetsciiFont(PetsciiFont srcFont) {
        this.glyphs = srcFont.glyphs;
    }

    /**
     * I think this is only used in unit tests
     * @param i
     * @return 8x8 image of the requested glyph
     */
    public BufferedImage getImage(int i) {
        PetsciiGlyph g = getGlyph(i);
        BufferedImage bi = new BufferedImage(8,8,BufferedImage.TYPE_INT_ARGB);
        bi.setRGB(0, 0, 8, 8, g.getRGBArray(), 0, 8);
        return bi;
    }
    
    public PetsciiGlyph getGlyph(int i) {
        return glyphs.get(i);
    }

    @Override
    public Iterable<PetsciiGlyph> getAvailableGlyphs() {
        return glyphs;
    }

    @Override
    public Screen<PetsciiFont> newScreen() {
        return new PetsciiScreen(this);
    }
    
    private static final byte[] NEWLINE = { 13 };

    @Override
    public byte[] getNewline() {
        return NEWLINE;
    }
    

}