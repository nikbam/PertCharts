package org.bitbucket.paidaki.pertcharts.gui.tabs.pertcharttab.chart;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Ellipse;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;

public class EllipseNode extends DraggableChartNode {

    private static final double RADIUS_X = 35;
    private static final double RADIUS_Y = 20;

    private Label textLabel, dateLabel;
    private Ellipse ellipse;

    public EllipseNode() {
        VBox container = new VBox(5);
        container.setAlignment(Pos.CENTER);

        StackPane ovalContainer = new StackPane();
        ovalContainer.setAlignment(Pos.CENTER);

        createDateLabel();
        createTextLabel();
        createOvalContainer();

        ovalContainer.getChildren().addAll(ellipse, textLabel);
        container.getChildren().addAll(dateLabel, ovalContainer);

        this.getCenter().setyOffset(12.5);  // Fix center //TODO Make dynamic
        this.setAlignment(Pos.CENTER);
        this.getChildren().add(container);
    }

    private void createOvalContainer() {
        ellipse = new Ellipse(RADIUS_X, RADIUS_Y);
        ellipse.setFill(Color.AZURE);
    }

    private void createTextLabel() {
        textLabel = new Label();
        textLabel.setFont(Font.font("System", FontWeight.BOLD, 14));
        textLabel.setStyle("-fx-text-fill: cornsilk");
    }

    private void createDateLabel() {
        dateLabel = new Label();
        dateLabel.setFont(Font.font("System", FontPosture.ITALIC, 14));
        dateLabel.setStyle("-fx-text-fill: black");
    }

    public Label getDateLabel() {
        return dateLabel;
    }

    public void setDateLabel(Label dateLabel) {
        this.dateLabel = dateLabel;
    }

    public Label getTextLabel() {
        return textLabel;
    }

    public void setTextLabel(Label textLabel) {
        this.textLabel = textLabel;
    }

    public Ellipse getEllipse() {
        return ellipse;
    }

    public void setEllipse(Ellipse ellipse) {
        this.ellipse = ellipse;
    }

    @Override
    public void onEnter() {
    }

    @Override
    public void onExit() {
    }

    @Override
    public void onPressed() {
    }

    @Override
    public void onReleased() {
    }

    @Override
    public void onDragged() {
    }
}
