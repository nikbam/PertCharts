package org.bitbucket.paidaki.pertcharts.gui.table;

import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import org.bitbucket.paidaki.pertcharts.application.Activity;
import org.bitbucket.paidaki.pertcharts.Main;

public class TextFieldCell extends TableCell<Activity, String> {

    private TextField textField;
    private KeyCode keyPressed;
    private TableView<Activity> table;

    public TextFieldCell(TableView<Activity> table) {
        this.table = table;
    }

    @Override
    public void commitEdit(String item) {
        super.commitEdit(item);

        Platform.runLater(() -> textField.setText(item));
        table.getItems().get(this.getIndex()).setName(item);

        if (Main.DEBUGGING) {
            for (int i = 0; i < table.getItems().size(); i++) {
                System.out.println("T" + i + ") Id: " + table.getItems().get(i).getId() + ", Name : " + table.getItems().get(i).getName() + ", Start : " + table.getItems().get(i).getStartDate());
            }
            System.out.println("---------------------------------------------------------------");
        }
    }

    @Override
    public void startEdit() {
        super.startEdit();

        if (textField == null) {
            createTextField();
        } else {
            textField.setText(getCellValue());
        }
        setGraphic(textField);
        setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        Platform.runLater(textField::requestFocus);
    }

    @Override
    public void cancelEdit() {
        super.cancelEdit();

        setContentDisplay(ContentDisplay.TEXT_ONLY);
        textField.setText(getCellValue());
    }

    @Override
    public void updateItem(String item, boolean empty) {
        super.updateItem(item, empty);

        if (empty) {
            setText(null);
            setGraphic(null);
        } else {
            if (isEditing()) {
                if (textField != null) {
                    textField.setText(getCellValue());
                }
                setGraphic(textField);
                setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
            } else {
                setText(getCellValue());
                setContentDisplay(ContentDisplay.TEXT_ONLY);
            }
        }
    }

    private void createTextField() {
        textField = new TextField(getCellValue());
        textField.setMinWidth(this.getWidth() - this.getGraphicTextGap() * 2);
        textField.setOnKeyPressed(event -> {
            keyPressed = event.getCode();
            if (event.getCode() == KeyCode.ENTER) {
                this.getParent().requestFocus();
            }
        });
        textField.focusedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
            if (!newValue) {
                if (keyPressed == KeyCode.ESCAPE || textField.getText().isEmpty()) {
                    cancelEdit();
                } else {
                    commitEdit(textField.getText());
                }
                setContentDisplay(ContentDisplay.TEXT_ONLY);
            }
        });
        table.getScene().addEventFilter(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                if (isEditing() && !textField.localToScene(textField.getBoundsInLocal()).contains(mouseEvent.getX(), mouseEvent.getY())) {
                    if (keyPressed == KeyCode.ESCAPE || textField.getText().isEmpty()) {
                        cancelEdit();
                    } else {
                        commitEdit(textField.getText());
                    }
                    setContentDisplay(ContentDisplay.TEXT_ONLY);
                }
            }
        });
    }

    private String getCellValue() {
        return getItem() == null ? "" : getItem();
    }
}
