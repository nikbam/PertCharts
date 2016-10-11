package org.bitbucket.paidaki.pertcharts.gui.table;

import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import org.bitbucket.paidaki.pertcharts.application.Activity;
import org.bitbucket.paidaki.pertcharts.application.util.Util;

import java.time.LocalDate;

public abstract class DatePickerCell extends TableCell<Activity, LocalDate> {

    protected DatePicker datePicker;
    protected TableView<Activity> table;
    protected EventHandler<MouseEvent> datePickerMouseEvent;

    public DatePickerCell(TableView<Activity> table) {
        this.table = table;
    }

    @Override
    public void commitEdit(LocalDate item) {
        super.commitEdit(item);
    }

    @Override
    public void startEdit() {
        super.startEdit();

        createDatePicker();
        setGraphic(datePicker);
        setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        Platform.runLater(datePicker.getEditor()::requestFocus);
    }

    @Override
    public void cancelEdit() {
        super.cancelEdit();

        setContentDisplay(ContentDisplay.TEXT_ONLY);
        datePicker.setValue(getCellValue());
    }

    @Override
    public void updateItem(LocalDate item, boolean empty) {
        super.updateItem(item, empty);

        if (empty) {
            setText(null);
            setGraphic(null);
        } else {
            if (isEditing()) {
                if (datePicker != null) {
                    datePicker.setValue(getCellValue());
                }
                setGraphic(datePicker);
                setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
            } else {
                setText(getCellValue().format(Util.DATE_FORMATTER1));
                setContentDisplay(ContentDisplay.TEXT_ONLY);
            }
        }
    }

    private void createDatePicker() {
        datePicker = setDatePicker();
        datePicker.getEditor().setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                commitEdit(datePicker.getConverter().fromString(datePicker.getEditor().getText()));
                setContentDisplay(ContentDisplay.TEXT_ONLY);
            } else if (event.getCode() == KeyCode.ESCAPE) {
                cancelEdit();
            }
        });
        datePicker.setOnAction(event -> {
            commitEdit(datePicker.getValue());
            Platform.runLater(table::requestFocus);
        });
        datePicker.setMinWidth(this.getWidth() - this.getGraphicTextGap() * 2);
        datePicker.getEditor().setAlignment(Pos.CENTER);

        table.getScene().addEventFilter(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                if (isEditing() && !datePicker.localToScene(datePicker.getBoundsInLocal()).contains(mouseEvent.getX(), mouseEvent.getY())) {
                    commitEdit(datePicker.getConverter().fromString(datePicker.getEditor().getText()));
                    setContentDisplay(ContentDisplay.TEXT_ONLY);
                }
            }
        });
    }

    protected abstract DatePicker setDatePicker();

    private LocalDate getCellValue() {
        return getItem() == null ? LocalDate.now() : getItem();
    }
}
