package org.bitbucket.paidaki.pertcharts.gui.dialogs;

import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.effect.BoxBlur;
import javafx.scene.layout.StackPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class Preferences extends Stage {

    public Preferences(Stage mainWindow) {
        this.initStyle(StageStyle.UTILITY);
        this.initModality(Modality.WINDOW_MODAL);
        this.initOwner(mainWindow);
        Scene scene = new Scene(new StackPane(new Label("Preferences")), 300, 150);
        this.setScene(scene);
        mainWindow.getScene().getRoot().setEffect(new BoxBlur());
        this.setOnCloseRequest(e -> mainWindow.getScene().getRoot().setEffect(null));
        this.setResizable(false);
        this.show();
    }

}
