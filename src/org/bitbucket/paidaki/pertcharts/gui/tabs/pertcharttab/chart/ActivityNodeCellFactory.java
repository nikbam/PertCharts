package org.bitbucket.paidaki.pertcharts.gui.tabs.pertcharttab.chart;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;

public class ActivityNodeCellFactory {

    public static final Font DEFAULT_FONT = Font.font("System", 11);
    public static final Font DEFAULT_FONT_BOLD = Font.font("System", FontWeight.BOLD, 11);
    public static final Font DEFAULT_FONT_ITALIC = Font.font("System", FontPosture.ITALIC, 11);
    public static final String DEFAULT_STYLE = "-fx-border-color: #272727; -fx-border-width: 2; -fx-background-color: cornsilk";
    public static final String DEFAULT_UNUSED_STYLE = "-fx-background-color: #272727";

    private ActivityNodeCellFactory() {
    }

    public static ActivityNodeCell getChartNodeCell() {
        return new ActivityNodeCell();
    }

    public static ActivityNodeCell getChartNodeCell(String text) {
        return new ActivityNodeCell(text);
    }

    public static ActivityNodeCell getStyledChartNodeCell() {
        ActivityNodeCell cell = getChartNodeCell();
        cell.setStyle(DEFAULT_STYLE);

        Label label = cell.getLabel();
        label.setPadding(new Insets(5, 5, 5, 5));
        label.setFont(DEFAULT_FONT);

        return cell;
    }

    public static ActivityNodeCell getStyledChartNodeCell(String text) {
        ActivityNodeCell cell = getChartNodeCell(text);
        cell.setStyle(DEFAULT_STYLE);

        Label label = cell.getLabel();
        label.setPadding(new Insets(5, 5, 5, 5));
        label.setFont(DEFAULT_FONT);

        return cell;
    }

    public static ActivityNodeCell getUnusedChartNodeCell() {
        ActivityNodeCell cell = getChartNodeCell();
        cell.setStyle(DEFAULT_UNUSED_STYLE);

        return cell;
    }
}
