/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package us.happynet.png2petscii.model;

import us.happynet.png2petscii.model.PetsciiFont;
import us.happynet.png2petscii.model.PetsciiGlyph;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Assume;

/**
 *
 * @author nickb
 */
public class PetsciiFontTest {

    public PetsciiFontTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }

    static final ClassLoader CL = PetsciiFont.class.getClassLoader();
    static final PetsciiFont FONT = getFont();

    static final PetsciiFont getFont() {
        try {    
            return new PetsciiFont(CL.getResourceAsStream("charset_lower.png"));
        } catch (IOException ex) {
            Logger.getLogger(PetsciiFontTest.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }
    
    @Before
    public void setUp() throws IOException {
        if(FONT == null) {
            Assume.assumeTrue("couldn't create font object", false);
        }
    }
    
    @After
    public void tearDown() {
    }
    
    @Test    
    public void findClosestFindsSpace() {
        int[] blankSpace = new int[64];
        Arrays.fill(blankSpace, 0);
        BufferedImage image = new BufferedImage(8, 8, BufferedImage.TYPE_INT_ARGB);
        image.setRGB(0, 0, 8, 8, blankSpace, 0, 8);
        PetsciiGlyph glyph = FONT.findClosest(image);
        assertEquals("screen code is space", 32, glyph.getScreenCode());
    }
    
    @Test    
    public void findClosestFindsAtSign() {
        final int w = 0xFFFFFF;
        int[] atSign = {
            0,0,w,w,w,w,0,0,
            0,w,w,0,0,w,w,0,
            0,w,w,0,w,w,w,0,
            0,w,w,0,w,w,w,0,
            0,w,w,0,0,0,0,0,
            0,w,w,0,0,0,w,0,
            0,0,w,w,w,w,0,0,
            0,0,0,0,0,0,0,0
        };
        BufferedImage image = new BufferedImage(8, 8, BufferedImage.TYPE_INT_ARGB);
        image.setRGB(0, 0, 8, 8, atSign, 0, 8);
        PetsciiGlyph glyph = FONT.findClosest(image);
        assertEquals("screen code is at-sign", 0, glyph.getScreenCode());
    }

}
