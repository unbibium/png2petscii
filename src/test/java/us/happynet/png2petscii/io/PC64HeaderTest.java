/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package us.happynet.png2petscii.io;

import static org.junit.Assert.assertArrayEquals;
import org.junit.Test;

/**
 *
 * @author nickb
 */
public class PC64HeaderTest {
    @Test
    public void headerForSmallFilenames() {
        byte[] expected = {'A','B','C','_','_','_',0,0,0,0,0,0,0,0,0,0};
        assertArrayEquals(expected,PC64Header.toHeaderFilename("ABC_\té"));
    }
    @Test
    public void stripsExtension() {
        byte[] expected = {'A','B','C','.','X','Y','Z',0,0,0,0,0,0,0,0,0};
        assertArrayEquals(expected,PC64Header.toHeaderFilename("ABC.XYZ.DED"));
    }
    @Test
    public void truncatesLongNames() {
        byte[] expected = {'A','B','C','D','E','F','G','H','I','J','K','L','M','N','O','P'};
        assertArrayEquals(expected,PC64Header.toHeaderFilename("ABCDEFGHIJKLMNOPQRSTUVWXYZ"));
    }
    @Test
    public void convertsToUppercase() {
        byte[] expected = {'A','B','C','D','E','F','G','H','I','J','K','L','M','N','O','P'};
        assertArrayEquals(expected,PC64Header.toHeaderFilename("abcdefghijklmnop"));
    }
}
