package us.happynet.png2petscii;

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
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelReader;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Window;
import javax.imageio.ImageIO;

public class FXMLController implements Initializable {
    
    @FXML
    private Label label;
    @FXML
    private ImageView srcImage;
    @FXML
    private Canvas dstCanvas;
    
    private File selectedFile;
    
    @FXML
    private void handleButtonAction(ActionEvent event) {
        System.out.println("You clicked me!");
        label.setText("Hello World!");
    }
    
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
        } catch (IOException ex) {
            // TODO: show error dialog
            Logger.getLogger(FXMLController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @FXML
    private void handleSave(ActionEvent event) {
        System.out.println("You clicked me!");
        label.setText("SAVE!");
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
            return;
        }
        PetsciiScreen screen = font.convert(src);
        //PetsciiScreen screen = font.convert(srcImage.getImage());
        screen.drawTo(dstCanvas);
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
            return new PetsciiFont(im);
        }
    }
    
    
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    
}
