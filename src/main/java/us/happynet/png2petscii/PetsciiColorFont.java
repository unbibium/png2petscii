/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package us.happynet.png2petscii;

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

    private final List<PetsciiGlyph> allColorGlyphs = new ArrayList<>();

    public PetsciiColorFont(PetsciiFont srcFont, PetsciiColor background) {
        super(srcFont);
        this.background = background;
        for (PetsciiColor c : PetsciiColor.values()) {
            if (c == background) {
                continue;
            }
            for(PetsciiGlyph g : glyphs) {
                if(g.getScreenCode()==96) {
                    continue;
                }
                allColorGlyphs.add( new PetsciiColorGlyph(g, c, background));
            }
        }
    }
    
    public PetsciiColorFont(PetsciiFont srcFont) {
        this(srcFont, PetsciiColor.BLACK);
    }
    
    public PetsciiColorFont(File f) throws IOException {
        this(new PetsciiFont(f), PetsciiColor.BLACK);
    }

    @Override
    public Iterable<PetsciiGlyph> getAvailableGlyphs() {
        return allColorGlyphs;
    }

}
