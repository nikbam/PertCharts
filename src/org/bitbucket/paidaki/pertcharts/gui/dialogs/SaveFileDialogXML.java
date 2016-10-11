package org.bitbucket.paidaki.pertcharts.gui.dialogs;

import javafx.scene.effect.BoxBlur;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;

public class SaveFileDialogXML {

    private File selectedFile;

    public SaveFileDialogXML(Stage mainWindow, String initialFilename) {
        FileChooser fileChooser = new FileChooser();

        fileChooser.setInitialDirectory(new File(System.getProperty("user.home") + File.separator + "Desktop"));
        fileChooser.setInitialFileName(initialFilename + ".xml");
        fileChooser.setTitle("Save As");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("XML files (*.xml)", "*.xml"),
                new FileChooser.ExtensionFilter("Pert Charts wrappers files (*.pert)", "*.pert"),
                new FileChooser.ExtensionFilter("Java Serializable (*.ser)", "*.ser")
        );

        mainWindow.getScene().getRoot().setEffect(new BoxBlur());

        selectedFile = fileChooser.showSaveDialog(mainWindow);
        if (selectedFile != null) {
            if (fileChooser.getSelectedExtensionFilter().getDescription().toLowerCase().contains(".xml") && !selectedFile.getName().toLowerCase().endsWith(".xml")) {
                selectedFile = new File(selectedFile.getPath() + ".xml");
            } else if (fileChooser.getSelectedExtensionFilter().getDescription().toLowerCase().contains(".pert") && !selectedFile.getName().toLowerCase().endsWith(".pert")) {
                selectedFile = new File(selectedFile.getPath() + ".pert");
            } else if (fileChooser.getSelectedExtensionFilter().getDescription().toLowerCase().contains(".ser") && !selectedFile.getName().toLowerCase().endsWith(".ser")) {
                selectedFile = new File(selectedFile.getPath() + ".ser");
            }
        }
        mainWindow.getScene().getRoot().setEffect(null);
    }

    public File getFile() {
        return selectedFile;
    }

}
