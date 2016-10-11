package org.bitbucket.paidaki.pertcharts.gui.dialogs;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.effect.BoxBlur;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.util.Optional;

public class ConfirmDialog {

    private boolean choiceOk;

    public ConfirmDialog(Stage mainWindow) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");
        alert.initStyle(StageStyle.UTILITY);
        alert.setHeaderText("Any unsaved changes will be lost");
        alert.setContentText("Are you sure you want to continue?");

        mainWindow.getScene().getRoot().setEffect(new BoxBlur());

        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK) {
            choiceOk = true;
        } else {
            choiceOk = false;
        }

        mainWindow.getScene().getRoot().setEffect(null);
    }

    public boolean isChoiceOk() {
        return choiceOk;
    }
}
