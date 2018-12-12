package us.happynet.png2petscii.model;

import java.awt.image.BufferedImage;
import java.awt.image.RasterFormatException;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;

/**
 * Abstract class for any screen I might want to write to.
 *
 * @author nickb
 * @param <F> font class
 */
abstract public class Screen<F extends Font> {
    protected final List<List<Glyph>> contents;
    protected List<Glyph> currentLine;
    protected final F font;
    private final PetsciiColor background;

    public final void newline() {
        currentLine = new ArrayList<>();
        contents.add(currentLine);
    }

    public void type(Glyph glyph) {
        currentLine.add(glyph);
    }

    public Screen(F font, PetsciiColor background) {
        this.font = font;
        this.background = background;
        contents = new ArrayList<>();
        newline();
    }

    public PetsciiColor getBackground() {
        return background;
    }
    
    public BufferedImage toBufferedImage() {
        int h = contents.size();
        int w = contents.stream().map(List::size).max(Integer::compare).orElse(0);
        // strip any blank lines at end
        while(h>0 && contents.get(h-1).isEmpty()) {
            h--;
        }
        if (h == 0 || w == 0) {
            throw new IllegalStateException("screen is empty");
        }
        return toBufferedImage(w*8, h*8);
    }

    public BufferedImage toBufferedImage(int w, int h) {
        BufferedImage bi = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        int y = 0;
        for (List<Glyph> row : contents) {
            int x = 0;
            for (Glyph glyph : row) {
                try {
                    bi.setRGB(x, y, 8, 8, glyph.getRGBArray(), 0, 8);
                } catch (ArrayIndexOutOfBoundsException ex) {
                    System.err.printf("oob: %d,%d > %d,%d\n", x, y, w, h);
                }
                x += 8;
                if (x > w) {
                    break;
                }
            }
            y += 8;
            if (y > h) {
                break;
            }
        }
        return bi;
    }
    
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
    
    
    /**
     * writes the bytes for each PETSCII character to the output stream, with
     * a CR character after each line.
     * 
     * @param os stream to which to write the PETSCII stream
     * @throws IOException if there is any problem
     */
    public void writeData(OutputStream os) throws IOException {
        for (List<Glyph> row : contents) {
            for (Glyph glyph : row) {
                glyph.writeData(os);
            }
            writeNewLine(os);
        }
    }
    
    /**
     * Attempts to draw the provided image to the screen in whichever glyphs are
     * closest.
     * <p>
     * this works on a {@link BufferedImage} and I think it works.
     *
     * @param image An arbitrary image to convert
     */
    public void convert(BufferedImage image) {
        final int width = image.getWidth();
        final int height = image.getHeight();
        for (int row = 0; row < height; row += 8) {
            for (int column = 0; column < width; column += 8) {
                try {
                    BufferedImage tile = image.getSubimage(column, row, 8, 8);
                    type(findClosest(tile));
                } catch (RasterFormatException rfe) {
                    // TODO: figure out what to do with partial tiles
                }
            }
            newline();
        }
    }

    public void writeNewLine(OutputStream os) throws IOException {
        os.write(13);
        os.write(10);
    }

    /**
     * Find the closest glyph that can be typed in this screen's
     * font and background color.
     * @param tile
     * @return 
     */
    protected Glyph findClosest(BufferedImage tile) {
        Glyph result = null;
        double score = Double.POSITIVE_INFINITY;
        for(Glyph glyph : getAvailableGlyphs()) {
            double newscore = glyph.diff(tile);
            if (newscore < score) {
                score = newscore;
                result = glyph;
            }
        }
        return result;
    }

    /**
     * TODO: Loop through all available glyphs from font
     * AND colors
     * @return all glyphs that can be typed on this screen
     */
    private Iterable<Glyph> getAvailableGlyphs() {
        return font.getAvailableGlyphs();
    }
    

}
