package org.bitbucket.paidaki.pertcharts.gui.table;

import javafx.application.Platform;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableView;
import org.bitbucket.paidaki.pertcharts.application.Activity;
import org.bitbucket.paidaki.pertcharts.Main;
import org.bitbucket.paidaki.pertcharts.application.util.Util;
import org.bitbucket.paidaki.pertcharts.gui.EndDatePicker;

import java.time.LocalDate;

public class EndDatePickerCell extends DatePickerCell {

    public EndDatePickerCell(TableView<Activity> table) {
        super(table);
    }

    @Override
    public void commitEdit(LocalDate item) {
        super.commitEdit(item);

        Platform.runLater(() -> datePicker.getEditor().setText(String.valueOf(item)));

        Activity rowActivity = table.getItems().get(this.getIndex());
        rowActivity.commitEndDate(item);
        rowActivity.fixDates();
        rowActivity.calculateStats();

        if (Main.DEBUGGING) {
            for (int i = 0; i < table.getItems().size(); i++) {
                System.out.println(i + " : " + table.getItems().get(i).getEndDate());
            }
            System.out.println("---------------------------------------------------------------");
        }
    }

    @Override
    protected DatePicker setDatePicker() {
        return new EndDatePicker(table.getItems().get(this.getIndex()), Util.DATE_FORMATTER1);
    }
}
