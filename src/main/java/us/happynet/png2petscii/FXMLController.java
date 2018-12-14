package us.happynet.png2petscii;

import us.happynet.png2petscii.model.Font;

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
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Window;
import us.happynet.png2petscii.model.Glyph;
import us.happynet.png2petscii.model.Screen;

public class FXMLController implements Initializable {

    @FXML
    private Label label;
    @FXML
    private ImageView srcImage;
    @FXML
    private ImageView dstImage;
    @FXML
    private ChoiceBox<String> outputChoice;

    // program state
    private File selectedFile;
    private Screen<? extends Font> outputScreen;

    @FXML
    private void handleOpen(ActionEvent event) {
        Window stage = srcImage.getScene().getWindow();
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Source File");
        fileChooser.getExtensionFilters().add(new ExtensionFilter("Image Files", "*.jpg", "*.jpeg", "*.png"));
        selectedFile = fileChooser.showOpenDialog(stage);
        try (InputStream fis = new BufferedInputStream(new FileInputStream(selectedFile))) {
            Image src = new Image(fis, 312, 200, true, true);
            srcImage.setImage(src);
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


    @FXML
    private void handleSave(ActionEvent event) {
        label.setText("SAVE!");
        Window stage = srcImage.getScene().getWindow();
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Output File");
        OutputFormats.populate(fileChooser, outputScreen.getClass());
        File outputFile = fileChooser.showSaveDialog(stage);
        ExtensionFilter format = fileChooser.getSelectedExtensionFilter();
        try {
            OutputFormats.write(format, outputScreen, outputFile);
            label.setText("Saved!");
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
    private void valueChange() {
        if(srcImage.getImage() != null) {
            performConversion();
            label.setText("new format");
        }
    }

    @FXML
    private void imageLoaded() {
        System.out.println("Image loaded");
        performConversion();
    }

    private void performConversion() {
        Font font;
        try {
            font = Font.get(outputChoice.getValue());
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

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        ObservableList<String> list = FXCollections.observableArrayList(Font.getFontNames());
        outputChoice.setItems(list);
        outputChoice.setValue(list.get(0));
    }
}
