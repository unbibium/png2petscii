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

    /**
     * Provides all glyphs used by the given instnace.
     * In the most ordinary case, will return every glyph in the
     * entire font.  In some instances, will filter out ugly
     * characters that probably wouldn't look good in the finished
     * product.  In others, will provide several different versions
     * of each glyph.
     * @return all glyphs that are allowed to be used in output files
     */
    abstract public Iterable<G> getAvailableGlyphs();
    
}
