/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package us.happynet.png2petscii.model;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author nickb
 */
public class PetsciiColorFont extends PetsciiFont {

    private final PetsciiColor background;

    private final List<RenderedGlyph> allColorGlyphs = new ArrayList<>();

    public PetsciiColorFont(PetsciiFont srcFont, PetsciiColor[] colors, PetsciiColor background) {
        super(srcFont);
        this.background = background;
        for (PetsciiColor foreground : colors) {
            if (foreground == background) {
                continue;
            }
            for(RenderedGlyph g : glyphs) {
                // skip shift-space
                if(g.getScreenCode()==96 || g.getScreenCode()==224) {
                    continue;
                }
                allColorGlyphs.add( new PetsciiGlyph(g, foreground, background));
            }
        }
    }
    
    /**
     * Generates a font with only one foreground color
     * 
     * @param srcFont
     * @param foreground the lone foreground color to render the glyphs
     * @param background the background color of the screen being written to
     */
    public PetsciiColorFont(PetsciiFont srcFont, PetsciiColor foreground, PetsciiColor background) {
        this(srcFont, asArray(foreground), background);
    }
    
    private static PetsciiColor[] asArray(PetsciiColor color) {
        PetsciiColor[] array = new PetsciiColor[1];
        array[0]=color;
        return array;
    }
    
    public PetsciiColorFont(PetsciiFont srcFont, PetsciiColor background) {
        this(srcFont,PetsciiColor.values(),background);
    }
    
    public PetsciiColorFont(PetsciiFont srcFont) {
        this(srcFont, PetsciiColor.BLACK);
    }
    
    public PetsciiColorFont(File f) throws IOException {
        this(new PetsciiFont(f), PetsciiColor.BLACK);
    }

    @Override
    public Iterable<RenderedGlyph> getAvailableGlyphs() {
        return allColorGlyphs;
    }

}
