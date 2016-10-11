package org.bitbucket.paidaki.pertcharts.gui.tabs.pertcharttab.chart;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;

public class ActivityNodeCell extends StackPane {

    private Label label;

    public ActivityNodeCell() {
        this("");
    }

    public ActivityNodeCell(String text) {
        this.label = new Label(text);

        this.setAlignment(Pos.CENTER);
        this.getChildren().add(label);
    }

    public Label getLabel() {
        return label;
    }

    public void setLabelText(String text) {
        label.setText(text);
    }
}
