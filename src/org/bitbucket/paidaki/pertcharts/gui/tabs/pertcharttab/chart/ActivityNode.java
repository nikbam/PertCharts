package org.bitbucket.paidaki.pertcharts.gui.tabs.pertcharttab.chart;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.Pos;
import javafx.scene.layout.TilePane;

public class ActivityNode extends ChartNode {

    private static final double CELL_SIZE = 30;
    private static final double GAP = CELL_SIZE / 15;
    private static final int NUM_ROWS = 3;
    private static final int NUM_COLS = 3;
    private BooleanProperty isCritical;

    private TilePane grid;

    public ActivityNode() {
        createGrid();

        this.isCritical = new SimpleBooleanProperty(false);
        this.isCritical.addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                grid.getStyleClass().add("criticalNode");
            } else {
                grid.getStyleClass().remove("criticalNode");
            }
        });
        this.setAlignment(Pos.CENTER);
        this.getChildren().add(grid);
    }

    private void createGrid() {
        grid = new TilePane();
        grid.setHgap(GAP);
        grid.setVgap(GAP);
        grid.setPrefColumns(NUM_COLS);
        grid.setPrefRows(NUM_ROWS);
        createCells();
    }

    private void createCells() {
        grid.getChildren().clear();
        for (int i = 0; i < NUM_ROWS; i++) {
            for (int j = 0; j < NUM_COLS; j++) {
                ActivityNodeCell cell = ActivityNodeCellFactory.getStyledChartNodeCell(String.valueOf(i + ", " + j));
                cell.setMinWidth(CELL_SIZE);
                grid.getChildren().add(cell);
            }
        }
    }

    public void setCellText(int row, int col, String text) {
        int index = row * NUM_COLS + col;
        if (index < 0 || index > grid.getChildren().size() - 1) {
            return;
        }
        ActivityNodeCell cell = (ActivityNodeCell) grid.getChildren().get(index);
        cell.setLabelText(text);
    }

    public void setUnusedCell(int row, int col) {
        int index = row * NUM_COLS + col;
        if (index < 0 || index > grid.getChildren().size() - 1) {
            return;
        }
        grid.getChildren().set(index, ActivityNodeCellFactory.getUnusedChartNodeCell());
    }

    public ActivityNodeCell getCell(int row, int col) {
        int index = row * NUM_COLS + col;
        if (index < 0 || index > grid.getChildren().size() - 1) {
            return null;
        }
        return (ActivityNodeCell) grid.getChildren().get(index);
    }

    public boolean getIsCritical() {
        return isCritical.get();
    }

    public BooleanProperty isCriticalProperty() {
        return isCritical;
    }

    public void setIsCritical(boolean isCritical) {
        this.isCritical.set(isCritical);
    }
}
