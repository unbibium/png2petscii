package us.happynet.png2petscii.io;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import us.happynet.png2petscii.model.Screen;

/**
 *
 * @author nickb
 */
public class S00Writer extends ScreenWriter {
    private final String filename;

    /**
     * Writes S00 header followed by the generated screen.
     *
     * @param os
     * @throws IOException
     */
    @Override
    public void write(OutputStream os) throws IOException {
        PC64Header.writeP00Header(os, filename);
        super.write(os);
    }

    public S00Writer(Screen source, File outputFilename) {
        super(source);
        this.filename = outputFilename.getName();
    }
  
}
