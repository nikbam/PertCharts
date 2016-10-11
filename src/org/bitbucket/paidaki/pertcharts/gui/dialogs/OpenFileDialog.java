package org.bitbucket.paidaki.pertcharts.gui.dialogs;

import javafx.scene.effect.BoxBlur;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;

public class OpenFileDialog {

    private File selectedFile;

    public OpenFileDialog(Stage mainWindow) {
        FileChooser fileChooser = new FileChooser();

        fileChooser.setInitialDirectory(new File(System.getProperty("user.home") + File.separator + "Desktop"));
        fileChooser.setTitle("Open");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("XML files (*.xml)", "*.xml"),
                new FileChooser.ExtensionFilter("Pert Charts wrappers files (*.pert)", "*.pert"),
                new FileChooser.ExtensionFilter("Java Serializable (*.ser)", "*.ser")
        );

        mainWindow.getScene().getRoot().setEffect(new BoxBlur());

        selectedFile = fileChooser.showOpenDialog(mainWindow);

        mainWindow.getScene().getRoot().setEffect(null);
    }

    public File getFile() {
        return selectedFile;
    }

}
