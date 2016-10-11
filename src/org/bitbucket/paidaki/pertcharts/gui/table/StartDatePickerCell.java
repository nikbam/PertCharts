package org.bitbucket.paidaki.pertcharts.gui.table;

import javafx.application.Platform;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableView;
import org.bitbucket.paidaki.pertcharts.application.Activity;
import org.bitbucket.paidaki.pertcharts.Main;
import org.bitbucket.paidaki.pertcharts.application.util.Util;
import org.bitbucket.paidaki.pertcharts.gui.StartDatePicker;

import java.time.LocalDate;

public class StartDatePickerCell extends DatePickerCell {

    public StartDatePickerCell(TableView<Activity> table) {
        super(table);
    }

    @Override
    public void commitEdit(LocalDate item) {
        super.commitEdit(item);

        Platform.runLater(() -> datePicker.getEditor().setText(String.valueOf(item)));

        Activity rowActivity = table.getItems().get(this.getIndex());
        rowActivity.commitStartDate(item);
        rowActivity.fixDates();
        rowActivity.calculateStats();

        if (Main.DEBUGGING) {
            for (int i = 0; i < table.getItems().size(); i++) {
                System.out.println("D" + i + ") Id: " + table.getItems().get(i).getId() + ", Name : " + table.getItems().get(i).getName() + ", Start : " + table.getItems().get(i).getStartDate());
            }
            System.out.println("---------------------------------------------------------------");
        }
    }

    @Override
    protected DatePicker setDatePicker() {
        return new StartDatePicker(table.getItems().get(this.getIndex()), Util.DATE_FORMATTER1);
    }
}
