/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package us.happynet.png2petscii.model;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import us.happynet.png2petscii.io.PetsciiOptimizingOutputStream;

/**
 *
 * @author nickb
 */
public class PetsciiScreen extends Screen<PetsciiFont> {

    PetsciiScreen(PetsciiFont font) {
        super(font, PetsciiColor.BLACK);
    }
    
    PetsciiScreen(PetsciiFont font, PetsciiColor background) {
        super(font, background);
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
            super.writeData(poos);
            os.write(baos.toByteArray());
        }
    }
    
    @Override
    public void writeNewLine(OutputStream os) throws IOException {
        os.write(13);
    }

}
