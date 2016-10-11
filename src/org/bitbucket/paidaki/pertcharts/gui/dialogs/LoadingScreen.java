package org.bitbucket.paidaki.pertcharts.gui.dialogs;

import javafx.animation.PauseTransition;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import org.bitbucket.paidaki.pertcharts.application.util.Resources;

public class LoadingScreen extends Stage {

    private ImageView loadingImage;

    public LoadingScreen() {
        loadingImage = new ImageView(Resources.LOADING);
        Scene scene = new Scene(new StackPane(loadingImage), loadingImage.getImage().getWidth(), loadingImage.getImage().getHeight());

        this.initStyle(StageStyle.UNDECORATED);
        this.initModality(Modality.WINDOW_MODAL);
        this.setScene(scene);
        this.setResizable(false);
        this.getIcons().addAll(Resources.APP);
        PauseTransition pause = new PauseTransition(Duration.seconds(0.1));
        pause.setOnFinished(event -> this.show());
        pause.play();
    }

    public ImageView getLoadingImage() {
        return loadingImage;
    }
}
