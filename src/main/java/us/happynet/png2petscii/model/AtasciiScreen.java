package us.happynet.png2petscii.model;

import java.io.IOException;
import java.io.OutputStream;

/**
 *
 * @author nickb
 */
class AtasciiScreen extends Screen<AtasciiFont,AtasciiGlyph> {

    public AtasciiScreen(AtasciiFont aThis) {
        super(aThis);
    }
    
    @Override
    public void writeNewLine(OutputStream os) throws IOException {
        os.write(155);
    }
  
}
