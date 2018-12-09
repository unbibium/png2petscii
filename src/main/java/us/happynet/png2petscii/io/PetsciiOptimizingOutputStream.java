/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package us.happynet.png2petscii.io;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import us.happynet.png2petscii.model.PetsciiColor;
import static us.happynet.png2petscii.model.PetsciiGlyph.CRSR_RIGHT;
import static us.happynet.png2petscii.model.PetsciiGlyph.RVS_OFF;
import static us.happynet.png2petscii.model.PetsciiGlyph.RVS_ON;

/**
 * Filters redundant control codes from a stream of PETSCII output.
 *
 * @author nickb
 */
public class PetsciiOptimizingOutputStream extends FilterOutputStream {

    private boolean reverse = false;
    private static final int CR = 0x0D;
    
    // initial state matches no color code
    private int colorState = 0;
    
    private static final int[] COLOR_CODES = Arrays.stream(PetsciiColor.values())
            .mapToInt(PetsciiColor::getPetscii)
            .toArray();

    public PetsciiOptimizingOutputStream(OutputStream out) {
        super(out);
    }

    @Override
    public void write(int b) throws IOException {
        // Turn unnecessary CRSR_RIGHTs back into SPACE
        // so that ASCII users on BBSes can see them.
        if(b == CRSR_RIGHT && !reverse) {
            out.write(0x20);
            return;
        }
        if(b == RVS_ON) {
            if (reverse) return;
            reverse=true;
        } else if(b == RVS_OFF) {
            if (!reverse) return;
            reverse=false;
        } else if ((b & 0x7f)==CR) {
            reverse=false;
        } else if (Arrays.stream(COLOR_CODES).anyMatch((a) -> a==b)) {
            if(colorState == b) return;
            colorState = b;
        }
        out.write(b);
    }

    
}
