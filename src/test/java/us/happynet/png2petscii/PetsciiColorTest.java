package us.happynet.png2petscii;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
import static us.happynet.png2petscii.PetsciiColor.*;

/**
 * This performs some sanity checks to make sure that I don't
 * accidentally mess up the color decoding or definitions.
 *
 * @author nickb
 */
public class PetsciiColorTest {

    @Test
    public void redHasMoreRedThanGreen() {
        assertTrue(RED.getRed() > RED.getGreen());
        assertTrue(LIGHT_RED.getRed() > LIGHT_RED.getGreen());
    }
    
    @Test
    public void redHasMoreRedThanBlue() {
        assertTrue(RED.getRed() > RED.getBlue());
        assertTrue(LIGHT_RED.getRed() > LIGHT_RED.getBlue());
    }
    
    @Test
    public void greenHasMoreGreenThanRed() {
        assertTrue(GREEN.getGreen() > GREEN.getRed());
        assertTrue(LIGHT_GREEN.getGreen() > LIGHT_GREEN.getRed());
    }
    
    @Test
    public void greenHasMoreGreenThanBlue() {
        assertTrue(GREEN.getGreen() > GREEN.getBlue());
        assertTrue(LIGHT_GREEN.getGreen() > LIGHT_GREEN.getBlue());
    }
    
    @Test
    public void blueHasMoreBlueThanRed() {
        assertTrue(BLUE.getBlue() > BLUE.getRed());
        assertTrue(LIGHT_BLUE.getBlue() > LIGHT_BLUE.getRed());
    }
    
    @Test
    public void blueHasMoreBlueThanGreen() {
        assertTrue(BLUE.getBlue() > BLUE.getGreen());
        assertTrue(LIGHT_BLUE.getBlue() > LIGHT_BLUE.getGreen());
    }
    
    @Test
    public void graysAreInCorrectOrder() {
        assertTrue(WHITE.getBlue() > LIGHT_GRAY.getBlue());
        assertTrue(LIGHT_GRAY.getBlue() > MID_GRAY.getBlue());
        assertTrue(MID_GRAY.getBlue() > DARK_GRAY.getBlue());
        assertTrue(DARK_GRAY.getBlue() > BLACK.getBlue());
    }
    
    @Test
    public void diffItselfEqualsZero() {
        for (PetsciiColor color: PetsciiColor.values()) {
            assertEquals(color.name(), 0, color.diff(color));
        }
    }
    
    @Test
    public void blackDiffZeroEqualsZero() {
        assertEquals(0, BLACK.diff(0x000000));
    }
    
    @Test
    public void whiteDiffFFFFFFEqualsZero() {
        assertEquals(0, WHITE.diff(0xFFFFFF));
    }
    
    @Test
    public void blackDiffFFFFFFIsGreaterThanZero() {
        assertTrue(BLACK.diff(0xFFFFFF) > 0);
    }
    
    @Test
    public void whiteDiffZeroIsGreaterThanZero() {
        assertTrue(WHITE.diff(0x000000) > 0);
    }
    
    @Test
    public void diffOtherColorIsGreaterThanZero() {
        for(PetsciiColor colorA: PetsciiColor.values()) {
            for(PetsciiColor colorB: PetsciiColor.values()) {
                if(colorA == colorB) continue;
                assertTrue( colorA.name()+"/"+colorB.name(), 
                        colorA.diff(colorB) > 0);
            }
        }
    }
}
