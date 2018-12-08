package us.happynet.png2petscii.io;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import us.happynet.png2petscii.Screen;

/**
 *
 * @author nickb
 * @param <T> Concrete screen class
 */
abstract public class ScreenWriter<T extends Screen> {

    protected final T source;

    protected ScreenWriter(T source) {
        if (source == null) {
            throw new NullPointerException("constructed with null screen");
        }
        this.source = source;
    }

    abstract public void write(OutputStream os) throws IOException;

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
