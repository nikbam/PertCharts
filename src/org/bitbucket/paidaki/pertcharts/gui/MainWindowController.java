package org.bitbucket.paidaki.pertcharts.gui;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import org.bitbucket.paidaki.pertcharts.application.Activity;
import org.bitbucket.paidaki.pertcharts.Main;
import org.bitbucket.paidaki.pertcharts.application.ProjectManager;
import org.bitbucket.paidaki.pertcharts.gui.dialogs.*;
import org.bitbucket.paidaki.pertcharts.gui.menubar.MyMenuBarController;
import org.bitbucket.paidaki.pertcharts.gui.table.MyTableViewController;
import org.bitbucket.paidaki.pertcharts.gui.tabs.MyTabPaneController;
import org.bitbucket.paidaki.pertcharts.gui.toolbar.MyButtonBarController;

import java.io.File;

public class MainWindowController {

    private ProjectManager pm;

    @FXML
    private MyMenuBarController menuBarController;
    @FXML
    private MyButtonBarController buttonBarController;
    @FXML
    private MyTableViewController tableViewController;
    @FXML
    private MyTabPaneController tabPaneController;
    @FXML
    private Label statusLabel;

    @FXML
    private void initialize() {
        //Get ProjectManager instance
        pm = ProjectManager.getInstance();

        // Inject MainWindowController
        pm.injectMainController(this);
        menuBarController.injectMainController(this);
        buttonBarController.injectMainController(this);
        tableViewController.injectMainController(this);
        tabPaneController.injectMainController(this);

        // Set the status bar
        ProjectManager.status = statusLabel;

        // Log event
        if (Main.DEBUGGING) System.out.println(this.getClass().getSimpleName() + ": Initialized");
    }

    // Show a save file dialog
    public File showProjectSaveFileDialog(Stage mainWindow, String fileName) {
        return new SaveFileDialogXML(mainWindow, fileName).getFile();
    }

    public File showImageSaveFileDialog(Stage mainWindow, String fileName) {
        return new SaveFileDialogPNG(mainWindow, fileName).getFile();
    }

    // Show an open file dialog
    public File showOpenFileDialog(Stage mainWindow) {
        return new OpenFileDialog(mainWindow).getFile();
    }

    // Show confirmation dialog
    public boolean showConfirmDialog(Stage mainWindow) {
        return new ConfirmDialog(mainWindow).isChoiceOk();
    }

    // Show preferences window
    public void showPreferences(Stage mainWindow) {
        new Preferences(mainWindow);
    }

    // Show activity properties
    public void showProperties(Activity selectedActivity, ObservableList<Activity> data, Stage mainWindow) {
        new Properties(selectedActivity, data, mainWindow);
    }

    // Controllers getters
    public MyMenuBarController getMenuBarController() {
        return menuBarController;
    }

    public MyButtonBarController getButtonBarController() {
        return buttonBarController;
    }

    public MyTabPaneController getTabPaneController() {
        return tabPaneController;
    }

    public MyTableViewController getTableViewController() {
        return tableViewController;
    }
}
