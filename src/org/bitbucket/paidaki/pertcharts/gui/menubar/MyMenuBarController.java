package org.bitbucket.paidaki.pertcharts.gui.menubar;

import javafx.beans.property.BooleanProperty;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import org.bitbucket.paidaki.pertcharts.Main;
import org.bitbucket.paidaki.pertcharts.application.ProjectManager;
import org.bitbucket.paidaki.pertcharts.application.util.Resources;
import org.bitbucket.paidaki.pertcharts.gui.IncludedWindow;
import org.bitbucket.paidaki.pertcharts.gui.MainWindowController;
import org.bitbucket.paidaki.pertcharts.gui.table.MyTableViewController;
import org.bitbucket.paidaki.pertcharts.gui.tabs.MyTabPaneController;
import org.bitbucket.paidaki.pertcharts.gui.toolbar.MyButtonBarController;

public class MyMenuBarController implements IncludedWindow {

    private ProjectManager pm;

    private MainWindowController mainWindowController;
    private MyButtonBarController buttonBarController;
    private MyTabPaneController tabPaneController;
    private MyTableViewController tableViewController;

    @FXML
    private Menu exportMenu, sampleMenu;
    @FXML
    private MenuItem newMenuItem, openMenuItem, saveMenuItem, imageMenuItem, pdfMenuItem, preferencesMenuItem, exitMenuItem;
    @FXML
    private MenuItem undoMenuItem, redoMenuItem, copyMenuItem, pasteMenuItem, cutMenuItem, duplicateMenuItem, deleteMenuItem, searchMenuItem;
    @FXML
    private RadioMenuItem pertRMenuItem, ganttRMenuItem, statsRMenuItem;
    @FXML
    private CheckMenuItem toolboxCMenuItem;
    @FXML
    private MenuItem addMenuItem, removeMenuItem, clearActMenuItem, clearDepMenuItem, moveUpMenuItem, moveDownMenuItem, propertiesMenuItem;
    @FXML
    private MenuItem bitbucketMenuItem, aboutMenuItem;

    @FXML
    private void initialize() {
        // ProjectManager instance
        pm = ProjectManager.getInstance();

        // Log event
        if (Main.DEBUGGING) System.out.println(this.getClass().getSimpleName() + ": Initialized");
    }

    @Override
    public void injectMainController(MainWindowController mainWindowController) {
        this.mainWindowController = mainWindowController;
        setup();
    }

    private void setup() {
        // Get controllers
        buttonBarController = mainWindowController.getButtonBarController();
        tabPaneController = mainWindowController.getTabPaneController();
        tableViewController = mainWindowController.getTableViewController();

        // Setup menu bar
        initMenuBarIcons();

        // Setup sample projects
        createSampleMenuItems();

        // Setup radio menu items
        createToggleGroup();

        // Setup bindings
        initMenuBarBindings();

        // Log event
        if (Main.DEBUGGING) System.out.println(this.getClass().getSimpleName() + ": Setup done");
    }

    private void createSampleMenuItems() {
        for (String s : Resources.SAMPLES) {
            MenuItem menuItem = new MenuItem(s);
            menuItem.setMnemonicParsing(false);
            menuItem.setOnAction(event -> pm.openSampleProject(s));
            sampleMenu.getItems().add(menuItem);
        }
        if (sampleMenu.getItems().isEmpty()) {
            sampleMenu.setVisible(false);
        }
    }

    private void createToggleGroup() {
        // Create toggle group for the view menu
        ToggleGroup tGroup = new ToggleGroup();
        pertRMenuItem.setToggleGroup(tGroup);
        ganttRMenuItem.setToggleGroup(tGroup);
        statsRMenuItem.setToggleGroup(tGroup);
    }

    private void initMenuBarIcons() {
        // File menu icons
        newMenuItem.setGraphic(new ImageView(Resources.NEW_FILE_16));
        openMenuItem.setGraphic(new ImageView(Resources.OPEN_16));
        saveMenuItem.setGraphic(new ImageView(Resources.SAVE_16));
        exportMenu.setGraphic(new ImageView(Resources.EXPORT_16));
        imageMenuItem.setGraphic(new ImageView(Resources.IMAGE_PNG_16));
        pdfMenuItem.setGraphic(new ImageView(Resources.PDF_16));
        preferencesMenuItem.setGraphic(new ImageView(Resources.PREFERENCES_16));
        exitMenuItem.setGraphic(new ImageView(Resources.EXIT_16));

        // Edit menu icons
        undoMenuItem.setGraphic(new ImageView(Resources.UNDO_16));
        redoMenuItem.setGraphic(new ImageView(Resources.REDO_16));
        copyMenuItem.setGraphic(new ImageView(Resources.COPY_16));
        pasteMenuItem.setGraphic(new ImageView(Resources.PASTE_16));
        cutMenuItem.setGraphic(new ImageView(Resources.CUT_16));
        deleteMenuItem.setGraphic(new ImageView(Resources.DELETE_16));
        searchMenuItem.setGraphic(new ImageView(Resources.SEARCH_16));

        // Activities menu icons
        addMenuItem.setGraphic(new ImageView(Resources.ADD_16));
        removeMenuItem.setGraphic(new ImageView(Resources.DELETE_16));
        clearActMenuItem.setGraphic(new ImageView(Resources.DELETE_ALL_16));
        clearDepMenuItem.setGraphic(new ImageView(Resources.CLEAR_16));
        moveUpMenuItem.setGraphic(new ImageView(Resources.ARROW_UP_16));
        moveDownMenuItem.setGraphic(new ImageView(Resources.ARROW_DOWN_16));
        propertiesMenuItem.setGraphic(new ImageView(Resources.PROPERTIES_16));

        // Help menu icons
        bitbucketMenuItem.setGraphic(new ImageView(Resources.BITBUCKET_16));
        aboutMenuItem.setGraphic(new ImageView(Resources.ABOUT_16));
    }

    private void initMenuBarBindings() {
        // Bind Buttons with menu items
        saveMenuItem.disableProperty().bindBidirectional(buttonBarController.saveButtonDisableProperty());
        undoMenuItem.disableProperty().bindBidirectional(buttonBarController.undoButtonDisableProperty());
        redoMenuItem.disableProperty().bindBidirectional(buttonBarController.redoButtonDisableProperty());
        copyMenuItem.disableProperty().bindBidirectional(buttonBarController.copyButtonDisableProperty());
        pasteMenuItem.disableProperty().bindBidirectional(buttonBarController.pasteButtonDisableProperty());
        duplicateMenuItem.disableProperty().bindBidirectional(buttonBarController.copyButtonDisableProperty());
        cutMenuItem.disableProperty().bindBidirectional(buttonBarController.cutButtonDisableProperty());
        deleteMenuItem.disableProperty().bindBidirectional(buttonBarController.deleteButtonDisableProperty());
        removeMenuItem.disableProperty().bindBidirectional(buttonBarController.deleteButtonDisableProperty());
        clearActMenuItem.disableProperty().bindBidirectional(tableViewController.clearDisableProperty());
        clearDepMenuItem.disableProperty().bindBidirectional(tableViewController.clearDisableProperty());
        moveUpMenuItem.disableProperty().bindBidirectional(buttonBarController.moveUpButtonDisableProperty());
        moveDownMenuItem.disableProperty().bindBidirectional(buttonBarController.moveDownButtonDisableProperty());
        propertiesMenuItem.disableProperty().bindBidirectional(buttonBarController.propertiesButtonDisableProperty());
        searchMenuItem.setOnAction(event1 -> buttonBarController.setSearchFocus());

        // Bind View menu items with Tabs
        pertRMenuItem.disableProperty().bindBidirectional(tabPaneController.pertTabDisableProperty());
        pertRMenuItem.setOnAction(event -> {
            tabPaneController.selectPertTab();
            pertRMenuItem.setSelected(true);    // Counter accelerator bug
        });
        ganttRMenuItem.disableProperty().bindBidirectional(tabPaneController.ganttTabDisableProperty());
        ganttRMenuItem.setOnAction(event -> {
            tabPaneController.selectGanttTab();
            ganttRMenuItem.setSelected(true);   // Counter accelerator bug
        });
        statsRMenuItem.disableProperty().bindBidirectional(tabPaneController.statsTabDisableProperty());
        statsRMenuItem.setOnAction(event -> {
            tabPaneController.selectStatsTab();
            statsRMenuItem.setSelected(true);   // Counter accelerator bug
        });
    }

    // Actions
    @FXML
    private void doExit() {
        pm.doExit();
    }

    @FXML
    private void newProject() {
        pm.createNewProject();
    }

    @FXML
    private void openProject() {
        pm.openProject();
    }

    @FXML
    private void saveProject() {
        pm.saveProject();
    }

    @FXML
    private void saveProjectAs() {
        pm.saveProjectAs();
    }

    @FXML
    private void exportChartToImage() {
        pm.exportChartToImage();
    }

    @FXML
    private void exportChartToPdf() {
        pm.exportChartToPdf();
    }

    @FXML
    private void openPreferences() {
        pm.openPreferences();
    }

    @FXML
    private void undo() {
        pm.undo();
    }

    @FXML
    private void redo() {
        pm.redo();
    }

    @FXML
    private void cut() {
        pm.cut();
    }

    @FXML
    private void copy() {
        pm.copy();
    }

    @FXML
    private void paste() {
        pm.paste();
    }

    @FXML
    private void duplicate() {
        pm.duplicate();
    }

    @FXML
    private void delete() {
        pm.delete();
    }

    @FXML
    private void selectAll() {
        pm.selectAll();
    }

    @FXML
    private void deselect() {
        pm.deselect();
    }

    @FXML
    private void addActivity() {
        pm.addActivity();
    }

    @FXML
    private void clearActivities() {
        pm.clearActivities();
    }

    @FXML
    private void clearDependencies() {
        pm.clearDependencies();
    }

    @FXML
    private void moveActivityUp() {
        pm.moveActivityUp();
    }

    @FXML
    private void moveActivityDown() {
        pm.moveActivityDown();
    }

    @FXML
    private void activityProperties() {
        pm.activityProperties();
    }

    @FXML
    private void openBitbucket() {
        pm.openBitbucket();
    }

    @FXML
    private void openAbout() {
        pm.openAbout();
    }

    // Radio menu item selection setters
    public void sselectPertRMenuItem(boolean value) {
        pertRMenuItem.setSelected(value);
    }

    public void selectGanttRMenuItem(boolean value) {
        ganttRMenuItem.setSelected(value);
    }

    public void selectStatsRMenuItem(boolean value) {
        statsRMenuItem.setSelected(value);
    }

    public BooleanProperty toolboxCMenuItemSelectedProperty() {
        return toolboxCMenuItem.selectedProperty();
    }

    // Property getters
    public BooleanProperty addDisableProperty() {
        return addMenuItem.disableProperty();
    }

    public BooleanProperty deleteDisableProperty() {
        return removeMenuItem.disableProperty();
    }

    public BooleanProperty clearDepDisableProperty() {
        return clearDepMenuItem.disableProperty();
    }

    public BooleanProperty clearDisableProperty() {
        return clearActMenuItem.disableProperty();
    }

    public BooleanProperty propertiesDisableProperty() {
        return propertiesMenuItem.disableProperty();
    }
}
