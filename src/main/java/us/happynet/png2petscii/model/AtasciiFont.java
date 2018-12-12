/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package us.happynet.png2petscii.model;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author nickb
 */
public class AtasciiFont extends Font<AtasciiGlyph> {
    protected final List<AtasciiGlyph> glyphs = new ArrayList<>(256);
    
    /**
     * @param binFile a 1024-byte font bitmap
     * @throws java.io.IOException
     */
    public AtasciiFont(File binFile) throws IOException {
        this(read1K(binFile));
    }
        
    public AtasciiFont(InputStream is) throws IOException {
        this(read1K(is));
    }
    
    public AtasciiFont(byte[] bitmap) {
        if(bitmap.length != 1024) {
            throw new IllegalArgumentException("bitmap is wrong size");
        }
        for(int i=0; i<128; i++) {
            byte[] glyphBitmap = Arrays.copyOfRange(bitmap, i*8, i*8+8);
            glyphs.add(new AtasciiGlyph(glyphBitmap, i));
        }
        for(int i=0; i<128; i++) {
            byte[] glyphBitmap = Arrays.copyOfRange(bitmap, i*8, i*8+8);
            byte[] inverseBitmap = new byte[8];
            for(int y=0; y<8; y++) {
                inverseBitmap[y]=(byte) (0xFF ^ glyphBitmap[y]);
            }
            glyphs.add(new AtasciiGlyph(inverseBitmap, i+128));
        }
    }
    
    private static byte[] read1K(File binFile) throws IOException {
        try(FileInputStream fis = new FileInputStream(binFile);
            BufferedInputStream bis = new BufferedInputStream(fis)) {
            return read1K(bis);
        }
    }
    private static byte[] read1K(InputStream is) throws IOException {
        byte[] result = new byte[1024];
        int readResult = is.read(result);
        System.out.printf("read %d bytes for atascii\n", readResult);
        return result;
    }
    
    @Override
    public Iterable<AtasciiGlyph> getAvailableGlyphs() {
        return glyphs;
    }

    @Override
    public Screen<? extends Font<AtasciiGlyph>, AtasciiGlyph> newScreen() {
        return new AtasciiScreen(this);
    }

    private static final byte[] NEWLINE = { -101 };

    @Override
    public byte[] getNewline() {
        return NEWLINE;
    }

    
}
