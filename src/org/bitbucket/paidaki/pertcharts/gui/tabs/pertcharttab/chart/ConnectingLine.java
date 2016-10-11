package org.bitbucket.paidaki.pertcharts.gui.tabs.pertcharttab.chart;

import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.StrokeLineCap;

public class ConnectingLine extends Line {

    private ChartNode startNode, endNode;
    private Center center;
    private Arrow arrow;

    public ConnectingLine(ChartNode startNode, ChartNode endNode) {
        this.startNode = startNode;
        this.endNode = endNode;
        this.center = new Center(this);
        this.arrow = new Arrow(this);

        init();
        arrow.update();
    }

    private void init() {
        // Bind line to start and end nodes
        this.startXProperty().bind(startNode.getCenter().centerXProperty());
        this.startYProperty().bind(startNode.getCenter().centerYProperty());
        this.endXProperty().bind(endNode.getCenter().centerXProperty());
        this.endYProperty().bind(endNode.getCenter().centerYProperty());

        // Bind arrow to the middle of the line
        this.startXProperty().addListener(observable -> arrow.update());
        this.startYProperty().addListener(observable -> arrow.update());
        this.endXProperty().addListener(observable -> arrow.update());
        this.endYProperty().addListener(observable -> arrow.update());

        this.setStrokeWidth(2);
        this.setStroke(Color.valueOf("#5f8dcd"));
        this.setStrokeLineCap(StrokeLineCap.BUTT);
        this.setMouseTransparent(true);
        this.getArrow().setFill(Color.valueOf("#5f8dcd"));
    }

    public ChartNode getStartNode() {
        return startNode;
    }

    public void setStartNode(ChartNode startNode) {
        this.startNode = startNode;
        this.startXProperty().unbind();
        this.startYProperty().unbind();
        this.startXProperty().bind(startNode.getCenter().centerXProperty());
        this.startYProperty().bind(startNode.getCenter().centerYProperty());
    }

    public ChartNode getEndNode() {
        return endNode;
    }

    public void setEndNode(ChartNode endNode) {
        this.endNode = endNode;
        this.endXProperty().unbind();
        this.endYProperty().unbind();
        this.endXProperty().bind(endNode.getCenter().centerXProperty());
        this.endYProperty().bind(endNode.getCenter().centerYProperty());
    }

    public Center getCenter() {
        return center;
    }

    public Arrow getArrow() {
        return arrow;
    }
}
