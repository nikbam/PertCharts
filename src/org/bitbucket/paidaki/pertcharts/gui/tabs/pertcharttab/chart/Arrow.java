package org.bitbucket.paidaki.pertcharts.gui.tabs.pertcharttab.chart;

import javafx.geometry.Point2D;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import javafx.scene.transform.Rotate;

public class Arrow extends Polygon {

    private ConnectingLine line;
    private Rotate rz;

    public Arrow(ConnectingLine line) {
        super(0, 0, 6, 15, -6, 15);
        this.line = line;

        init();
    }

    private void init() {
        rz = new Rotate();
        {
            rz.setAxis(Rotate.Z_AXIS);
        }

        this.getTransforms().add(rz);
        this.layoutXProperty().bind(line.getCenter().centerXProperty());
        this.layoutYProperty().bind(line.getCenter().centerYProperty());
    }

    public void update() {
        Point2D tan = evalDt(line, 0.5f).normalize();
        double angle = Math.atan2(tan.getY(), tan.getX());
        angle = Math.toDegrees(angle);

        rz.setAngle(angle + 90);
    }

    private Point2D evalDt(Line c, float t) {
        return new Point2D((-3 * Math.pow(1 - t, 2) * c.getStartX()) + (3 * Math.pow(t, 2) * c.getEndX()), -3 * Math.pow(1 - t, 2) * c.getStartY() + 3 * Math.pow(t, 2) * c.getEndY());
    }
}