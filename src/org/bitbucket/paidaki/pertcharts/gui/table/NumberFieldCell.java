package org.bitbucket.paidaki.pertcharts.gui.table;

import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import org.bitbucket.paidaki.pertcharts.application.Activity;
import org.bitbucket.paidaki.pertcharts.gui.NumberTextField;

public class NumberFieldCell extends TableCell<Activity, Integer> {

    private TextField numberTextField;
    private KeyCode keyPressed;
    private TableView<Activity> table;

    public NumberFieldCell(TableView<Activity> table) {
        this.table = table;
    }

    @Override
    public void commitEdit(Integer item) {
        super.commitEdit(item);

        Platform.runLater(() -> numberTextField.setText(String.valueOf(item)));

        Activity rowActivity = table.getItems().get(this.getIndex());
        rowActivity.commitDuration(item);
        rowActivity.fixDates();
        rowActivity.calculateStats();
    }

    @Override
    public void startEdit() {
        super.startEdit();

        if (numberTextField == null) {
            createNumberField();
        } else {
            numberTextField.setText(getCellValue());
        }
        setGraphic(numberTextField);
        setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        Platform.runLater(numberTextField::requestFocus);
    }

    @Override
    public void cancelEdit() {
        super.cancelEdit();

        setContentDisplay(ContentDisplay.TEXT_ONLY);
        numberTextField.setText(getCellValue());
    }

    @Override
    public void updateItem(Integer item, boolean empty) {
        super.updateItem(item, empty);

        if (empty) {
            setText(null);
            setGraphic(null);
        } else {
            if (isEditing()) {
                if (numberTextField != null) {
                    numberTextField.setText(getCellValue());
                }
                setGraphic(numberTextField);
                setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
            } else {
                setText(getCellValue());
                setContentDisplay(ContentDisplay.TEXT_ONLY);
            }
        }
    }

    private void createNumberField() {
        numberTextField = new NumberTextField(getCellValue());
        numberTextField.setMinWidth(this.getWidth() - this.getGraphicTextGap() * 2);
        numberTextField.setOnKeyPressed(event -> {
            keyPressed = event.getCode();
            if (event.getCode() == KeyCode.ENTER) {
                this.getParent().requestFocus();
            }
        });
        numberTextField.focusedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
            if (!newValue) {
                if (keyPressed == KeyCode.ESCAPE || numberTextField.getText().isEmpty()) {
                    cancelEdit();
                } else {
                    int value = Integer.parseInt(numberTextField.getText());
                    if (value > 0) {
                        commitEdit(value);
                    } else {
                        cancelEdit();
                    }
                }
                setContentDisplay(ContentDisplay.TEXT_ONLY);
            }
        });
        numberTextField.setAlignment(Pos.CENTER);

        table.getScene().addEventFilter(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                if (isEditing() && !numberTextField.localToScene(numberTextField.getBoundsInLocal()).contains(mouseEvent.getX(), mouseEvent.getY())) {
                    if (keyPressed == KeyCode.ESCAPE || numberTextField.getText().isEmpty()) {
                        cancelEdit();
                    } else {
                        int value = Integer.parseInt(numberTextField.getText());
                        if (value > 0) {
                            commitEdit(value);
                        } else {
                            cancelEdit();
                        }
                    }
                    setContentDisplay(ContentDisplay.TEXT_ONLY);
                }
            }
        });
    }

    private String getCellValue() {
        return String.valueOf(getItem());
    }
}
