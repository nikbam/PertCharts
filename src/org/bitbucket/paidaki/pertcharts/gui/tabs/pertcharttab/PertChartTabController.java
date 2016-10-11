package org.bitbucket.paidaki.pertcharts.gui.tabs.pertcharttab;

import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import org.bitbucket.paidaki.pertcharts.Main;
import org.bitbucket.paidaki.pertcharts.application.ProjectManager;
import org.bitbucket.paidaki.pertcharts.application.util.Resources;
import org.bitbucket.paidaki.pertcharts.application.util.Util;
import org.bitbucket.paidaki.pertcharts.gui.IncludedWindow;
import org.bitbucket.paidaki.pertcharts.gui.MainWindowController;
import org.bitbucket.paidaki.pertcharts.gui.menubar.MyMenuBarController;
import org.bitbucket.paidaki.pertcharts.gui.table.MyTableViewController;

public class PertChartTabController implements IncludedWindow {

    private ProjectManager pm;
    private MainWindowController mainWindowController;
    private MyMenuBarController menuBarController;
    private MyTableViewController tableViewController;

    @FXML
    private VBox toolboxContainer;
    @FXML
    private Button showToolboxButton, hideToolboxButton;
    @FXML
    private Pane canvas;
    @FXML
    private ScrollPane canvasContainer;
    @FXML
    private Button nodeTool, arrowTool, testTool;
    @FXML
    private TitledPane shapeTPane, activityTPane, nodeTPane, arrowTPane, canvasTPane;
    @FXML
    private TextField canvasWidthTField, canvasHeightTField;
    @FXML
    private ColorPicker colorPicker;
    @FXML
    private CheckBox autoArrangeCheckBox;
    @FXML
    private MenuItem addMenuItem, deleteMenuItem, clearChartMenuItem, clearDepMenuItem, autoArrangeMenuItem, exportToImageMenuItem, propertiesMenuItem;

    @FXML
    private void initialize() {
        pm = ProjectManager.getInstance();

        setupToolbox();
        setupCanvas();

        testTool.setOnAction(event1 -> {
            //TODO
        });

        nodeTool.setOnAction(event1 -> {
            //TODO
        });

        arrowTool.setOnAction(event -> {
            //TODO
        });

        // Log event
        if (Main.DEBUGGING) System.out.println(this.getClass().getSimpleName() + ": Initialized");
    }

    private void setupCanvas() {
        // Initiate canvas
        canvas.setPrefSize(2000, 2000);
        canvas.minWidthProperty().bind(Bindings.subtract(canvasContainer.widthProperty(), 2));
        canvas.minHeightProperty().bind(Bindings.subtract(canvasContainer.heightProperty(), 2));
        canvasWidthTField.textProperty().bindBidirectional(canvas.prefWidthProperty(), Util.NUMBER_STRING_CONVERTER);
        canvasWidthTField.focusedProperty().addListener(observable -> canvasWidthTField.setText(Util.NUMBER_STRING_CONVERTER.toString(canvas.getWidth())));
        canvasHeightTField.textProperty().bindBidirectional(canvas.prefHeightProperty(), Util.NUMBER_STRING_CONVERTER);
        canvasHeightTField.focusedProperty().addListener(observable -> canvasHeightTField.setText(Util.NUMBER_STRING_CONVERTER.toString(canvas.getHeight())));
        colorPicker.setValue(Color.CORNSILK);
        colorPicker.setOnAction(event -> {
            canvas.setStyle("-fx-background-color: " + colorPicker.getValue().toString().replaceFirst("0x", "#"));
        });

        // Canvas Context Menu Icons
        addMenuItem.setGraphic(new ImageView(Resources.ADD_16));
        deleteMenuItem.setGraphic(new ImageView(Resources.DELETE_16));
        clearChartMenuItem.setGraphic(new ImageView(Resources.DELETE_ALL_16));
        clearDepMenuItem.setGraphic(new ImageView(Resources.CLEAR_16));
        autoArrangeMenuItem.setGraphic(new ImageView(Resources.AUTO_ARRANGE_16));
        exportToImageMenuItem.setGraphic(new ImageView(Resources.IMAGE_PNG_16));
        propertiesMenuItem.setGraphic(new ImageView(Resources.PROPERTIES_16));
    }

    private void setupToolbox() {
        // Toolbox Icons
        nodeTool.setGraphic(new ImageView(Resources.ELLIPSE_32));
        arrowTool.setGraphic(new ImageView(Resources.SQUARE_32));
        testTool.setGraphic(new ImageView(Resources.ARROW_32));

        // Fix Toolbox items
        activityTPane.managedProperty().bind(activityTPane.visibleProperty());
        nodeTPane.managedProperty().bind(nodeTPane.visibleProperty());
        arrowTPane.managedProperty().bind(arrowTPane.visibleProperty());

        // Bind show/hide button with Toolbox
        toolboxContainer.managedProperty().bind(toolboxContainer.visibleProperty());
        hideToolboxButton.visibleProperty().bind(toolboxContainer.visibleProperty());
        showToolboxButton.visibleProperty().bind(toolboxContainer.visibleProperty().not());
        hideToolboxButton.setOnAction(event -> {
            toolboxContainer.setVisible(false);
            tableViewController.setFocus();
        });
        showToolboxButton.setOnAction(event -> toolboxContainer.setVisible(true));
    }

    @Override
    public void injectMainController(MainWindowController mainWindowController) {
        this.mainWindowController = mainWindowController;
        setup();
    }

    private void setup() {
        // Get controllers
        menuBarController = mainWindowController.getMenuBarController();
        tableViewController = mainWindowController.getTableViewController();

        // Bind show/hide button with toolbox check MenuItem
        toolboxContainer.visibleProperty().bindBidirectional(menuBarController.toolboxCMenuItemSelectedProperty());

        // Context menu bindings
        addMenuItem.disableProperty().bind(menuBarController.addDisableProperty());
        deleteMenuItem.disableProperty().bind(menuBarController.deleteDisableProperty());
        clearChartMenuItem.disableProperty().bind(menuBarController.clearDisableProperty());
        clearDepMenuItem.disableProperty().bind(menuBarController.clearDepDisableProperty());
        propertiesMenuItem.disableProperty().bind(menuBarController.propertiesDisableProperty());

        // Log event
        if (Main.DEBUGGING) System.out.println(this.getClass().getSimpleName() + ": Setup done");
    }

    public Pane getCanvas() {
        return canvas;
    }

    public BooleanProperty autoArrangeProperty() {
        return autoArrangeCheckBox.selectedProperty();
    }

    // Actions
    @FXML
    private void autoArrange() {
        pm.autoArrangeChart();
    }

    @FXML
    private void clearChart() {
        pm.clearActivities();
    }

    @FXML
    private void clearDependencies() {
        pm.clearDependencies();
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
    private void activityProperties() {
        pm.activityProperties();
    }

    @FXML
    private void exportToImage() {
        pm.exportChartToImage();
    }
}
