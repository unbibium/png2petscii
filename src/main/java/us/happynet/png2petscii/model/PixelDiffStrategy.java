package us.happynet.png2petscii.model;

import java.util.function.BiFunction;
import java.util.function.IntBinaryOperator;

/**
 * Strategy pattern for determining difference between two pixels.
 * <p>
 * <ul>
 * <li> {@link #LUMA_DIFF} compares the sums of all color registers
 * <li> {@link #RGB_DIFF} compares each color register, and returns the sum
 * </ul>
 * 
 * @author nickb
 */
public enum PixelDiffStrategy implements IntBinaryOperator, BiFunction<Integer,Integer,Integer> {
    
    RGB_DIFF ((left,right) -> {
        int redA = (left >> 16) & 255;
        int greenA = (left >> 8) & 255;
        int blueA = (left) & 255;
        int redB = (right >> 16) & 255;
        int greenB = (right >> 8) & 255;
        int blueB = (right) & 255;
        return Math.abs(redA-redB) +
                Math.abs(greenA-greenB) +
                Math.abs(blueA-blueB);
    } ),
    LUMA_DIFF ((rgbA,rgbB) -> {
        int lumaA = (rgbA >> 16) & 255; 
        lumaA +=  (rgbA >> 8) & 255;
        lumaA +=  (rgbA) & 255;
        int lumaB = (rgbB >> 16) & 255; 
        lumaB +=  (rgbB >> 8) & 255;
        lumaB +=  (rgbB) & 255;
        return Math.abs(lumaA-lumaB);
    } );
    
    private final IntBinaryOperator func;
    
    PixelDiffStrategy(IntBinaryOperator func) {
        this.func = func;
    }
    
    @Override
    public final Integer apply(Integer left, Integer right) {
        return applyAsInt(left,right);
    }

    @Override
    public int applyAsInt(int left, int right) {
        return func.applyAsInt(left, right);
    }

}
