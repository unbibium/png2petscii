package us.happynet.png2petscii;

import java.awt.image.BufferedImage;

/**
 * generic Font class
 * 
 * @author nickb
 * @param <G> Glyph class
 */
abstract public class Font<G extends Glyph> {

    
    public G findClosest(BufferedImage tile) {
        G result = null;
        double score = Double.POSITIVE_INFINITY;
        for(G glyph : getAvailableGlyphs()) {
            double newscore = glyph.diff(tile);
            if (newscore < score) {
                score = newscore;
                result = glyph;
            }
        }
        return result;
    }

    abstract public Iterable<G> getAvailableGlyphs();
}
