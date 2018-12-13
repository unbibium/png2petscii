package us.happynet.png2petscii.model;


public class LumaDiffStrategy extends PixelDiffStrategy {

    @Override
    public int applyAsInt(int rgbA, int rgbB) {
        int lumaA = (rgbA >> 16) & 255; 
        lumaA +=  (rgbA >> 8) & 255;
        lumaA +=  (rgbA) & 255;
        int lumaB = (rgbB >> 16) & 255; 
        lumaB +=  (rgbB >> 8) & 255;
        lumaB +=  (rgbB) & 255;
        return Math.abs(lumaA-lumaB);
    }

}
