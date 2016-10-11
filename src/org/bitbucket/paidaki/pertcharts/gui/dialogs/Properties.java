package org.bitbucket.paidaki.pertcharts.gui.dialogs;

import javafx.application.Platform;
import javafx.beans.property.*;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.geometry.Side;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxListCell;
import javafx.scene.effect.BoxBlur;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;
import javafx.util.Callback;
import javafx.util.converter.NumberStringConverter;
import org.bitbucket.paidaki.pertcharts.application.Activity;
import org.bitbucket.paidaki.pertcharts.application.util.Util;
import org.bitbucket.paidaki.pertcharts.gui.CustomDatePicker;
import org.bitbucket.paidaki.pertcharts.gui.EndDatePicker;
import org.bitbucket.paidaki.pertcharts.gui.NumberTextField;
import org.bitbucket.paidaki.pertcharts.gui.StartDatePicker;

import java.util.ArrayList;

public class Properties extends Stage {

    private TextArea descriptionTextArea;
    private Button okButton, cancelButton;
    private ListView<ListViewData> listView;
    private RadioButton fixedDatesRButton, expectedDatesRButton;
    private TextField nameTextField, durationTextField, mostLikelyTextField, optimisticTextField, pessimisticTextField;
    private CustomDatePicker startDatePicker, endDatePicker;

    private Activity oldActivity;
    private Activity newActivity;
    private ObservableList<Activity> activities;
    private ObservableList<ListViewData> listData;

    public Properties(Activity oldActivity, ObservableList<Activity> data, Stage mainWindow) {
        this.oldActivity = oldActivity;
        this.newActivity = new Activity(oldActivity);
        this.activities = data;

        createWindow(mainWindow);
        initialize();

        this.show();
    }

    private void createWindow(Stage mainWindow) {
        BorderPane container = new BorderPane();
        container.setPrefSize(580, 315);

        container.setRight(createRightPane());
        container.setBottom(createBottomPane());
        container.setCenter(createCenterPane());

        this.setTitle(oldActivity.getId() + ". " + oldActivity.getName() + " - Properties");
        this.initStyle(StageStyle.UTILITY);
        this.initModality(Modality.WINDOW_MODAL);
        this.initOwner(mainWindow);
        Scene scene = new Scene(container);
        scene.getStylesheets().add(this.getClass().getResource("/org/bitbucket/paidaki/pertcharts/gui/css/Style.css").toExternalForm());
        scene.addEventHandler(KeyEvent.KEY_PRESSED, event -> {
            if (event.getCode() == KeyCode.ESCAPE) {
                cancelButton.requestFocus();
                Platform.runLater(cancelButton::fire);
            } else if (event.getCode() == KeyCode.ENTER) {
                okButton.requestFocus();
                Platform.runLater(okButton::fire);
            }
        });
        this.setScene(scene);
        mainWindow.getScene().getRoot().setEffect(new BoxBlur());
        this.setOnCloseRequest(e -> mainWindow.getScene().getRoot().setEffect(null));
        this.setResizable(false);
    }

    private void initialize() {
        setupListView();
        setupListeners();
        setupBindings();
    }

    private void setupListView() {
        listData = createListViewData();
        listView.setItems(listData);

        Callback<ListViewData, ObservableValue<Boolean>> getProperty = ListViewData::selectedProperty;
        Callback<ListView<ListViewData>, ListCell<ListViewData>> forListView = CheckBoxListCell.forListView(getProperty);
        listView.setCellFactory(forListView);
        listView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            newValue.setSelected(!newValue.getSelected());
        });
    }

    private void setupListeners() {
        cancelButton.setOnAction(event -> exit());
        okButton.setOnAction(event -> {
            newActivity.fixDates();
            commitDependencies();
            oldActivity.setActivity(newActivity);
            exit();
        });

        startDatePicker.setOnAction(event -> newActivity.commitStartDate(startDatePicker.getValue()));
        endDatePicker.setOnAction(event -> {
            if (fixedDatesRButton.isSelected() && !durationTextField.isFocused()) {
                newActivity.commitEndDate(endDatePicker.getValue());
            }
        });

        newActivity.durationProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.intValue() > 0) {
                if (fixedDatesRButton.isSelected()) {
                    newActivity.commitDuration(newValue.intValue());
                } else {
                    newActivity.setEndDate(Util.addWorkingDays(newActivity.getStartDate(), newActivity.getDuration() - 1));
                }
            } else {
                newActivity.setDuration(oldValue.intValue());
            }
        });
        newActivity.mostLikelyProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.intValue() > 0) {
                if (expectedDatesRButton.isSelected()) {
                    newActivity.commitExpected();
                }
            } else {
                newActivity.setMostLikely(oldValue.intValue());
            }
        });
        newActivity.optimisticProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.intValue() > 0) {
                if (expectedDatesRButton.isSelected()) {
                    newActivity.commitExpected();
                }
            } else {
                newActivity.setOptimistic(oldValue.intValue());
            }
        });
        newActivity.pessimisticProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.intValue() > 0) {
                if (expectedDatesRButton.isSelected()) {
                    newActivity.commitExpected();
                }
            } else {
                newActivity.setPessimistic(oldValue.intValue());
            }
        });

        durationTextField.focusedProperty().addListener((observable, oldValue, newValue) -> durationTextField.setText(String.valueOf(newActivity.getDuration())));
        mostLikelyTextField.focusedProperty().addListener((observable, oldValue, newValue) -> mostLikelyTextField.setText(String.valueOf(newActivity.getMostLikely())));
        optimisticTextField.focusedProperty().addListener((observable, oldValue, newValue) -> optimisticTextField.setText(String.valueOf(newActivity.getOptimistic())));
        pessimisticTextField.focusedProperty().addListener((observable, oldValue, newValue) -> pessimisticTextField.setText(String.valueOf(newActivity.getPessimistic())));
    }

    private void setupBindings() {
        nameTextField.textProperty().bindBidirectional(newActivity.nameProperty());
        descriptionTextArea.textProperty().bindBidirectional(newActivity.descriptionProperty());
        durationTextField.textProperty().bindBidirectional(newActivity.durationProperty(), new NumberStringConverter());
        mostLikelyTextField.textProperty().bindBidirectional(newActivity.mostLikelyProperty(), new NumberStringConverter());
        optimisticTextField.textProperty().bindBidirectional(newActivity.optimisticProperty(), new NumberStringConverter());
        pessimisticTextField.textProperty().bindBidirectional(newActivity.pessimisticProperty(), new NumberStringConverter());
        startDatePicker.valueProperty().bindBidirectional(newActivity.startDateProperty());
        endDatePicker.valueProperty().bindBidirectional(newActivity.endDateProperty());

        endDatePicker.disableProperty().bind(fixedDatesRButton.selectedProperty().not());
        durationTextField.disableProperty().bind(fixedDatesRButton.selectedProperty().not());

        mostLikelyTextField.disableProperty().bind(expectedDatesRButton.selectedProperty().not());
        optimisticTextField.disableProperty().bind(expectedDatesRButton.selectedProperty().not());
        pessimisticTextField.disableProperty().bind(expectedDatesRButton.selectedProperty().not());
    }

    private ObservableList<ListViewData> createListViewData() {
        ObservableList<ListViewData> data = FXCollections.observableArrayList();
        ArrayList<Activity> successors = new ArrayList<>();
        Util.getAllSuccessors(oldActivity, successors);     // Check for successors loop
        for (Activity a : activities) {
            if (a != oldActivity && !successors.contains(a)) {
                boolean selected = oldActivity.getPredecessors().contains(a);
                ListViewData entry = new ListViewData(a, selected);
                data.add(entry);
            }
        }
        return data;
    }

    private void commitDependencies() {
        newActivity.getPredecessors().clear();
        for (ListViewData ld : listData) {
            if (ld.getSelected()) {
                newActivity.getPredecessors().add(ld.getActivity());
                if (!ld.getActivity().getSuccessors().contains(oldActivity)) {
                    ld.getActivity().getSuccessors().add(oldActivity);
                }
                fixDates(newActivity, ld.getActivity());
            } else {
                newActivity.getPredecessors().remove(ld.getActivity());
                ld.getActivity().getSuccessors().remove(oldActivity);
            }
        }
        newActivity.setDependencies(Util.listToString(newActivity.getPredecessors()));
    }

    private void fixDates(Activity successor, Activity predecessor) {
        if (successor.getStartDate().isEqual(predecessor.getEndDate()) || successor.getStartDate().isBefore(predecessor.getEndDate())) {
            successor.commitStartDate(Util.firstWorkingDate(predecessor.getEndDate().plusDays(1)));
            for (Activity a : successor.getSuccessors()) {
                fixDates(a, successor);
            }
        }
    }

    private void exit() {
        newActivity = null;
        listData = null;
        this.fireEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSE_REQUEST));
    }

    private TabPane createCenterPane() {
        TabPane tabPane = new TabPane();
        Tab properties = new Tab("Properties");
        Tab dependencies = new Tab("Dependencies");

        // Properties tab
        VBox vbox = new VBox();
        GridPane grid = new GridPane();

        Label name = new Label("Name :");
        nameTextField = new TextField();
        grid.add(name, 1, 0);
        grid.add(nameTextField, 2, 0);

        Label startDate = new Label("Starting Date :");
        startDate.setPrefWidth(100);
        startDatePicker = new StartDatePicker(newActivity);
        grid.add(startDate, 1, 1);
        grid.add(startDatePicker, 2, 1);

        Separator separator1 = new Separator(Orientation.HORIZONTAL);
        grid.add(separator1, 0, 2, 3, 1);

        fixedDatesRButton = new RadioButton();
        fixedDatesRButton.setSelected(true);
        grid.add(fixedDatesRButton, 0, 3);

        Label endDate = new Label("End Date :");
        endDatePicker = new EndDatePicker(newActivity);
        grid.add(endDate, 1, 3);
        grid.add(endDatePicker, 2, 3);

        Label duration = new Label("Duration :");
        durationTextField = new NumberTextField();
        grid.add(duration, 1, 4);
        grid.add(durationTextField, 2, 4);

        Separator separator2 = new Separator(Orientation.HORIZONTAL);
        grid.add(separator2, 0, 5, 3, 1);

        Label optimistic = new Label("Optimistic :");
        optimisticTextField = new NumberTextField();
        grid.add(optimistic, 1, 6);
        grid.add(optimisticTextField, 2, 6);

        expectedDatesRButton = new RadioButton();
        grid.add(expectedDatesRButton, 0, 7);

        Label mostLikely = new Label("Most Likely :");
        mostLikely.setPrefWidth(100);
        mostLikelyTextField = new NumberTextField();
        grid.add(mostLikely, 1, 7);
        grid.add(mostLikelyTextField, 2, 7);

        Label pessimistic = new Label("Pessimistic :");
        pessimisticTextField = new NumberTextField();
        grid.add(pessimistic, 1, 8);
        grid.add(pessimisticTextField, 2, 8);

        ToggleGroup toggleGroup = new ToggleGroup();
        fixedDatesRButton.setToggleGroup(toggleGroup);
        expectedDatesRButton.setToggleGroup(toggleGroup);

        vbox.setPadding(new Insets(10, 5, 10, 10));
        vbox.setAlignment(Pos.TOP_CENTER);
        vbox.getChildren().addAll(grid);
        grid.setVgap(10);
        grid.setHgap(10);
        properties.setContent(vbox);

        // Dependencies tab
        StackPane stackPane = new StackPane();
        listView = new ListView<>();
        listView.setEditable(true);

        StackPane.setMargin(listView, new Insets(10, 5, 10, 10));
        listView.setPadding(new Insets(10, 5, 10, 10));
        stackPane.getChildren().add(listView);
        dependencies.setContent(stackPane);

        tabPane.setSide(Side.LEFT);
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        tabPane.getTabs().addAll(properties, dependencies);
        return tabPane;
    }

    private VBox createRightPane() {
        VBox vbox = new VBox();
        Label title = new Label("Description");
        descriptionTextArea = new TextArea();
        descriptionTextArea.setWrapText(true);
        descriptionTextArea.setText(newActivity.getDescription());

        vbox.setAlignment(Pos.CENTER);
        BorderPane.setMargin(vbox, new Insets(10, 10, 10, 5));
        title.setPadding(new Insets(0, 5, 5, 5));
        descriptionTextArea.setPrefWidth(250);
        VBox.setVgrow(descriptionTextArea, Priority.ALWAYS);

        title.setFont(Font.font("System", FontPosture.ITALIC, 16));

        vbox.getChildren().addAll(title, descriptionTextArea);
        return vbox;
    }

    private HBox createBottomPane() {
        HBox hbox = new HBox();
        okButton = new Button("OK");
        cancelButton = new Button("Cancel");

        hbox.setAlignment(Pos.BOTTOM_RIGHT);
        BorderPane.setMargin(hbox, new Insets(0, 10, 10, 10));
        hbox.setSpacing(10);

        okButton.setPrefWidth(80);
        cancelButton.setPrefWidth(80);

        hbox.getChildren().addAll(okButton, cancelButton);
        return hbox;
    }

    private class ListViewData {
        private BooleanProperty selected;
        private IntegerProperty id;
        private StringProperty name;
        private Activity activity;

        public ListViewData(Activity activity, boolean selected) {
            this.activity = activity;
            this.id = new SimpleIntegerProperty(activity.getId());
            this.name = new SimpleStringProperty(activity.getName());
            this.selected = new SimpleBooleanProperty(selected);
        }

        public Activity getActivity() {
            return activity;
        }

        public int getId() {
            return id.get();
        }

        public void setId(int id) {
            this.id.set(id);
        }

        public boolean getSelected() {
            return selected.get();
        }

        public BooleanProperty selectedProperty() {
            return selected;
        }

        public void setSelected(boolean selected) {
            this.selected.set(selected);
        }

        @Override
        public String toString() {
            return id.get() + ". " + name.get();
        }
    }
}
