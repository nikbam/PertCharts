package org.bitbucket.paidaki.pertcharts.gui.tabs.pertcharttab.chart;

import javafx.scene.layout.StackPane;

import java.awt.*;

public class ChartNode extends StackPane {

    private Center center;
    private int graphDepth;

    public ChartNode() {
        this.center = new Center(this);
        this.graphDepth = 0;
    }

    public void setLocation(Point location) {
        this.setLayoutX(location.getX());
        this.setLayoutY(location.getY());
    }

    public void setLocation(double x, double y) {
        this.setLayoutX(x);
        this.setLayoutY(y);
    }

    public ConnectingLine connect(ChartNode node) {
        return new ConnectingLine(this, node);
    }

    public void select() {
        this.getStyleClass().add("selectedNode");
    }

    public void deselect() {
        this.getStyleClass().remove("selectedNode");
    }

    public Center getCenter() {
        return center;
    }

    public int getGraphDepth() {
        return graphDepth;
    }

    public void setGraphDepth(int graphDepth) {
        this.graphDepth = graphDepth;
    }
}
