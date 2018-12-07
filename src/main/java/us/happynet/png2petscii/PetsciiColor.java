package us.happynet.png2petscii;

/**
 * An enumeration of color codes supported by the VIC-II chip.
 * 
 * @author nickb
 */
public enum PetsciiColor {

    BLACK       (0x000000, 144),
    WHITE       (0xFFFFFF, 5),
    RED         (0xFF0000, 28 ),
    CYAN        (0x00FFFF, 159),
    PURPLE      (0xFF00FF, 156),
    GREEN       (0x00FF00, 30 ),
    BLUE        (0x0000FF, 31 ),
    YELLOW      (0xFFFF00, 158 ),
    ORANGE      (0xFF8000, 129 ),
    BROWN       (0x804000, 149 ),
    LIGHT_RED   (0xFF8080, 150 ),
    DARK_GRAY   (0x404040, 151 ),
    MID_GRAY    (0x808080, 152 ),
    LIGHT_GREEN (0x80FF80, 153 ),
    LIGHT_BLUE  (0x8080FF, 154 ),
    LIGHT_GRAY  (0xC0C0C0, 155 );

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
     */
    public int diff(int otherRGB) {
        int redB = (otherRGB >> 16) & 255;
        int greenB = (otherRGB >> 8) & 255;
        int blueB = (otherRGB) & 255;
        return Math.abs(this.getRed()-redB) +
                Math.abs(this.getGreen()-greenB) +
                Math.abs(this.getBlue()-blueB);
    }

}
