/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package us.happynet.png2petscii.io;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.junit.Assert;
import org.junit.Test;
import us.happynet.png2petscii.model.Screen;
import static org.mockito.Mockito.*;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

/**
 *
 * @author nickb
 */
public class ScreenWriterTest {
    
    @Test
    public void testWriteFile() throws IOException {
        Screen scr = mock(Screen.class);
        ScreenWriterImpl sw = new ScreenWriterImpl(scr);
        File tmp = File.createTempFile("ScreenWriter", ".txt");
        try {
            sw.write(tmp);
            try(InputStream is = new FileInputStream(tmp)) {
                Assert.assertEquals("Header from ScreenWriter", 123, is.read());
                Assert.assertEquals("Data from Screen", 99, is.read());
                Assert.assertEquals("Footer from ScreenWriter", 0, is.read());
                Assert.assertEquals("EOF after footer", -1, is.read());
            }
        } finally {
            tmp.delete();
        }
        
        
    }

    // TODO: redesign whole thing with mock objects
    private static class ScreenWriterImpl extends ScreenWriter {

        public ScreenWriterImpl(Screen scr) throws IOException {
            super(scr);
            Answer s = (Answer) (InvocationOnMock invocation) -> {
                OutputStream os = (OutputStream) invocation.getArguments()[0];
                os.write(99);
                return null;
            };
            doAnswer(s).when(scr).writeData(any());
        }

        @Override
        public void write(OutputStream os) throws IOException {
            os.write(123); // an imaginary header
            source.writeData(os);
            os.write(0); // an imaginary footer
        }
    }
}
