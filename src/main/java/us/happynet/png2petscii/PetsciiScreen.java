/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package us.happynet.png2petscii;

import java.awt.image.BufferedImage;
import java.awt.image.RasterFormatException;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.PixelFormat;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritablePixelFormat;

/**
 *
 * @author nickb
 */
class PetsciiScreen {
    private final List<List<PetsciiGlyph>> contents;
    private List<PetsciiGlyph> currentLine;
    private final PetsciiFont font;
    
    public final void newline(){
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

    public void drawTo(Canvas c) {
        drawTo(c.getGraphicsContext2D());
    }
    public void drawTo(GraphicsContext ctxt) {
        System.out.println("Drawing PETSCII screen to canvas");
        PetsciiGlyph atSign = font.getGlyph(0);
        int y=0;
        for (List<PetsciiGlyph> row : contents) {
            int x=0;
            for (PetsciiGlyph glyph : row) {
                System.out.printf("%2X ", glyph.getScreenCode());
                //ctxt.drawImage(glyph.getImage(), x, y);
                ctxt.drawImage(atSign.getImage(), x, y);
                x+= 8;
                if(x > ctxt.getCanvas().getWidth()) break;
            }
            System.out.println();
            y += 8;
            if(y > ctxt.getCanvas().getHeight()) break;
        }
        System.out.println("Finished drawing.");
    }

    private static final WritablePixelFormat<IntBuffer> ARGB_FORMAT 
            = WritablePixelFormat.getIntArgbInstance();

    /**
     * Attempts to draw the provided image to the screen in
     * whichever glyphs are closest.
     * <p>
     * this works on a JavaFX image and I don't know if it works.
     * 
     * @param image An arbitrary image to convert
     */    
    public void convert(BufferedImage image) {
        final int width = image.getWidth();
        final int height = image.getHeight();
        for(int row=0; row<height; row+=8) {
            for(int column=0; column<width; column+=8) {
                try {
                    BufferedImage tile = image.getSubimage(column, row, 8, 8);
                    PetsciiGlyph g = font.findClosest(tile);
                    System.out.printf("%2x", g.getScreenCode());
                    type(g);
                } catch (RasterFormatException rfe) {
                    // TODO: figure out what to do with partial tiles
                }
            }
            System.out.println();
            newline();                
        }
    }
    
    /**
     * Attempts to draw the provided image to the screen in
     * whichever glyphs are closest.
     * <p>
     * this works on a JavaFX image and I don't know if it works.
     * 
     * @param image An arbitrary image to convert
     */
    @Deprecated
    public void convert(Image image) {
        // divide incoming image into 8x8 tiles
        final double width = Math.ceil(image.getWidth()/8);
        final double height = Math.ceil(image.getHeight()/8);
        System.out.printf("image seems to be %.0fx%.0f\n", width, height);
        PixelReader pr = image.getPixelReader();
        int[] srcBuffer = new int[64];
        BufferedImage srcBufImage = new BufferedImage(8, 8, BufferedImage.TYPE_INT_ARGB);
        for(int row=0; row<height; row++) {
            System.out.printf("Converting row %d.\n", row);
            for(int column=0; column<width; column++) {
                try {
                    pr.getPixels(column*8, row*8, 8, 8, ARGB_FORMAT, srcBuffer, 0, 8);
                } catch (java.lang.ArrayIndexOutOfBoundsException ex) {
                    System.err.printf("\npixels out-of-bounds at %d,%d\n",column,row);
                    break;
                }
                srcBufImage.setRGB(0, 0, 8, 8, srcBuffer, 0, 8);
                PetsciiGlyph g = font.findClosest(srcBufImage);
                System.out.printf("%2x", g.getScreenCode());
                type(g);
            }
            System.out.println();
            newline();
        }
    }

    /**
     * writes the bytes for each PETSCII character to the output
     * stream, with a CR character after each line.
     * 
     * @param os stream to which to write the PETSCII stream
     * @throws IOException if there is any problem
     */
    void writePetscii(OutputStream os) throws IOException {
        for (List<PetsciiGlyph> row : contents) {
            for (PetsciiGlyph glyph : row) {
                glyph.writePetscii(os);
            }
            // todo: have font output the CR so we can do ATASCII
            os.write(0x0A);
        }
            
    }
    
}
