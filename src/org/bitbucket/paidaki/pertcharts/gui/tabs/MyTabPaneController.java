package org.bitbucket.paidaki.pertcharts.gui.tabs;

import javafx.beans.property.BooleanProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.Pane;
import org.bitbucket.paidaki.pertcharts.application.Activity;
import org.bitbucket.paidaki.pertcharts.Main;
import org.bitbucket.paidaki.pertcharts.application.Project;
import org.bitbucket.paidaki.pertcharts.application.util.Tooltips;
import org.bitbucket.paidaki.pertcharts.gui.IncludedWindow;
import org.bitbucket.paidaki.pertcharts.gui.MainWindowController;
import org.bitbucket.paidaki.pertcharts.gui.menubar.MyMenuBarController;
import org.bitbucket.paidaki.pertcharts.gui.table.MyTableViewController;
import org.bitbucket.paidaki.pertcharts.gui.tabs.pertcharttab.PertChartTabController;
import org.bitbucket.paidaki.pertcharts.gui.tabs.statstab.StatsTabController;

public class MyTabPaneController implements IncludedWindow {

    private MainWindowController mainWindowController;
    private MyMenuBarController menuBarController;
    private MyTableViewController tableViewController;

    @FXML
    private PertChartTabController pertChartTabController;
    @FXML
    private StatsTabController statisticsTabController;
    @FXML
    private Tab pertTab, ganttTab, statsTab;
    @FXML
    private TabPane tabPane;

    @FXML
    private void initialize() {
        // Add tooltips
        pertTab.setTooltip(Tooltips.PERT_CHART);
        ganttTab.setTooltip(Tooltips.GANTT_CHART);
        statsTab.setTooltip(Tooltips.STATS);

        // Log event
        if (Main.DEBUGGING) System.out.println(this.getClass().getSimpleName() + ": Initialized");
    }

    @Override
    public void injectMainController(MainWindowController mainWindowController) {
        this.mainWindowController = mainWindowController;
        setup();
    }

    private void setup() {
        // Inject mainWindowController
        pertChartTabController.injectMainController(mainWindowController);
        statisticsTabController.injectMainController(mainWindowController);

        // Get controllers
        menuBarController = mainWindowController.getMenuBarController();
        tableViewController = mainWindowController.getTableViewController();

        // Bind Tabs with View menu items
        pertTab.setOnSelectionChanged(event -> {
            if (pertTab.isSelected()) menuBarController.sselectPertRMenuItem(true);
        });
        ganttTab.setOnSelectionChanged(event -> {
            if (ganttTab.isSelected()) menuBarController.selectGanttRMenuItem(true);
        });
        statsTab.setOnSelectionChanged(event -> {
            if (statsTab.isSelected()) menuBarController.selectStatsRMenuItem(true);
        });

        // Disable statistics tab when table is empty
        statsTab.disableProperty().bind(tableViewController.emptySelectionBinding());
        statsTab.disableProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                tabPane.getSelectionModel().select(pertTab);
            }
        });

        // Log event
        if (Main.DEBUGGING) System.out.println(this.getClass().getSimpleName() + ": Setup done");
    }

    public Pane getChartCanvas() {
        return pertChartTabController.getCanvas();
    }

    public void setActivityStats(Activity activity) {
        statisticsTabController.setActivity(activity);
    }

    public void setProjectStats(Project project) {
        statisticsTabController.setProject(project);
    }

    // Clear selections on active tab
    public void clearSelections() {
        //TODO
    }

    public Tab getSelectedTab() {
        return tabPane.getSelectionModel().getSelectedItem();
    }

    public BooleanProperty autoArrangeProperty() {
        return pertChartTabController.autoArrangeProperty();
    }

    // Tab selection setters
    public void selectPertTab() {
        tabPane.getSelectionModel().select(pertTab);
    }

    public void selectGanttTab() {
        tabPane.getSelectionModel().select(ganttTab);
    }

    public void selectStatsTab() {
        tabPane.getSelectionModel().select(statsTab);
    }

    // Tab disable property getters
    public BooleanProperty pertTabDisableProperty() {
        return pertTab.disableProperty();
    }

    public BooleanProperty ganttTabDisableProperty() {
        return ganttTab.disableProperty();
    }

    public BooleanProperty statsTabDisableProperty() {
        return statsTab.disableProperty();
    }

    // Tab disabled setters
    public void ganttTabSetDisabled(boolean value) {
        ganttTab.setDisable(value);
    }

    public void statsTabSetDisabled(boolean value) {
        statsTab.setDisable(value);
    }
}
