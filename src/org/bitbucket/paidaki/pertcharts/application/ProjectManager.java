package org.bitbucket.paidaki.pertcharts.application;

import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.bitbucket.paidaki.pertcharts.application.util.Resources;
import org.bitbucket.paidaki.pertcharts.gui.MainWindowController;
import org.bitbucket.paidaki.pertcharts.gui.dialogs.ExceptionDialog;
import org.bitbucket.paidaki.pertcharts.gui.dialogs.LoadingScreen;
import org.bitbucket.paidaki.pertcharts.gui.table.MyTableViewController;
import org.bitbucket.paidaki.pertcharts.gui.tabs.MyTabPaneController;
import org.bitbucket.paidaki.pertcharts.gui.toolbar.MyButtonBarController;

import java.awt.*;
import java.io.File;
import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;

public class ProjectManager {

    public static final String TITLE = "Pert Charts";
    public static Label status;
    private static ProjectManager instance = new ProjectManager();
    private static Stage mainWindow;
    private static Stage splashWindow;

    private MainWindowController mainWindowController;
    private MyTableViewController tableViewController;
    private MyTabPaneController tabPaneController;
    private MyButtonBarController buttonBarController;

    private ArrayList<Project> projects;    // For later implementation of multiple projects
    private Project activeProject;
    private ChartEditor chartEditor;

    // Private constructor for Singleton
    private ProjectManager() {
        projects = new ArrayList<>();
    }

    // Get the instance of the class
    public static ProjectManager getInstance() {
        return instance;
    }

    // Show the loading screen
    public void showSplashScreen() {
        if (splashWindow == null) {
            splashWindow = new LoadingScreen();
            PauseTransition pause = new PauseTransition(Duration.seconds(3));
            pause.setOnFinished(event -> {
                splashWindow.hide();
                mainWindow.show();
                Platform.runLater(tableViewController::setFocus);
            });
            pause.play();
        }
    }

    // Load the main window
    public void loadMainWindow(Stage primaryStage) {
        if (mainWindow == null) {
            mainWindow = primaryStage;
            mainWindow.getIcons().addAll(Resources.APP);
            mainWindow.setOnCloseRequest(event -> {
                ProjectManager.this.doExit();
                event.consume();
            });
        }
    }

    // Load all the controllers
    public void loadControllers() {
        tableViewController = mainWindowController.getTableViewController();
        tabPaneController = mainWindowController.getTabPaneController();
        buttonBarController = mainWindowController.getButtonBarController();
    }

    // Create new wrappers
    public void createNewProject() {
        if (activeProject == null || askSaveConfirm()) {
            Project newProject = new Project();
//            projects.add(newProject); // TODO only for multitab functionality
            setActiveProject(newProject);
            status.setText("New project created : " + activeProject.getProjectName());
        }
    }

    // Update the active wrappers
    private void setActiveProject(Project newProject) {
        activeProject = newProject;
        activeProject.setIsEditing(false);
        if (mainWindow != null) {
            mainWindow.titleProperty().unbind();
            mainWindow.titleProperty().bind(Bindings.concat(activeProject.saveAsteriskProperty(), activeProject.projectNameProperty(), " - ", TITLE));
        }
        chartEditor = new ChartEditor(tabPaneController.getChartCanvas(), activeProject, tabPaneController.autoArrangeProperty());
        tableViewController.setTableItems(activeProject.getActivities());
        buttonBarController.projectStartDatePickerProperty().unbind();
        buttonBarController.projectStartDatePickerProperty().bindBidirectional(activeProject.startingDateProperty());
    }

    // Ask for save confirmation
    private boolean askSaveConfirm() {
        return !activeProject.getIsEditing() || activeProject.getActivities().isEmpty() || mainWindowController.showConfirmDialog(mainWindow);
    }

    // Update project statistics
    public void updateProjectStats() {
        activeProject.updateStats();
    }

    // Button actions
    public void doExit() {
        if (askSaveConfirm()) {
            Platform.runLater(Platform::exit);
        }
    }

    public void openProject() {
        File file = mainWindowController.showOpenFileDialog(mainWindow);
        if (file != null && askSaveConfirm()) {
            Project project = FileManager.loadProjectFromFile(file);
            if (project != null) {
                setActiveProject(project);
                tableViewController.focusLastRow();
                status.setText("Project loaded : " + file.getPath());
            } else {
                status.setText("Failed to load Project : " + file.getPath());
            }
        }
    }

    public void openSampleProject(String sampleFile) {
        if (askSaveConfirm()) {
            InputStream inputStream = Resources.class.getResourceAsStream("/org/bitbucket/paidaki/pertcharts/resources/samples/" + sampleFile);
            Project project = FileManager.loadProjectFromFile(inputStream, sampleFile);
            if (project != null) {
                setActiveProject(project);
                tableViewController.focusLastRow();
                status.setText("Sample Project loaded : " + sampleFile);
            } else {
                status.setText("Failed to load Sample Project : " + sampleFile);
            }
        }
    }

    public void saveProject() {
        if (activeProject.getFile() != null) {
            if (FileManager.saveProjectToFile(activeProject, activeProject.getFile())) {
                activeProject.setIsEditing(false);
                status.setText("Project saved : " + activeProject.getFile().getPath());
            } else {
                status.setText("Failed to save Project : " + activeProject.getFile().getPath());
            }
        } else {
            saveProjectAs();
        }
    }

    public void saveProjectAs() {
        File file = mainWindowController.showProjectSaveFileDialog(mainWindow, activeProject.getProjectName());
        if (file != null) {
            if (FileManager.saveProjectToFile(activeProject, file)) {
                activeProject.setFile(file);
                activeProject.setIsEditing(false);
                status.setText("Project saved : " + file.getPath());
            } else {
                status.setText("Failed to save Project : " + file.getPath());
            }
        }
    }

    public void exportChartToImage() {
        File file = mainWindowController.showImageSaveFileDialog(mainWindow, activeProject.getProjectName());
        if (file != null) {
            if (FileManager.saveChartToImage(chartEditor.getCanvasSnapshot(), file)) {
                status.setText("Image saved : " + file.getPath());
            } else {
                status.setText("Failed to save Image : " + file.getPath());
            }
        }
    }

    public void autoArrangeChart() {
        chartEditor.doAutoArrange();
    }

    public void exportChartToPdf() {
    }

    public void openPreferences() {
        //TODO Better preferences
        mainWindowController.showPreferences(mainWindow);
    }

    public void undo() {
    }

    public void redo() {
    }

    public void cut() {
    }

    public void copy() {
    }

    public void paste() {
    }

    public void duplicate() {
    }

    public void delete() {
        Activity activityToRemove = tableViewController.getSelectedItem();
        if (activityToRemove != null) {
            activeProject.deleteActivity(activityToRemove);
        }
        tableViewController.setFocus();
    }

    public void selectAll() {
    }

    public void deselect() {
    }

    public void addActivity() {
        activeProject.addActivity();
        tableViewController.focusLastRow();
    }

    public void clearActivities() {
        activeProject.clearActivities();
    }

    public void clearDependencies() {
        activeProject.clearDependencies();
        chartEditor.updateLines();
        chartEditor.checkDoAutoArrange();
    }

    public void moveActivityUp() {
        Activity activityToMoveUp = tableViewController.getSelectedItem();
        if (activityToMoveUp != null) {
            if (tabPaneController.getSelectedTab().getText().toLowerCase().contains("statistics")) {
                activeProject.moveActivityUp(activityToMoveUp);
                tabPaneController.selectStatsTab();
            } else {
                activeProject.moveActivityUp(activityToMoveUp);
            }
        }
    }

    public void moveActivityDown() {
        Activity activityToMoveDown = tableViewController.getSelectedItem();
        if (activityToMoveDown != null) {
            if (tabPaneController.getSelectedTab().getText().toLowerCase().contains("statistics")) {
                activeProject.moveActivityDown(activityToMoveDown);
                tabPaneController.selectStatsTab();
            } else {
                activeProject.moveActivityDown(activityToMoveDown);
            }
        }
    }

    public void activityProperties() {
        Activity selectedActivity = tableViewController.getSelectedItem();
        mainWindowController.showProperties(selectedActivity, activeProject.getActivities(), mainWindow);
    }

    public void searchForActivities(String searchTerm) {
        buttonBarController.addSearchResults(activeProject.searchForActivities(searchTerm));
    }

    public void openBitbucket() {
        try {
            Desktop.getDesktop().browse(new URI(Resources.REPO_URL));
        } catch (Exception e) {
            Alert alert = new ExceptionDialog(e);
            alert.setContentText("Could not open URL : " + Resources.REPO_URL);
            alert.showAndWait();
        }
    }

    public void openAbout() {
    }

    public void setActivityStatsTab(Activity activity) {
        tabPaneController.setActivityStats(activity);
        setProjectStatsTab(activeProject);
    }

    public void selectActivity(Activity activity) {
        tableViewController.setSelection(activity);
    }

    public void setActivityChartTab(Activity activity) {
        chartEditor.selectActivityNode(activity);
    }

    public void setProjectStatsTab(Project project) {
        tabPaneController.setProjectStats(project);
    }

    public void updateChartCriticalPath() {
        chartEditor.updateLines();
        chartEditor.checkDoAutoArrange();
    }

    public void addChartActivity(Activity activity) {
        chartEditor.addActivity(activity);
    }

    public void removeChartActivity(Activity activity) {
        chartEditor.removeActivity(activity);
    }

    // Setters - Getters
    public ObservableList<Activity> getData() {
        return activeProject.getActivities();
    }

    public void injectMainController(MainWindowController mainWindowController) {
        this.mainWindowController = mainWindowController;
    }
}
