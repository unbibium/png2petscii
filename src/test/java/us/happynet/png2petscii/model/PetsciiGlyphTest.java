package us.happynet.png2petscii.model;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import org.junit.Assert;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import org.junit.AssumptionViolatedException;
import org.junit.Test;

/**
 * 
 *
 * @author nickb
 */
public class PetsciiGlyphTest {
    
    /**
     * The at-sign is useful because it is asymmetrical, and easily
     * remembered as the glyph that appears when you poke 0 into the screen.
     */
    private static final byte[] AT_SIGN_BYTES = {60,102,110,110,96,98,60,0};

    private static PetsciiFont getFont(String fontFilename) {
        ClassLoader cl = PetsciiGlyph.class.getClassLoader();
        File f = new File(cl.getResource(fontFilename).getFile());
        try {
            return new PetsciiFont(f);
        } catch (IOException ex) {
            throw new AssumptionViolatedException("Couldn't create PETSCII font for some tests", ex);
        }
    }

    private static final PetsciiFont LOWERCASE_FONT = getFont("c64lower.bin");
    
    @Test
    public void canInitializeWithBytes() {
        PetsciiGlyph glyph = new PetsciiGlyph(AT_SIGN_BYTES, 0);
        Assert.assertArrayEquals(AT_SIGN_BYTES, glyph.toBytes());
    }
    
    @Test
    public void canInitializeWithFont() throws IOException {
        Glyph glyph = LOWERCASE_FONT.getGlyph(0);
        Assert.assertArrayEquals(AT_SIGN_BYTES, glyph.toBytes());
    }
    
    @Test
    public void diffItselfEqualsZero() throws IOException {
        PetsciiGlyph atSignGlyph = new PetsciiGlyph(AT_SIGN_BYTES, 0);
        BufferedImage atSignImage = LOWERCASE_FONT.getImage(0);
        assertEquals(0,atSignGlyph.diff(atSignImage), 0.001);
    }

    @Test
    public void diffOtherCharacterNotEqualZero() {
        PetsciiGlyph atSignGlyph = new PetsciiGlyph(AT_SIGN_BYTES, 0);
        BufferedImage letterAImage = LOWERCASE_FONT.getImage(1);
        assertNotNull("font has image 1", letterAImage);
        assertTrue(atSignGlyph.diff(letterAImage) > 0);
    }
    
    @Test
    public void differenceIsHigherWithReversedSelf() {
        PetsciiGlyph atSignGlyph = new PetsciiGlyph(AT_SIGN_BYTES, 0);
        BufferedImage letterAImage = LOWERCASE_FONT.getImage(1);
        BufferedImage reverseAtSignImage = LOWERCASE_FONT.getImage(128);
        assertTrue(atSignGlyph.diff(letterAImage) < atSignGlyph.diff(reverseAtSignImage));
    }
    
    @Test
    public void differenceIsHighestWithReversedSelf() {
        PetsciiGlyph atSignGlyph = new PetsciiGlyph(AT_SIGN_BYTES, 0);
        BufferedImage reverseAtSignImage = LOWERCASE_FONT.getImage(128);
        assertNotNull("font has image at 128", reverseAtSignImage);
        double maxDiff = atSignGlyph.diff(reverseAtSignImage);
        for(int i=1; i<256; i++) {
            if(i==128) continue;
            assertTrue("char "+i, atSignGlyph.diff(LOWERCASE_FONT.getImage(i)) < maxDiff);
        }
    }
    
    @Test
    public void writesPetsciiLetters() throws IOException {
        PetsciiGlyph atSignGlyph = new PetsciiGlyph(AT_SIGN_BYTES, 0);
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        atSignGlyph.writeData(os);
        byte[] ba = os.toByteArray();
        assertEquals("byte array length", 3, ba.length);
        assertEquals("first character is default WHITE", 5, ba[0]);
        assertEquals("second character is RVS_OFF", -110, ba[1] );
        assertEquals("third character is ASCII @", 64, ba[2]);
    }
    
    @Test
    public void writesPetsciiLettersInColor() throws IOException {
        PetsciiGlyph atSignGlyph = new PetsciiGlyph(AT_SIGN_BYTES, 0, PetsciiColor.CYAN, PetsciiColor.RED);
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        atSignGlyph.writeData(os);
        byte[] ba = os.toByteArray();
        assertEquals("byte array length", 3, ba.length);
        assertEquals("first character is CYAN", -97, ba[0]);
        assertEquals("second character is RVS_OFF", -110, ba[1] );
        assertEquals("third character is ASCII @", 64, ba[2]);
    }
    
}
