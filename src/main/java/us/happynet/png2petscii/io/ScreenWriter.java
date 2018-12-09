package us.happynet.png2petscii.io;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import us.happynet.png2petscii.model.Screen;

/**
 * Writes the contents of a screen to a file or output screen.
 * <p>
 * This class writes a raw text file with no other embellishments.
 * Other classes may output program files or other file formats.
 * 
 * @author nickb
 */
public class ScreenWriter {

    protected final Screen source;

    public ScreenWriter(Screen source) {
        if (source == null) {
            throw new NullPointerException("constructed with null screen");
        }
        this.source = source;
    }


    public void write(OutputStream os) throws IOException {
        source.writeData(os);
    }

    /**
     * Writes the attached screen's data to the specified output file.
     *
     * @param file the filename to use
     * @throws IOException if anything at all goes wrong
     */
    public void write(File file) throws IOException {
        try (OutputStream fos = new FileOutputStream(file);
                OutputStream os = new BufferedOutputStream(fos)) {
            write(os);
        }
    }

}
