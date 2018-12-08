package us.happynet.png2petscii.io;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;

/**
 *
 * @author nickb
 */
public class PC64Header {

    static byte[] C64FILE_HEADER = {
        'C', '6', '4', 'F', 'i', 'l', 'e', 0
    };

    /**
     * Writes a header for a PC64-style P00 or S00 file.
     *
     * @param os output stream
     * @param filename ASCII filename to insert into the header
     * @throws IOException
     */
    public static void writeP00Header(OutputStream os, String filename) throws IOException {
        byte[] petsciiFilename = toHeaderFilename(filename);
        os.write(C64FILE_HEADER);
        os.write(petsciiFilename);
        for (int i = petsciiFilename.length; i < 18; i++) {
            os.write(0);
        }
    }

    /**
     * Converts a filename to a format suitable for a P00 file header. It strips
     * the file extension and truncates the filename to 16 characters, and
     * converts any unknown characters to left-arrow characters.
     *
     * @param name source filename in an ASCII string
     * @return a 16-byte array containing the filename, padded with 0x00.
     */
    public static byte[] toHeaderFilename(String name) {
        String bareName = name.toUpperCase();
        int dotPosition = name.lastIndexOf('.');
        if(dotPosition > -1) {
            bareName = bareName.substring(0, dotPosition);
        }
        byte[] result = new byte[16];
        Arrays.fill(result, (byte) 0);
        for (int i = 0; i < 16 && i < bareName.length(); i++) {
            char c = bareName.charAt(i);
            if (c >= ' ' && c < '_') {
                result[i] = (byte) c;
            } else {
                result[i] = '_';
            }
        }
        return result;
    }

}
