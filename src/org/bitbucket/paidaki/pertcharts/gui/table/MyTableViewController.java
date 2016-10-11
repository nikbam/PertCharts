package org.bitbucket.paidaki.pertcharts.gui.table;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.util.Callback;
import org.bitbucket.paidaki.pertcharts.application.Activity;
import org.bitbucket.paidaki.pertcharts.Main;
import org.bitbucket.paidaki.pertcharts.application.ProjectManager;
import org.bitbucket.paidaki.pertcharts.application.util.Resources;
import org.bitbucket.paidaki.pertcharts.application.util.Tooltips;
import org.bitbucket.paidaki.pertcharts.gui.IncludedWindow;
import org.bitbucket.paidaki.pertcharts.gui.MainWindowController;
import org.bitbucket.paidaki.pertcharts.gui.tabs.MyTabPaneController;
import org.bitbucket.paidaki.pertcharts.gui.toolbar.MyButtonBarController;

import java.time.LocalDate;

public class MyTableViewController implements IncludedWindow {

    private ProjectManager pm;

    private MainWindowController mainWindowController;
    private MyTabPaneController tabPaneController;
    private MyButtonBarController buttonBarController;

    @FXML
    private TableView<Activity> table;
    @FXML
    private TableColumn<Activity, Integer> idCol, durationCol, mostLikelyCol, optimisticCol, pessimisticCol;
    @FXML
    private TableColumn<Activity, String> nameCol, dependenciesCol;
    @FXML
    private TableColumn<Activity, LocalDate> startDateCol, endDateCol;
    @FXML
    private MenuItem addMenuItem, removeMenuItem, clearActMenuItem, clearDepMenuItem, moveUpMenuItem, moveDownMenuItem, propertiesMenuItem;

    @FXML
    private void initialize() {
        // Get ProjectManager instance
        pm = ProjectManager.getInstance();

        // Clear focus from buttons
        Platform.runLater(() -> table.getParent().requestFocus());

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
        tabPaneController = mainWindowController.getTabPaneController();
        buttonBarController = mainWindowController.getButtonBarController();

        // Setup the table
        initTable();

        // Setup activities context menu
        initActivitiesMenu();

        // Log event
        if (Main.DEBUGGING) System.out.println(this.getClass().getSimpleName() + ": Setup done");
    }

    private void initTable() {
        // Add button placeholder for when the table is empty
        Button addActivity = new Button("Add Activity");
        addActivity.setGraphic(new ImageView(Resources.ADD_32));
        addActivity.getStyleClass().add("addActivity");
        addActivity.setOnAction(event -> pm.addActivity());
        addActivity.setTooltip(Tooltips.ADD);
        table.setPlaceholder(addActivity);

        // Double click on row event
        table.setRowFactory(param -> {
            TableRow<Activity> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() > 1 && !row.isEmpty() && table.getEditingCell() == null) {
                    pm.activityProperties();
                }
            });
            return row;
        });

        // Id column
        idCol.setCellValueFactory(new PropertyValueFactory<Activity, Integer>("id"));

        // Name column
        Callback<TableColumn<Activity, String>, TableCell<Activity, String>> nameCellFactory = (TableColumn<Activity, String> param) -> new TextFieldCell(table);
        nameCol.setCellValueFactory(new PropertyValueFactory<Activity, String>("name"));
        nameCol.setCellFactory(nameCellFactory);

        // Dependencies column
        dependenciesCol.setCellValueFactory(new PropertyValueFactory<Activity, String>("dependencies"));

        // Start date column
        Callback<TableColumn<Activity, LocalDate>, TableCell<Activity, LocalDate>> startDateCellFactory = (TableColumn<Activity, LocalDate> param) -> new StartDatePickerCell(table);
        startDateCol.setCellValueFactory(new PropertyValueFactory<Activity, LocalDate>("startDate"));
        startDateCol.setCellFactory(startDateCellFactory);

        // End date column
        Callback<TableColumn<Activity, LocalDate>, TableCell<Activity, LocalDate>> endDateCellFactory = (TableColumn<Activity, LocalDate> param) -> new EndDatePickerCell(table);
        endDateCol.setCellValueFactory(new PropertyValueFactory<Activity, LocalDate>("endDate"));
        endDateCol.setCellFactory(endDateCellFactory);

        Callback<TableColumn<Activity, Integer>, TableCell<Activity, Integer>> numberCellFactory = (TableColumn<Activity, Integer> param) -> new NumberFieldCell(table);
        // Duration column
        durationCol.setCellValueFactory(new PropertyValueFactory<Activity, Integer>("duration"));
        durationCol.setCellFactory(numberCellFactory);

        // Optimistic column
        optimisticCol.setCellValueFactory(new PropertyValueFactory<Activity, Integer>("optimistic"));
        optimisticCol.setCellFactory(numberCellFactory);
        optimisticCol.setEditable(false);

        // Most likely column
        mostLikelyCol.setCellValueFactory(new PropertyValueFactory<Activity, Integer>("mostLikely"));
        mostLikelyCol.setCellFactory(numberCellFactory);
        mostLikelyCol.setEditable(false);

        // Pessimistic column
        pessimisticCol.setCellValueFactory(new PropertyValueFactory<Activity, Integer>("pessimistic"));
        pessimisticCol.setCellFactory(numberCellFactory);
        pessimisticCol.setEditable(false);

        // Bind statistics tab with selected activity
        table.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                pm.setActivityStatsTab(newValue);
                pm.setActivityChartTab(newValue);
            }
        });

        table.setTableMenuButtonVisible(true);
    }

    private void initActivitiesMenu() {
        // Bind Buttons with menu items
        removeMenuItem.disableProperty().bindBidirectional(buttonBarController.deleteButtonDisableProperty());
        ListProperty lstProp = new SimpleListProperty();
        lstProp.bind(table.itemsProperty());
        clearActMenuItem.disableProperty().bind(lstProp.emptyProperty());
        clearDepMenuItem.disableProperty().bind(lstProp.emptyProperty());
        moveUpMenuItem.disableProperty().bindBidirectional(buttonBarController.moveUpButtonDisableProperty());
        moveDownMenuItem.disableProperty().bindBidirectional(buttonBarController.moveDownButtonDisableProperty());
        propertiesMenuItem.disableProperty().bindBidirectional(buttonBarController.propertiesButtonDisableProperty());

        // Menu icons
        addMenuItem.setGraphic(new ImageView(Resources.ADD_16));
        removeMenuItem.setGraphic(new ImageView(Resources.DELETE_16));
        clearActMenuItem.setGraphic(new ImageView(Resources.DELETE_ALL_16));
        clearDepMenuItem.setGraphic(new ImageView(Resources.CLEAR_16));
        moveUpMenuItem.setGraphic(new ImageView(Resources.ARROW_UP_16));
        moveDownMenuItem.setGraphic(new ImageView(Resources.ARROW_DOWN_16));
        propertiesMenuItem.setGraphic(new ImageView(Resources.PROPERTIES_16));
    }

    // Actions
    @FXML
    private void addActivity() {
        pm.addActivity();
    }

    @FXML
    private void delete() {

        pm.delete();
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

    // Clear selections on table
    public void clearSelections() {
        table.getSelectionModel().clearSelection();
    }

    // Custom binding for empty selection check in table
    public BooleanBinding emptySelectionBinding() {
        return Bindings.isEmpty(table.getSelectionModel().getSelectedItems());
    }

    // Table data setter
    public void setTableItems(ObservableList<Activity> data) {
        table.setItems(data);
    }

    // Get selected item
    public Activity getSelectedItem() {
        return table.getSelectionModel().getSelectedItem();
    }

    // Set selection
    public void setSelection(Activity activity) {
        table.getSelectionModel().select(activity);
    }

    // Set focus
    public void setFocus() {
        Platform.runLater(table::requestFocus);
    }

    // Edit added row
    public void focusLastRow() {
        if (!table.getItems().isEmpty()) {
            table.requestFocus();
            Platform.runLater(() -> table.getSelectionModel().select(table.getItems().size() - 1));
        }
    }

    // Getters - Setters
    public BooleanProperty clearDisableProperty() {
        return clearDepMenuItem.disableProperty();
    }
}
