package org.bitbucket.paidaki.pertcharts;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.bitbucket.paidaki.pertcharts.application.Project;
import org.bitbucket.paidaki.pertcharts.application.ProjectManager;

public class Main extends Application {

    public static final boolean DEBUGGING = false;

    @Override
    public void start(Stage primaryStage) throws Exception {
        ProjectManager pm = ProjectManager.getInstance();

        pm.showSplashScreen();

        Parent root = FXMLLoader.load(getClass().getResource("/org/bitbucket/paidaki/pertcharts/gui/MainWindow.fxml"));
        primaryStage.setTitle(ProjectManager.TITLE);
        Scene scene = new Scene(root);
        scene.getStylesheets().add(this.getClass().getResource("/org/bitbucket/paidaki/pertcharts/gui/css/Style.css").toExternalForm());
        primaryStage.setScene(scene);

        pm.loadMainWindow(primaryStage);
        pm.loadControllers();
        Project.clearNextProjectId();
        pm.createNewProject();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
