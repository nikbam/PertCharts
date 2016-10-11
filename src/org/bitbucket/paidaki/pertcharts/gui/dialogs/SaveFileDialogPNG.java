package org.bitbucket.paidaki.pertcharts.gui.dialogs;

import javafx.scene.effect.BoxBlur;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;

public class SaveFileDialogPNG {

    private File selectedFile;

    public SaveFileDialogPNG(Stage mainWindow, String initialFilename) {
        FileChooser fileChooser = new FileChooser();

        fileChooser.setInitialDirectory(new File(System.getProperty("user.home") + File.separator + "Desktop"));
        fileChooser.setInitialFileName(initialFilename + ".png");
        fileChooser.setTitle("Save As");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("PNG (*.png)", "*.png"),
                new FileChooser.ExtensionFilter("JPEG (*.jpg)", "*.jpg"),
                new FileChooser.ExtensionFilter("GIF (*.gif)", "*.gif")
        );

        mainWindow.getScene().getRoot().setEffect(new BoxBlur());

        selectedFile = fileChooser.showSaveDialog(mainWindow);
        if (selectedFile != null) {
            if (fileChooser.getSelectedExtensionFilter().getDescription().toLowerCase().contains(".png") && !selectedFile.getName().toLowerCase().endsWith(".png")) {
                selectedFile = new File(selectedFile.getPath() + ".png");
            } else if (fileChooser.getSelectedExtensionFilter().getDescription().toLowerCase().contains(".jpg") && !selectedFile.getName().toLowerCase().endsWith(".jpg")) {
                selectedFile = new File(selectedFile.getPath() + ".jpg");
            } else if (fileChooser.getSelectedExtensionFilter().getDescription().toLowerCase().contains(".gif") && !selectedFile.getName().toLowerCase().endsWith(".gif")) {
                selectedFile = new File(selectedFile.getPath() + ".gif");
            }
        }
        mainWindow.getScene().getRoot().setEffect(null);
    }

    public File getFile() {
        return selectedFile;
    }

}
