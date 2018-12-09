package us.happynet.png2petscii.io;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import us.happynet.png2petscii.PetsciiScreen;

/**
 *
 * @author nickb
 */
public class SeqWriter extends ScreenWriter<PetsciiScreen> {
    
    public SeqWriter(PetsciiScreen s) {
        super(s);
    }

    @Override
    public void write(OutputStream os) throws IOException {
        source.writeData(os);
    }
}
