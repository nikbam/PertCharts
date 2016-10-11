package org.bitbucket.paidaki.pertcharts.gui.tabs.pertcharttab.chart;

import javafx.scene.paint.Color;

public class CriticalLine extends ConnectingLine {

    public CriticalLine(ChartNode startNode, ChartNode endNode) {
        super(startNode, endNode);

        this.setStroke(Color.RED);
        this.setStrokeWidth(3);
        this.getArrow().setFill(Color.RED);
    }
}
