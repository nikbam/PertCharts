package org.bitbucket.paidaki.pertcharts.gui.tabs.pertcharttab.chart;

import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Glow;

public class DraggableActivityNode extends ActivityNode implements IDraggable {

    private double dx, dy;
    private DropShadow dropShadow;
    private Glow glowEffect;

    public DraggableActivityNode() {
        this.setPadding(new Insets(10, 10, 10, 10));
        addEffects();
        addListeners();
    }

    private void addEffects() {
        this.dropShadow = new DropShadow();
        this.glowEffect = new Glow();
        this.setEffect(dropShadow);
    }

    private void addListeners() {
        this.setOnMouseEntered(event -> {
            this.setCursor(Cursor.HAND);
            dropShadow.setInput(glowEffect);
            onEnter();
        });
        this.setOnMouseExited(event1 -> {
            dropShadow.setInput(null);
            onExit();
        });
        this.setOnMousePressed(e1 -> {
            this.toFront();
            dx = this.getLayoutX() - e1.getSceneX();
            dy = this.getLayoutY() - e1.getSceneY();
            onPressed();
        });
        this.setOnMouseReleased(event -> {
            onReleased();
        });
        this.setOnMouseDragged(e2 -> {
            double newX = e2.getSceneX() + dx;
            double newY = e2.getSceneY() + dy;

            if (newX > 0) {
                this.setLayoutX(newX);
            } else {
                this.setLayoutX(0);
            }
            if (newY > 0) {
                this.setLayoutY(newY);
            } else {
                this.setLayoutY(0);
            }
            onDragged();
        });
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
