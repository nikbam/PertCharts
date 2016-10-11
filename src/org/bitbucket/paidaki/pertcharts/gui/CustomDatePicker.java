package org.bitbucket.paidaki.pertcharts.gui;

import javafx.application.Platform;
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.util.Callback;
import javafx.util.StringConverter;
import org.bitbucket.paidaki.pertcharts.application.util.Util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class CustomDatePicker extends DatePicker {

    public CustomDatePicker() {
        this(Util.DATE_FORMATTER2);
    }

    public CustomDatePicker(DateTimeFormatter dateFormatter) {
        StringConverter<LocalDate> converter = new StringConverter<LocalDate>() {
            @Override
            public String toString(LocalDate date) {
                return Util.dateToString(date, dateFormatter);
            }

            @Override
            public LocalDate fromString(String string) {
                LocalDate date = Util.validateDate(string, dateFormatter);
                return date == null || !dateIsAllowed(date) ? CustomDatePicker.this.getValue() : date;
            }
        };
        Callback<DatePicker, DateCell> dayCellFactory = dp -> new DateCell() {
            @Override
            public void updateItem(LocalDate item, boolean empty) {
                super.updateItem(item, empty);

                this.getStyleClass().remove("cellBetweenDates");
                this.getStyleClass().remove("cellDisabled");

                if (!dateIsAllowed(item)) {
                    this.getStyleClass().add("cellDisabled");
                    Platform.runLater(() -> setDisable(true));
                } else {
                    if (dateIsBetweenStartEnd(item)) {
                        this.getStyleClass().add("cellBetweenDates");
                    }
                }
            }
        };
        this.setConverter(converter);
        this.setPromptText("dd/MM/yyyy");
        this.setDayCellFactory(dayCellFactory);
        this.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null) {
                this.setValue(oldValue);
            }
        });
    }

    protected boolean dateIsAllowed(LocalDate testDate) {
        return Util.validateDate(testDate);
    }

    protected boolean dateIsBetweenStartEnd(LocalDate testDate) {
        return false;
    }
}
