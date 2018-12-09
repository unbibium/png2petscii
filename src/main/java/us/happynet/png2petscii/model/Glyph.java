/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package us.happynet.png2petscii.model;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Glyph superclass for future refactoring.
 * 
 * Represents any glyph that can be compared with a buffered image
 * and then written in some ASCII variant.
 *
 * @author nickb
 */
abstract public class Glyph {
    
    abstract void writeData(OutputStream os) throws IOException;
    abstract double diff(BufferedImage imgB);
    
}
