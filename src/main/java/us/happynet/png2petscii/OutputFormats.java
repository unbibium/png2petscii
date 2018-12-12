/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package us.happynet.png2petscii;

import java.io.File;
import java.io.IOException;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import us.happynet.png2petscii.io.P00Writer;
import us.happynet.png2petscii.io.PrgWriter;
import us.happynet.png2petscii.io.ScreenWriter;
import us.happynet.png2petscii.model.PetsciiScreen;
import us.happynet.png2petscii.model.Screen;

/**
 *
 * @author nickb
 */
public class OutputFormats {

    private static final ExtensionFilter S00_FORMAT = new ExtensionFilter(
            "S00 Sequential File", "*.s00");
    private static final ExtensionFilter TXT_FORMAT = new ExtensionFilter(
            "Text File", "*.txt", "*.seq");
    private static final ExtensionFilter P00_FORMAT = new ExtensionFilter(
            "P00 Program File", "*.p00");
    private static final ExtensionFilter PRG_FORMAT = new ExtensionFilter(
            "Program File", "*.prg");
    
    static void populate(FileChooser fileChooser, Class<? extends Screen> aClass) {
        fileChooser.getExtensionFilters().add(TXT_FORMAT);
        if(aClass.isAssignableFrom(PetsciiScreen.class)) {
            fileChooser.getExtensionFilters().addAll(S00_FORMAT, P00_FORMAT, PRG_FORMAT);
        }
    }

    static void write(ExtensionFilter format, Screen outputScreen, File outputFile) throws IOException {
        // TODO: put these formats in an Enum type with factory methods
        // or something fancy like that.
        ScreenWriter writer;
        if (format == P00_FORMAT) {
            writer = new P00Writer(outputScreen, outputFile);
        } else if (format == PRG_FORMAT) {
            writer = new PrgWriter(outputScreen);
        } else {
            writer = new ScreenWriter(outputScreen);
        }

        writer.write(outputFile);

    }
    
}
