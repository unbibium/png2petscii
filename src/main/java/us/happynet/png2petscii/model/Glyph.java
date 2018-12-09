/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package us.happynet.png2petscii.model;

import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.IOException;
import java.io.OutputStream;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;

/**
 * Glyph superclass for future refactoring.
 *
 * Represents any glyph that can be compared with a buffered image and then
 * written in some ASCII variant.
 *
 * @author nickb
 */
abstract public class Glyph {

    protected final int screenCode;
    protected final PetsciiColor background;
    protected final PetsciiColor foreground;
    
    protected final boolean[][] bitmap = new boolean[8][8];
    protected final WritableImage image = new WritableImage(8,8);
    protected final int[] rgbArray = new int[64];
    
    public boolean bitmap(int x, int y) {
        return bitmap[x][y];
    }
    
    /**
     * Create a PetsciiGlyph from eight bytes
     * @param data an array of eight bytes
     * @param screenCode 
     * @param foreground 
     * @param background 
     */
    public Glyph(byte[] data, int screenCode, PetsciiColor foreground, PetsciiColor background) {
        this(screenCode,foreground,background);
        if(data.length != 8) {
            throw new IllegalArgumentException("need 8 bytes, was " + data.length);
        }
        final PixelWriter pw = image.getPixelWriter();
        assert pw != null;
        for(int y=0; y<8; y++) {
            for(int x=0; x<8; x++) {
                boolean bit = (data[y] & (1<<x)) != 0;
                bitmap[7-x][y] = bit;
                pw.setArgb(7-x, y, (bit ? foreground:background).getRGB());
                rgbArray[7-x+y*8]=(bit ? foreground:background).getRGB();
            }
        }
    }

    public Glyph(int screenCode, PetsciiColor foreground, PetsciiColor background) {
        this.screenCode = screenCode;
        this.background = background;
        this.foreground = foreground;
    }

    public PetsciiColor getBackgroundColor() {
        return background;
    }

    public PetsciiColor getForegroundColor() {
        return foreground;
    }

    public int getScreenCode() {
        return screenCode;
    }

    public void dump() {
        for (int y = 0; y < 8; y++) {
            for (int x = 0; x < 8; x++) {
                System.out.print(bitmap(x, y) ? "*" : ".");
            }
            System.out.print("\n");
        }
    }

    /**
     * Returns the eight bytes that make up this character.
     * @return 
     */
    public byte[] toBytes() {
        byte[] result = new byte[8];
        for(int y=0; y<8; y++) {
            byte row = 0;
            for (int x=0; x<8; x++) {
                row <<= 1;
                if(bitmap(x,y)) {
                    row |= (byte) 1;
                }
            }
            result[y]=row;
        }
        return result;
    }

    public int[] getRGBArray() {
        return rgbArray;
    }

    /**
     * @param imgB
     * @return difference of luma values between this glyph and imgB.
     */
    public double diff(BufferedImage imgB) {
        int fgBgDifference = 255 * 3;
        final WritableRaster alphaRaster = imgB.getAlphaRaster();
        long difference = 0;
        for (int y = 0; y < 8; y++) {
            for (int x = 0; x < 8; x++) {
                if (alphaRaster != null && alphaRaster.getSample(x, y, 0) > 0) {
                    if (bitmap(x,y)) {
                        difference += fgBgDifference;
                    }
                } else {
                    int rgbB = imgB.getRGB(x, y);
                    int lumaB = (rgbB >> 16) & 255; 
                    lumaB +=  (rgbB >> 8) & 255;
                    lumaB +=  (rgbB) & 255;
                    int lumaA = bitmap(x,y) ? fgBgDifference : 0;
                    difference += Math.abs(lumaA - lumaB);
                }
            }
        }
        // Normalizing the value of different pixels
        // for accuracy(average pixels per color
        // component)
        double avg_different_pixels = difference / 192;
        // There are 255 values of pixels in total
        double percentage = (avg_different_pixels / 255) * 100;
        return percentage;

    }
        
    abstract void writeData(OutputStream os) throws IOException;

}
