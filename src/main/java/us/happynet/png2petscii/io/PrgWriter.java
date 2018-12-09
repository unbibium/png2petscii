package us.happynet.png2petscii.io;

import java.io.IOException;
import java.io.OutputStream;
import us.happynet.png2petscii.model.Screen;

/**
 * Writer for raw PRG files.
 * <p>
 * The generated program will be a standard SYS2064-style
 * BASIC program.  The machine language part of the program
 * will first set the appropriate background color, and then
 * print out every character generated by the 
 * {@link PetsciiScreen#writeData(java.io.OutputStream) 
 * method, and turn off quote mode so that it behaves like a BBS
 * terminal program would.  It will then return to BASIC.
 *
 * @author nickb
 */
public class PrgWriter extends ScreenWriter {

    public PrgWriter(Screen source) {
        super(source);
    }

    private static final byte[] PRG_BASIC = {
        1, 8, // Start address for PRG file
        11, 8, // end address for BASIC memory
        -30, 7, // line number 2018
        -98, // SYS
        50, 48, 54, 52, // 2064
        0, 0, 0, 0, 0, 0 // filler
    };

//>C:0810  a9 00 8d 21  d0 ad 32 08  f0 0d 20 d2  ff ee 16 08   ...!..2... .....
//>C:0820  d0 f3 ee 17  08 d0 ee a9  32 8d 16 08  a9 08 8d 17   ........2.......
//>C:0830  08 60 41 42  43 44 45 00  00 00 00 00  00 00 00 00   .`ABCDE.........

    /*
    The machine language part of the PRG file is separated in two bits.
    write the first bit, then the background color, then the rest,
    then the character data, then a zero.
     */
    private static final byte[] PRG_0810 = {
        -87 // LDA #$
    };

    private static final byte[] PRG_0812 = {
        -115, 33, -48, // STA $D021  ; background color
        // beginning of loop
        -87, 0,     // LDA #0
        -123,-44,   // STA 212  ; turn off quote mode
        -83, 54, 8, // LDA $0836  ; next character (contains pointer)
        -16, 13, // BEQ *+13   ; routine ends when byte is 0
        32, -46, -1, // JSR $FFD2  ; print to screen
        -18, 26, 8,
        -48,-17,
        -18, 27, 8,
        -48,-22,-87, 54,-115, 26, 8,-87, 8,-115, 27, 8,
        96 // RTS to return to basic
    };

    /**
     * Writes the bytes of a PRG file that will display the attached screen.
     *
     * @param os
     * @throws IOException
     */
    @Override
    public void write(OutputStream os) throws IOException {
        os.write(PRG_BASIC);
        os.write(PRG_0810);
        os.write(0); // TODO: get background color
        os.write(PRG_0812);
        super.write(os);
        os.write(0); // terminator byte so ML knows when to stop.
    }

}
