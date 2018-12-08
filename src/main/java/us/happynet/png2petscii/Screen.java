package us.happynet.png2petscii;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;

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
    
    /**
     * Attempts to draw the provided image to the screen in
     * whichever glyphs are closest.
     * <p>
     * this converts it to a BufferedImage and runs the
     * other method.
     * 
     * @param image An arbitrary image to convert
     */
    public void convert(Image image) {
        convert(SwingFXUtils.fromFXImage(image, null));
    }

}
