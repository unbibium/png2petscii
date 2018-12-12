package us.happynet.png2petscii.model;

import java.io.IOException;
import java.io.OutputStream;

/**
 *
 * @author nickb
 */
class AtasciiScreen extends Screen<AtasciiFont> {

    public AtasciiScreen(AtasciiFont aThis) {
        super(aThis, PetsciiColor.BLUE);
    }
    
    @Override
    public void writeNewLine(OutputStream os) throws IOException {
        os.write(155);
    }
  
}
