package org.bitbucket.paidaki.pertcharts.gui.tabs.pertcharttab.chart;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class Legend extends VBox {

    private Label title;
    private ActivityNode node;

    public Legend() {
        createTitle();
        createChartNode();

        this.setSpacing(3);
        setMargin(title, new Insets(0, 5, 0, 5));
        setMargin(node, new Insets(0, 5, 0, 5));
        this.setAlignment(Pos.CENTER);
        this.getChildren().addAll(title, node);
    }

    private void createTitle() {
        this.title = new Label("LEGEND");
        this.title.setFont(Font.font("System", FontWeight.BOLD, 16));
        this.title.setStyle("-fx-text-fill: #272727");
    }

    private void createChartNode() {
        this.node = new ActivityNode();

        node.setCellText(0, 0, "ES");
        node.getCell(0, 0).getLabel().setStyle("-fx-text-fill: green");

        node.setCellText(0, 1, "ID");
        node.getCell(0, 1).getLabel().setFont(ActivityNodeCellFactory.DEFAULT_FONT_BOLD);
        node.getCell(0, 1).getLabel().setStyle("-fx-underline: true");

        node.setCellText(0, 2, "EF");
        node.getCell(0, 2).getLabel().setStyle("-fx-text-fill: green");

        node.setCellText(1, 0, "SL");
        node.getCell(1, 0).getLabel().setStyle("-fx-text-fill: red");

        node.setUnusedCell(1, 1);

        node.setCellText(1, 2, "SL");
        node.getCell(1, 2).getLabel().setStyle("-fx-text-fill: red");

        node.setCellText(2, 0, "LS");
        node.getCell(2, 0).getLabel().setStyle("-fx-text-fill: blue");

        node.setCellText(2, 1, "DUR");
        node.getCell(2, 1).getLabel().setFont(ActivityNodeCellFactory.DEFAULT_FONT_ITALIC);

        node.setCellText(2, 2, "LF");
        node.getCell(2, 2).getLabel().setStyle("-fx-text-fill: blue");
    }
}