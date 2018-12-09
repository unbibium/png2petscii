package us.happynet.png2petscii.model;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;

/**
 *
 * @author nickb
 */
public class AtasciiGlyph extends Glyph {

    AtasciiGlyph(byte[] glyphBitmap, int i) {
        super(glyphBitmap, i, PetsciiColor.LIGHT_BLUE, PetsciiColor.BLUE);
    }

    @Override
    void writeData(OutputStream os) throws IOException {
        switch(screenCode & 0x60) {
            case 0x00:   // punctuation
                os.write(screenCode | 0x20);
                break;
            case 0x20:   // capital letters
                os.write(screenCode ^ 0x60);
                break;
            case 0x40:   // control graphics
                if((screenCode & 0x1F) > 0x1A) {
                    os.write(0x1B); // ESC
                }
                os.write(screenCode ^ 0x40);
                break;
            case 0x60:   // lowercase letters
                if((screenCode & 0x1F) > 0x1C) {
                    os.write(0x1B); // ESC
                }
                os.write(screenCode);
                break;
        }
    }
    
}
