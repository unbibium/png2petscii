package us.happynet.png2petscii;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Abstract class for any screen I might want to write to.
 * <p>
 * this is mostly here so that I can make mock objects for tests
 * a little easier.
 *
 * @author nickb
 */
abstract public class Screen {
    
    abstract public void convert(BufferedImage image);
    abstract public void writeData(OutputStream os) throws IOException;
    
}
