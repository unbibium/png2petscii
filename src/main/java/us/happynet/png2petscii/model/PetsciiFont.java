package us.happynet.png2petscii.model;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;

/**
 * Represents a CBM font of some kind.
 * in practice, there will be uppercase and lowercase.
 * 
 * @author nickb
 */
public class PetsciiFont extends Font<PetsciiGlyph> {

    protected final List<PetsciiGlyph> glyphs;
    private final BufferedImage[] images = new BufferedImage[256];
    
    public PetsciiFont(File f) throws IOException {
        this(ImageIO.read(f));
    }
    
    public PetsciiFont(InputStream f) throws IOException {
        this(ImageIO.read(f));
    }
    
    protected PetsciiFont(PetsciiFont srcFont) {
        this.glyphs = srcFont.glyphs;
    }
    
    public PetsciiFont(BufferedImage image) {
        if(image.getHeight() != 64 && image.getWidth() != 256) {
            throw new IllegalArgumentException("invalid image size");
        }
        glyphs = new ArrayList<>(256);
        int characterIndex = 0;
        for (int j=0; j<8; j++) {
            for (int i = 0; i<32; i++) {
                BufferedImage bi = image.getSubimage(i*8, j*8, 8, 8);
                images[characterIndex] = bi;
                glyphs.add(new PetsciiGlyph(bi, characterIndex));
                characterIndex++;
            }
        }  
    }
    
    public BufferedImage getImage(int i) {
        return images[i];
    }
    
    public PetsciiGlyph getGlyph(int i) {
        return glyphs.get(i);
    }

    @Override
    public Iterable<PetsciiGlyph> getAvailableGlyphs() {
        return glyphs;
    }

    @Override
    public Screen<PetsciiFont,PetsciiGlyph> newScreen() {
        return new PetsciiScreen(this);
    }
    
    private static final byte[] NEWLINE = { 13 };

    @Override
    public byte[] getNewline() {
        return NEWLINE;
    }
    

}