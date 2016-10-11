package org.bitbucket.paidaki.pertcharts.gui.toolbar;

import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.geometry.Side;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import org.bitbucket.paidaki.pertcharts.application.Activity;
import org.bitbucket.paidaki.pertcharts.Main;
import org.bitbucket.paidaki.pertcharts.application.ProjectManager;
import org.bitbucket.paidaki.pertcharts.application.util.Resources;
import org.bitbucket.paidaki.pertcharts.application.util.Tooltips;
import org.bitbucket.paidaki.pertcharts.gui.IncludedWindow;
import org.bitbucket.paidaki.pertcharts.gui.MainWindowController;
import org.bitbucket.paidaki.pertcharts.gui.table.MyTableViewController;

import java.time.LocalDate;
import java.util.ArrayList;

public class MyButtonBarController implements IncludedWindow {

    private ProjectManager pm;

    private MainWindowController mainWindowController;
    private MyTableViewController tableViewController;

    @FXML
    private Button newButton, openButton, saveButton, preferencesButton, cutButton, copyButton, pasteButton, undoButton, redoButton, addButton, deleteButton, moveUpButton, moveDownButton, propertiesButton;
    @FXML
    private TextField searchTextField;
    @FXML
    private DatePicker projectStartDatePicker;

    private ContextMenu searchPopup;

    @FXML
    private void initialize() {
        // ProjectManager instance
        pm = ProjectManager.getInstance();

        // Button icons
        newButton.setGraphic(new ImageView(Resources.NEW_FILE_32));
        openButton.setGraphic(new ImageView(Resources.OPEN_32));
        saveButton.setGraphic(new ImageView(Resources.SAVE_32));
        preferencesButton.setGraphic(new ImageView(Resources.PREFERENCES_32));
        cutButton.setGraphic(new ImageView(Resources.CUT_32));
        copyButton.setGraphic(new ImageView(Resources.COPY_32));
        pasteButton.setGraphic(new ImageView(Resources.PASTE_32));
        undoButton.setGraphic(new ImageView(Resources.UNDO_32));
        redoButton.setGraphic(new ImageView(Resources.REDO_32));
        addButton.setGraphic(new ImageView(Resources.ADD_32));
        deleteButton.setGraphic(new ImageView(Resources.DELETE_32));
        moveUpButton.setGraphic(new ImageView(Resources.ARROW_UP_32));
        moveDownButton.setGraphic(new ImageView(Resources.ARROW_DOWN_32));
        propertiesButton.setGraphic(new ImageView(Resources.PROPERTIES_32));

        // Add tooltips
        newButton.setTooltip(Tooltips.NEW_FILE);
        openButton.setTooltip(Tooltips.OPEN_FILE);
        saveButton.setTooltip(Tooltips.SAVE);
        preferencesButton.setTooltip(Tooltips.PREFERENCES);
        cutButton.setTooltip(Tooltips.CUT);
        copyButton.setTooltip(Tooltips.COPY);
        pasteButton.setTooltip(Tooltips.PASTE);
        undoButton.setTooltip(Tooltips.UNDO);
        redoButton.setTooltip(Tooltips.REDO);
        addButton.setTooltip(Tooltips.ADD);
        deleteButton.setTooltip(Tooltips.DELETE);
        moveUpButton.setTooltip(Tooltips.MOVE_UP);
        moveDownButton.setTooltip(Tooltips.MOVE_DOWN);
        propertiesButton.setTooltip(Tooltips.PROPERTIES);
        searchTextField.setTooltip(Tooltips.SEARCH);

        // Setup search text field
        initSearchTextField();
        searchPopup = createSearchPopup();

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
        tableViewController = mainWindowController.getTableViewController();

        // Bind buttons to table
        cutButton.disableProperty().bind(tableViewController.emptySelectionBinding());
        copyButton.disableProperty().bind(tableViewController.emptySelectionBinding());
        pasteButton.disableProperty().bind(tableViewController.emptySelectionBinding());
        deleteButton.disableProperty().bind(tableViewController.emptySelectionBinding());
        moveUpButton.disableProperty().bind(tableViewController.emptySelectionBinding());
        moveDownButton.disableProperty().bind(tableViewController.emptySelectionBinding());
        propertiesButton.disableProperty().bind(tableViewController.emptySelectionBinding());

        // Log event
        if (Main.DEBUGGING) System.out.println(this.getClass().getSimpleName() + ": Setup done");
    }

    // Actions
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
    private void delete() {
        pm.delete();
    }

    @FXML
    private void addActivity() {
        pm.addActivity();
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

    // Disable property getters
    public BooleanProperty saveButtonDisableProperty() {
        return saveButton.disableProperty();
    }

    public BooleanProperty cutButtonDisableProperty() {
        return cutButton.disableProperty();
    }

    public BooleanProperty copyButtonDisableProperty() {
        return copyButton.disableProperty();
    }

    public BooleanProperty pasteButtonDisableProperty() {
        return pasteButton.disableProperty();
    }

    public BooleanProperty undoButtonDisableProperty() {
        return undoButton.disableProperty();
    }

    public BooleanProperty redoButtonDisableProperty() {
        return redoButton.disableProperty();
    }

    public BooleanProperty deleteButtonDisableProperty() {
        return deleteButton.disableProperty();
    }

    public BooleanProperty moveUpButtonDisableProperty() {
        return moveUpButton.disableProperty();
    }

    public BooleanProperty moveDownButtonDisableProperty() {
        return moveDownButton.disableProperty();
    }

    public BooleanProperty propertiesButtonDisableProperty() {
        return propertiesButton.disableProperty();
    }

    // Date Picker getter
    public ObjectProperty<LocalDate> projectStartDatePickerProperty() {
        return projectStartDatePicker.valueProperty();
    }

    // Search TextField methods
    private void initSearchTextField() {
        searchTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.length() > 2) {
                doSearch(newValue);
            } else {
                searchPopup.getItems().clear();
                searchPopup.hide();
            }
        });
        searchTextField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.DOWN) {
                if (!searchPopup.isShowing() && searchTextField.getText().length() > 2) {
                    doSearch(searchTextField.getText());
                }
            }
        });
    }

    private ContextMenu createSearchPopup() {
        return new ContextMenu();
    }

    public void setSearchFocus() {
        Platform.runLater(searchTextField::requestFocus);
    }

    private void doSearch(String searchTerm) {
        pm.searchForActivities(searchTerm);
    }

    public void addSearchResults(ArrayList<Activity> results) {
        searchPopup.getItems().clear();
        MenuItem item;
        for (int i = 0; i < results.size(); i++) {
            Activity activity = results.get(i);
            if (i > 9) {
                Label more = new Label("<more>");
                more.setAlignment(Pos.CENTER);
                more.setStyle("-fx-font-style: italic; -fx-text-fill: grey");
                SeparatorMenuItem separator = new SeparatorMenuItem();
                separator.setContent(more);
                searchPopup.getItems().add(separator);
                break;
            } else {
                Label text = new Label(activity.getId() + ". " + activity.getName());
                text.setMaxWidth(searchTextField.getPrefWidth());
                text.setWrapText(true);
                item = new MenuItem("", text);
                item.setOnAction(event -> {
                    tableViewController.setSelection(activity);
                    tableViewController.setFocus();
                });
                searchPopup.getItems().add(item);
            }
        }
        if (!searchPopup.isShowing() && !searchPopup.getItems().isEmpty()) {
            searchPopup.show(searchTextField, Side.BOTTOM, 0, 0);
        }
    }
}
