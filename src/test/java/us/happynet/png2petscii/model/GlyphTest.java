package us.happynet.png2petscii.model;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author nickb
 */
public class GlyphTest {
    Glyph instance;
    
    private class GlyphImpl extends Glyph {
        
        GlyphImpl(byte[] bytes, int code) {
            super(bytes,code);
        }

        @Override
        void writeData(OutputStream os) throws IOException {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        double diff(BufferedImage tile) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        int[] getRGBArray() {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
        
    }
    
    @Before
    public void setUp() {
        
    }
    
    @Test
    public void bothBitmaps() {
        byte[] bytes = {1,2,4,8,16,32,64,-128};
        instance = new GlyphImpl(bytes,0);
        assertTrue(instance.bitmap(7, 0));
        assertTrue(instance.bitmap(7));
        assertTrue(instance.bitmap(6, 1));
        assertTrue(instance.bitmap(14));
    }
           
    
}
