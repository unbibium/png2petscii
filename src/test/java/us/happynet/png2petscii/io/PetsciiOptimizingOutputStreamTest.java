package us.happynet.png2petscii.io;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import static org.junit.Assert.assertArrayEquals;
import org.junit.Before;
import org.junit.Test;
import us.happynet.png2petscii.PetsciiColor;

/**
 * Filters out redundant color and reverse codes from PETSCII output.
 * 
 * @author nickb
 */
public class PetsciiOptimizingOutputStreamTest {
    private OutputStream instance;
    private ByteArrayOutputStream target;
    
    private static final byte RVS_ON = 0x12;
    private static final byte RVS_OFF = RVS_ON | -0x80;
    private static final byte CR = 0x0D;
    private static final byte SHIFT_CR = CR | -0x80;
    
    @Before
    public void setUp() {
        target = new ByteArrayOutputStream();
        instance = new PetsciiOptimizingOutputStream(target);
    }
    
    @Test
    public void letsPrintableCharactersPass() throws IOException {
        byte[] printableCharacters = new byte[192];
        for(int i=0; i<32; i++) {
            printableCharacters[i] = (byte) (32+i);
            printableCharacters[i+32] = (byte) (64+i);
            printableCharacters[i+64] = (byte) (96+i);
            printableCharacters[i+96] = (byte) (160+i);
            printableCharacters[i+128] = (byte) (192+i);
            printableCharacters[i+160] = (byte) (224+i);
        }
        
        instance.write(printableCharacters);
        byte[] actual = target.toByteArray();
        assertArrayEquals("all printable characters", printableCharacters, actual);
    }
    
    @Test
    public void letsReverseCharactersPassOnce() throws IOException {
        byte[] stringThatTogglesReverse = {
            'A', RVS_ON, 'B', RVS_OFF, 'C'
        };
        instance.write(stringThatTogglesReverse);
        byte[] actual = target.toByteArray();
        assertArrayEquals("string that toggles reverse", stringThatTogglesReverse, actual);
    }
    
    @Test
    public void filtersReverseCharactersTheSecondTime() throws IOException {
        byte[] stringThatTogglesReverse = { 
            'D', 
            RVS_ON, 'E', RVS_ON, 'F', 
            RVS_OFF, 'G', RVS_OFF, 'H' 
        };
        byte[] expected = {'D', RVS_ON, 'E', 'F', RVS_OFF, 'G', 'H'};
        instance.write(stringThatTogglesReverse);
        byte[] actual = target.toByteArray();
        assertArrayEquals("string with doubled reverse characters", expected, actual);
    }
    
    @Test
    public void filtersReverseOffInitially() throws IOException {
        byte[] stringThatTogglesReverse = { 
            'I', RVS_OFF, 'J'
        };
        byte[] expected = {'I', 'J'};
        instance.write(stringThatTogglesReverse);
        byte[] actual = target.toByteArray();
        assertArrayEquals("string with just a reverse-off", expected, actual);
    }
    
    @Test
    public void filtersReverseOffAfterCR() throws IOException {
        byte[] turnsReverseOffWithCr = { 
            'K', RVS_ON, 'L', CR,
            'M', RVS_OFF, 'N', RVS_ON, 'O', SHIFT_CR,
            'P', RVS_OFF, 'Q'
        };
        byte[] expected = { 
            'K', RVS_ON, 'L', CR,
            'M', 'N', RVS_ON, 'O', SHIFT_CR,
            'P', 'Q'
        };
        instance.write(turnsReverseOffWithCr);
        byte[] actual = target.toByteArray();
        assertArrayEquals("string where a CR turns reverse off", expected, actual);
    }

    @Test
    public void filtersDuplicateColors() throws IOException {
        ByteArrayOutputStream expectedOutputStream = new ByteArrayOutputStream();
        // fill input array with {'0', BLK, '0', BLK, '0'}
        // fill expected array with {'0', BLK, '0', '0' }
        for(PetsciiColor color : PetsciiColor.values()) {
            byte c = (byte) Integer.toHexString(color.getColorCode()).charAt(0);
            byte[] input = {c, color.getPetscii(), c, color.getPetscii(), c };
            byte[] expectedOutput = {c, color.getPetscii(), c, c };
            instance.write(input);
            expectedOutputStream.write(expectedOutput);
        }

        byte[] expected = expectedOutputStream.toByteArray();
        byte[] actual = target.toByteArray();
        assertArrayEquals("String where each color is used twice in a row", 
                expected, actual);
    }
    
    
}
