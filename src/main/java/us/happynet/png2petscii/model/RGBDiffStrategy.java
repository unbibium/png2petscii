package us.happynet.png2petscii.model;

/**
 *
 * @author nickb
 */
public class RGBDiffStrategy extends PixelDiffStrategy {

    @Override
    public int applyAsInt(int left, int right) {
        int redA = (left >> 16) & 255;
        int greenA = (left >> 8) & 255;
        int blueA = (left) & 255;
        int redB = (right >> 16) & 255;
        int greenB = (right >> 8) & 255;
        int blueB = (right) & 255;
        return Math.abs(redA-redB) +
                Math.abs(greenA-greenB) +
                Math.abs(blueA-blueB);
    }
    
}
