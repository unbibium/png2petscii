package us.happynet.png2petscii.model;

/**
 * An enumeration of color codes supported by the VIC-II chip.
 * <p>
 * Color palette taken from 
 * {@linkplain http://unusedino.de/ec64/technical/misc/vic656x/colors/}
 * 
 * @author nickb
 */
public enum PetsciiColor {

    BLACK       (0x000000, 144),
    WHITE       (0xFFFFFF, 5),
    RED         (0x6B372B, 28 ),
    CYAN        (0x70A4B2, 159),
    PURPLE      (0x6F3D86, 156),
    GREEN       (0x588D43, 30 ),
    BLUE        (0x352879, 31 ),
    YELLOW      (0xB8C76F, 158 ),
    ORANGE      (0x6F4F25, 129 ),
    BROWN       (0x433900, 149 ),
    LIGHT_RED   (0x9A6759, 150 ),
    DARK_GRAY   (0x444444, 151 ),
    MID_GRAY    (0x6C6C6C, 152 ),
    LIGHT_GREEN (0x9AD284, 153 ),
    LIGHT_BLUE  (0x6C5EB5, 154 ),
    LIGHT_GRAY  (0x959595, 155 );

    private final int color;
    private final byte code;
    
    PetsciiColor(int RGB, int c) {
        this.color = RGB;
        this.code = (byte) (c & 0xFF);
    }
    
    /**
     * @return the 24-bit RGB code for this color.
     */
    public int getRGB() {
        return this.color;
    }    
    
    /**
     * @return the 8-bit Blue level for this color. 
     */
    public int getBlue() {
        return color & 0xFF;
    }
    
    /**
     * @return the 8-bit Green level for this color. 
     */
    public int getGreen() {
        return (color >> 8) & 0xFF;
    }
    
    /**
     * @return the 8-bit Red level for this color. 
     */
    public int getRed() {
        return (color >> 16) & 0xFF;
    }
    
    /**
     * @return the PETSCII character code to switch to this color.
     */
    public byte getPetscii() {
        return this.code;
    }
    
    /**
     * @return the screen code to poke to memory to get this color.
     */
    public byte getColorCode() {
        return (byte) this.ordinal();
    }
    
    /**
     * 
     * @param other
     * @return the difference between the color level of the two colors.
     */
    public int diff(PetsciiColor other) {
        return Math.abs(this.getRed()-other.getRed()) +
                Math.abs(this.getGreen()-other.getGreen()) +
                Math.abs(this.getBlue()-other.getBlue());
    }
    /**
     * 
     * @param otherRGB a 24-bit RGB color for comparison
     * @return the difference between the color level of the two colors.
     * 0 for identical colors.
     */
    public int diff(int otherRGB) {
        int redB = (otherRGB >> 16) & 255;
        int greenB = (otherRGB >> 8) & 255;
        int blueB = (otherRGB) & 255;
        return Math.abs(this.getRed()-redB) +
                Math.abs(this.getGreen()-greenB) +
                Math.abs(this.getBlue()-blueB);
    }
    
    /**
     * Quantizes a pixel to the closest known C64 color.
     * @param sourceRGB a 24-bit RGB color
     * @return RGB value of the closest C64 palette entry
     */
    public static int quantize(int sourceRGB) {
        int minDiff = 999;
        int minColor = 0;
        for (PetsciiColor c : PetsciiColor.values()) {
            if(c.getRGB() == sourceRGB) {
                return sourceRGB;
            }
            int cDiff = c.diff(sourceRGB);
            if(minDiff > cDiff) {
                minDiff = cDiff;
                minColor = c.getRGB();
            }
        }
        return minColor;
    }

}
