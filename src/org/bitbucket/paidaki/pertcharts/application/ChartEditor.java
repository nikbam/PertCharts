package org.bitbucket.paidaki.pertcharts.application;

import javafx.beans.property.BooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.BoundingBox;
import javafx.geometry.Insets;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Tooltip;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.util.converter.NumberStringConverter;
import org.bitbucket.paidaki.pertcharts.application.util.Util;
import org.bitbucket.paidaki.pertcharts.gui.tabs.pertcharttab.chart.*;

import java.awt.*;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public class ChartEditor {

    private static final Point SPAWN_LOCATION = new Point(300, 300);
    private static final Point OUT_OF_SCREEN = new Point(0, -1000);
    private static final int LINES_TO_SKIP = 1;
    private static final double LEFT_PADDING = 5;
    private static final double GAP_X = 150;
    private static final double GAP_Y = 150;

    private final Legend legend;
    private final EllipseNode start, end;

    private ProjectManager pm = ProjectManager.getInstance();

    private Pane canvas;
    private Project project;
    private ObservableList<Activity> activities;
    private ObservableList<ConnectingLine> lines;
    private BooleanProperty autoArrangeIsOn;
    private Map<Activity, DraggableActivityNode> nodes;
    private ChartNode selectedNode;

    public ChartEditor(Pane canvas, Project project, BooleanProperty autoArrangeIsOn) {
        this.canvas = canvas;
        this.project = project;
        this.activities = project.getActivities();
        this.lines = FXCollections.observableArrayList();
        this.nodes = new LinkedHashMap<>();
        this.autoArrangeIsOn = autoArrangeIsOn;
        this.start = createStartNode();
        this.end = createEndNode();
        this.legend = createLegend();

        initialize();
        addListeners();
        updateLines();
    }

    private void initialize() {
        nodes.clear();
        lines.clear();
        canvas.getChildren().clear();
        canvas.getChildren().add(legend);
        for (Activity a : activities) {
            DraggableActivityNode node = createActivityNode(a);
            nodes.put(a, node);
        }

        checkDoAutoArrange();
        if (!autoArrangeIsOn.get() && !nodes.isEmpty()) {
            spawnAtDefaultLocations();
        }
        addNodesToCanvas();
    }

    private void addListeners() {
        activities.addListener((ListChangeListener<? super Activity>) c -> {
            if (activities.isEmpty()) {
                initialize();
            }
        });
        lines.addListener((ListChangeListener<? super ConnectingLine>) c -> {
            while (c.next()) {
                if (!activities.isEmpty()) {
                    if (c.wasAdded()) {
                        for (Object o : c.getAddedSubList()) {
                            ConnectingLine line = (ConnectingLine) o;
                            addLineToCanvas(line);
                        }
                    } else if (c.wasRemoved()) {
                        for (Object o : c.getRemoved()) {
                            ConnectingLine line = (ConnectingLine) o;
                            removeLineFromCanvas(line);
                        }
                    }
                }
            }
        });
    }

    public void addActivity(Activity activity) {
        DraggableActivityNode node = createActivityNode(activity);
        nodes.put(activity, node);

        if (!autoArrangeIsOn.get()) {
            spawnAtDefaultLocation(node);
        }
        addNodeToCanvas(node);
    }

    private void spawnAtDefaultLocation(ChartNode node) {
        if (!canvas.getChildren().contains(start) && !canvas.getChildren().contains(end)) {
            start.setLocation(SPAWN_LOCATION);
            end.setLocation(SPAWN_LOCATION);
        }
        node.setLocation(SPAWN_LOCATION);
    }

    public void removeActivity(Activity activity) {
        DraggableActivityNode node = nodes.get(activity);
        nodes.remove(activity);
        removeNodeFromCanvas(node);
    }

    private void addNodesToCanvas() {
        for (Activity a : nodes.keySet()) {
            addNodeToCanvas(nodes.get(a));
        }
    }

    private void spawnAtDefaultLocations() {
        for (Activity a : nodes.keySet()) {
            spawnAtDefaultLocation(nodes.get(a));
        }
    }

    private void addLineToCanvas(ConnectingLine line) {
        if (!canvas.getChildren().contains(line)) {
            canvas.getChildren().addAll(line, line.getArrow());
            line.toBack();
            line.getArrow().toBack();
        }
        legend.toBack();
    }

    private void removeLineFromCanvas(ConnectingLine line) {
        canvas.getChildren().removeAll(line, line.getArrow());
    }

    private void addNodeToCanvas(ChartNode node) {
        if (!canvas.getChildren().contains(start) && !canvas.getChildren().contains(end)) {    // If it is the first node add start and end nodes
            canvas.getChildren().add(start);
            canvas.getChildren().add(end);
        }
        canvas.getChildren().add(node);
    }

    private void removeNodeFromCanvas(ChartNode node) {
        if (nodes.size() < 1) { // If it was the last node remove start and end nodes
            canvas.getChildren().remove(start);
            canvas.getChildren().remove(end);
        }
        canvas.getChildren().remove(node);
    }

    public void checkDoAutoArrange() {
        if (!autoArrangeIsOn.get() || nodes.isEmpty()) {
            return;
        }
        doAutoArrange();
    }

    public void doAutoArrange() {
        if (nodes.isEmpty()) {
            return;
        }
        LinkedHashMap<Integer, Integer> depthsMap = Util.calculateDepths(nodes);
        fixX();
        fixY(depthsMap);
    }

    private void fixX() {
        int max = 0;
        start.setLayoutX(start.getGraphDepth() * GAP_X + LEFT_PADDING);
        for (Activity a : nodes.keySet()) {
            ChartNode node = nodes.get(a);
            node.setLayoutX(node.getGraphDepth() * GAP_X + LEFT_PADDING);
            if (node.getGraphDepth() > max) max = node.getGraphDepth();
        }
        end.setLayoutX(++max * GAP_X + LEFT_PADDING + 20);  //TODO Replace 20 (make start and end nodes have the same gap) with dynamic value
    }

    private void fixY(LinkedHashMap<Integer, Integer> depthsMap) {
        int max = Collections.max(depthsMap.values());
        int pos = LINES_TO_SKIP + (max / 2);
        double offset = max % 2 != 0 ? 0 : GAP_Y / 2;
        start.setLayoutY(pos * GAP_Y - offset + 9);    //TODO Replace 9 (center oval nodes) with dynamic value
        for (Integer depth : depthsMap.keySet()) {
            int frequency = depthsMap.get(depth);
            pos = LINES_TO_SKIP + ((max - frequency) / 2);
            offset = (frequency != max && frequency % 2 != 0 && max % 2 == 0) ? GAP_Y / 2 : 0;
            for (Activity a : nodes.keySet()) {
                ChartNode node = nodes.get(a);
                if (node.getGraphDepth() == depth) {
                    node.setLayoutY(pos * GAP_Y + offset);
                    pos++;
                }
            }
        }
        end.setLayoutY(start.getLayoutY());
    }

    public void updateLines() {
        lines.clear();
        ConnectingLine line;
        for (Activity a : nodes.keySet()) {
            DraggableActivityNode node = nodes.get(a);
            boolean isCritical;
            if (project.getCriticalActivities().contains(a)) {
                isCritical = true;
                node.setIsCritical(true);
            } else {
                isCritical = false;
                node.setIsCritical(false);
            }
            if (a.getPredecessors().isEmpty()) {
                if (isCritical) {
                    line = new CriticalLine(start, node);
                } else {
                    line = new ConnectingLine(start, node);
                }
                lines.add(line);
            }
            if (a.getSuccessors().isEmpty()) {
                if (isCritical) {
                    line = new CriticalLine(node, end);
                } else {
                    line = new ConnectingLine(node, end);
                }
                lines.add(line);
            } else {
                for (Activity s : a.getSuccessors()) {
                    if (isCritical && project.getCriticalActivities().contains(s)) {
                        line = new CriticalLine(node, nodes.get(s));
                    } else {
                        line = new ConnectingLine(node, nodes.get(s));
                    }
                    lines.add(line);
                }
            }
        }
    }

    private Legend createLegend() {
        Legend legend = new Legend();
        legend.setPadding(new Insets(5, 5, 5, 5));
        legend.toBack();
        return legend;
    }

    private EllipseNode createStartNode() {
        EllipseNode start = new EllipseNode();
        start.getTextLabel().setText("Start");
        start.getEllipse().setFill(Color.DARKGREEN);
        start.getDateLabel().textProperty().bindBidirectional(project.startingDateProperty(), Util.DATE_STRING_CONVERTER);
        start.setLocation(OUT_OF_SCREEN);
        return start;
    }

    private EllipseNode createEndNode() {
        EllipseNode end = new EllipseNode();
        end.getTextLabel().setText("End");
        end.getEllipse().setFill(Color.DARKRED);
        end.getDateLabel().textProperty().bindBidirectional(project.finishDateProperty(), Util.DATE_STRING_CONVERTER);
        end.setLocation(OUT_OF_SCREEN);
        return end;
    }

    private DraggableActivityNode createActivityNode(Activity activity) {
        DraggableActivityNode node = new DraggableActivityNode() {
            @Override
            public void onPressed() {
                pm.selectActivity(activity);
            }
        };
        Tooltip tooltip = new Tooltip("Activity Node");
        tooltip.textProperty().bind(activity.nameProperty());
        tooltip.setMaxWidth(200);
        tooltip.setWrapText(true);
        Tooltip.install(node, tooltip);

        node.getCell(0, 0).getLabel().textProperty().bindBidirectional(activity.esDaysProperty(), new NumberStringConverter());
        node.getCell(0, 0).getLabel().setStyle("-fx-text-fill: green");

        node.getCell(0, 1).getLabel().textProperty().bindBidirectional(activity.idProperty(), new NumberStringConverter());
        node.getCell(0, 1).getLabel().setFont(ActivityNodeCellFactory.DEFAULT_FONT_BOLD);
        node.getCell(0, 1).getLabel().setStyle("-fx-underline: true");

        node.getCell(0, 2).getLabel().textProperty().bindBidirectional(activity.efDaysProperty(), new NumberStringConverter());
        node.getCell(0, 2).getLabel().setStyle("-fx-text-fill: green");

        node.getCell(1, 0).getLabel().textProperty().bindBidirectional(activity.slackProperty(), new NumberStringConverter());
        node.getCell(1, 0).getLabel().setStyle("-fx-text-fill: red");

        node.setUnusedCell(1, 1);

        node.getCell(1, 2).getLabel().textProperty().bindBidirectional(activity.slackProperty(), new NumberStringConverter());
        node.getCell(1, 2).getLabel().setStyle("-fx-text-fill: red");

        node.getCell(2, 0).getLabel().textProperty().bindBidirectional(activity.lsDaysProperty(), new NumberStringConverter());
        node.getCell(2, 0).getLabel().setStyle("-fx-text-fill: blue");

        node.getCell(2, 1).getLabel().textProperty().bindBidirectional(activity.durationProperty(), new NumberStringConverter());
        node.getCell(2, 1).getLabel().setFont(ActivityNodeCellFactory.DEFAULT_FONT_ITALIC);

        node.getCell(2, 2).getLabel().textProperty().bindBidirectional(activity.lfDaysProperty(), new NumberStringConverter());
        node.getCell(2, 2).getLabel().setStyle("-fx-text-fill: blue");

        node.setLocation(OUT_OF_SCREEN);
        return node;
    }

    public WritableImage getCanvasSnapshot() {
        BoundingBox box = getNodesBoundingBox();

        if (selectedNode != null) selectedNode.deselect();

        SnapshotParameters parameters = new SnapshotParameters();
        parameters.setFill(Color.TRANSPARENT);
        parameters.setViewport(new Rectangle2D(box.getMinX(), box.getMinY(), box.getWidth(), box.getHeight()));
        WritableImage image = canvas.snapshot(parameters, null);

        if (selectedNode != null) selectedNode.select();

        return image;
    }

    private BoundingBox getNodesBoundingBox() {
        double minX = canvas.getPrefWidth();
        double minY = canvas.getPrefHeight();
        double maxX = 0;
        double maxY = 0;
        for (Node n : canvas.getChildren()) {
            if (n.getBoundsInParent().getMinX() < minX) minX = n.getBoundsInParent().getMinX();
            if (n.getBoundsInParent().getMaxX() > maxX) maxX = n.getBoundsInParent().getMaxX();
            if (n.getBoundsInParent().getMinY() < minY) minY = n.getBoundsInParent().getMinY();
            if (n.getBoundsInParent().getMaxY() > maxY) maxY = n.getBoundsInParent().getMaxY();
        }
        // Fix Bounds
        if (minX < 0) minX = 0;
        if (minY < 0) minY = 0;
        if (maxX > canvas.getWidth()) canvas.setPrefWidth(maxX);
        if (maxY > canvas.getHeight()) canvas.setPrefHeight(maxY);

        return new BoundingBox(minX, minY, maxX - minX, maxY - minY);
    }

    public void selectActivityNode(Activity activity) {
        for (Activity a : nodes.keySet()) {
            nodes.get(a).deselect();
        }
        selectedNode = nodes.get(activity);
        selectedNode.select();
    }
}
