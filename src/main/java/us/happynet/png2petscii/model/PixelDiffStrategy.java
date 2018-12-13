package us.happynet.png2petscii.model;

import java.util.function.BiFunction;
import java.util.function.IntBinaryOperator;

/**
 *
 * @author nickb
 */
abstract public class PixelDiffStrategy implements IntBinaryOperator, BiFunction<Integer,Integer,Integer> {
    @Override
    public final Integer apply(Integer t, Integer u) {
        return applyAsInt(t,u);
    }

}
