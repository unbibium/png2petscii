package us.happynet.png2petscii;

import us.happynet.png2petscii.io.P00Writer;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Window;
import javax.imageio.ImageIO;
import us.happynet.png2petscii.io.PrgWriter;
import us.happynet.png2petscii.io.ScreenWriter;
import us.happynet.png2petscii.io.SeqWriter;

public class FXMLController implements Initializable {
    
    @FXML
    private Label label;
    @FXML
    private ImageView srcImage;
    @FXML
    private Canvas dstCanvas;
    @FXML
    private ImageView dstImage;
    
    // program state
    private File selectedFile;
    PetsciiScreen outputScreen;
    
    @FXML
    private void handleOpen(ActionEvent event) {
        System.out.println("You clicked OPEN!");
        Window stage = srcImage.getScene().getWindow();        
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Source File");
        fileChooser.getExtensionFilters().add(new ExtensionFilter("Image Files", "*.jpg", "*.jpeg", "*.png"));
        selectedFile=fileChooser.showOpenDialog(stage);
        try(InputStream fis = new BufferedInputStream(new FileInputStream(selectedFile))) {
            Image s = new Image(fis, 320, 200, true, true);
            srcImage.setImage(s);
        } catch (FileNotFoundException ex) {
            // TODO: show error dialog
            Logger.getLogger(FXMLController.class.getName()).log(Level.SEVERE, null, ex);
            label.setText(ex.getMessage());
        } catch (IOException ex) {
            // TODO: show error dialog
            Logger.getLogger(FXMLController.class.getName()).log(Level.SEVERE, null, ex);
            label.setText(ex.getMessage());
        }
    }

    private static final ExtensionFilter S00_FORMAT = new ExtensionFilter(
                "S00 Sequential File", "*.S00");
    private static final ExtensionFilter SEQ_FORMAT = new ExtensionFilter(
                "Sequential File, raw", "*.seq", "*.txt");
    private static final ExtensionFilter P00_FORMAT = new ExtensionFilter(
                "P00 Program File", "*.P00");
    private static final ExtensionFilter PRG_FORMAT = new ExtensionFilter(
                "Program File, raw", "*.PRG");
    
    @FXML
    private void handleSave(ActionEvent event) {
        label.setText("SAVE!");
        Window stage = srcImage.getScene().getWindow();        
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Output File");
        fileChooser.getExtensionFilters().addAll(P00_FORMAT, SEQ_FORMAT, PRG_FORMAT);
        File outputFile=fileChooser.showSaveDialog(stage);
        ExtensionFilter format = fileChooser.getSelectedExtensionFilter();
        ScreenWriter writer;
        // TODO: put these formats in an Enum type with factory methods
        // or something fancy like that.
        if(format == P00_FORMAT) {
            writer = new P00Writer(outputScreen, outputFile);
        } else if(format == PRG_FORMAT) {
            writer = new PrgWriter(outputScreen);
        } else if(format == SEQ_FORMAT) {
            writer = new SeqWriter(outputScreen);
        } else if (format == null) {
            label.setText("null");
            return;
        } else {
            label.setText(format.getDescription() + " not supported yet");
            return;
        }
                
        try {
            writer.write(outputFile);
            label.setText("Success?");
        } catch (IOException ex) {
            Logger.getLogger(FXMLController.class.getName()).log(Level.SEVERE, null, ex);
            label.setText(ex.getMessage());
        }
    }
    
    @FXML
    private void handleRetry(ActionEvent event) {
        performConversion();
        label.setText("RETRY!");
    }
    
    @FXML
    private void imageLoaded() {
        System.out.println("Image loaded");
        performConversion();
    }

    private void performConversion() {
        PetsciiFont font;
        BufferedImage src;
        try {
            font = getFont();
            src = ImageIO.read(selectedFile);
        } catch (IOException ex) {
            // TODO: display error when font can't load
            Logger.getLogger(FXMLController.class.getName()).log(Level.SEVERE, null, ex);
            label.setText(ex.getMessage());
            return;
        }
        outputScreen = font.convert(srcImage.getImage());
        BufferedImage bi = outputScreen.toBufferedImage();
        dstImage.setImage(SwingFXUtils.toFXImage(bi, null));
    }
    
    private PetsciiFont getFont() throws IOException {
        // TODO: add a drop-down box of available fonts, and return selected.
        String filename = "charset_upper.png";
        ClassLoader cl = getClass().getClassLoader();
        try(InputStream is = cl.getResourceAsStream(filename)) {
            if(is == null) {
                throw new FileNotFoundException(filename);
            }
            BufferedImage im = ImageIO.read(is);
            return new PetsciiColorFont(new PetsciiFont(im));
        }
    }
    
    
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    
}
