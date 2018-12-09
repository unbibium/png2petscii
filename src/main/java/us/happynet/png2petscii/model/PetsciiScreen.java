/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package us.happynet.png2petscii.model;

import java.awt.image.BufferedImage;
import java.awt.image.RasterFormatException;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import us.happynet.png2petscii.io.PetsciiOptimizingOutputStream;

/**
 *
 * @author nickb
 */
public class PetsciiScreen extends Screen {

    private final List<List<PetsciiGlyph>> contents;
    private List<PetsciiGlyph> currentLine;
    private final PetsciiFont font;

    public PetsciiScreen() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public final void newline() {
        currentLine = new ArrayList<>();
        contents.add(currentLine);
    }

    public void type(PetsciiGlyph glyph) {
        currentLine.add(glyph);
    }

    public PetsciiScreen(PetsciiFont font) {
        this.font = font;
        contents = new ArrayList<>();
        newline();
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
        for (List<PetsciiGlyph> row : contents) {
            int x = 0;
            for (PetsciiGlyph glyph : row) {
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
        System.out.println("Generated a buffered image.");
        return bi;
    }

    /**
     * Attempts to draw the provided image to the screen in whichever glyphs are
     * closest.
     * <p>
     * this works on a {@link BufferedImage} and I think it works.
     *
     * @param image An arbitrary image to convert
     */
    @Override
    public void convert(BufferedImage image) {
        final int width = image.getWidth();
        final int height = image.getHeight();
        for (int row = 0; row < height; row += 8) {
            for (int column = 0; column < width; column += 8) {
                try {
                    BufferedImage tile = image.getSubimage(column, row, 8, 8);
                    PetsciiGlyph g = font.findClosest(tile);
                    type(g);
                } catch (RasterFormatException rfe) {
                    // TODO: figure out what to do with partial tiles
                }
            }
            newline();
        }
    }

    /**
     * writes the bytes for each PETSCII character to the output stream, with
     * a CR character after each line.
     * 
     * @param os stream to which to write the PETSCII stream
     * @throws IOException if there is any problem
     */
    public void writeData(PetsciiOptimizingOutputStream os) throws IOException {
        for (List<PetsciiGlyph> row : contents) {
            for (PetsciiGlyph glyph : row) {
                glyph.writeData(os);
            }
            // todo: have font output the CR so we can move this
            // to the superclass instead and do ATASCII.
            os.write(0x0D);
        }
    }

    /**
     * writes the bytes for each PETSCII character to the output stream, with a
     * CR character after each line.
     * 
     * All outgoing data will be filtered through 
     * {@link PetsciiOptimizingOutputStream}
     * before being written to the output stream.
     *
     * @param os stream to which to write the PETSCII stream
     * @throws IOException if there is any problem
     */
    @Override
    public void writeData(OutputStream os) throws IOException {
        // prevent recursion
        assert !(os instanceof PetsciiOptimizingOutputStream);
        // cache everything in a byte array.  we can't just wrap
        // the existing one because that would close it at the end,
        // and the caller won't be able to add trailing info.
        try(ByteArrayOutputStream baos = new ByteArrayOutputStream();
                PetsciiOptimizingOutputStream poos = new PetsciiOptimizingOutputStream(baos)) {
            writeData((PetsciiOptimizingOutputStream) poos);
            os.write(baos.toByteArray());
        }
    }

}
