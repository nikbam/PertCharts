package org.bitbucket.paidaki.pertcharts.gui.tabs.statstab;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import org.bitbucket.paidaki.pertcharts.application.Activity;
import org.bitbucket.paidaki.pertcharts.application.Project;
import org.bitbucket.paidaki.pertcharts.application.util.Util;
import org.bitbucket.paidaki.pertcharts.gui.IncludedWindow;
import org.bitbucket.paidaki.pertcharts.gui.MainWindowController;

public class StatsTabController implements IncludedWindow {

    private MainWindowController mainWindowController;

    @FXML
    private Label idLabel, nameLabel, durationLabel, mostLikelyLabel, optimisticLabel, pessimisticLabel, expectedLabel, esLabel, lsLabel, efLabel, lfLabel, slackLabel, pefLabel, ptotalLabel;
    @FXML
    private TextArea descriptionTextArea;
    @FXML
    private DatePicker beginDatePicker, finishDatePicker, esDatePicker, lsDatePicker, efDatePicker, lfDatePicker, ptotalDatePicker;
    @FXML
    private ListView<String> cpsListView;

    @FXML
    private void initialize() {
        nameLabel.setWrapText(true);
        descriptionTextArea.setEditable(false);
        descriptionTextArea.setWrapText(true);
        beginDatePicker.setDisable(true);
        finishDatePicker.setDisable(true);
        esDatePicker.setDisable(true);
        lsDatePicker.setDisable(true);
        efDatePicker.setDisable(true);
        lfDatePicker.setDisable(true);
        ptotalDatePicker.setDisable(true);
        cpsListView.setEditable(false);
    }

    @Override
    public void injectMainController(MainWindowController mainWindowController) {
        this.mainWindowController = mainWindowController;
        setup();
    }

    private void setup() {
    }

    public void setActivity(Activity activity) {
        idLabel.setText(String.valueOf(activity.getId()));
        nameLabel.textProperty().unbind();
        nameLabel.textProperty().bind(activity.nameProperty());
        descriptionTextArea.textProperty().unbind();
        descriptionTextArea.textProperty().bind(activity.descriptionProperty());
        beginDatePicker.setValue(activity.getStartDate());
        finishDatePicker.setValue(activity.getEndDate());
        durationLabel.setText(String.valueOf(activity.getDuration()));
        optimisticLabel.setText(String.valueOf(activity.getOptimistic()));
        mostLikelyLabel.setText(String.valueOf(activity.getMostLikely()));
        pessimisticLabel.setText(String.valueOf(activity.getPessimistic()));
        expectedLabel.setText(Util.formatDouble(activity.getExpected()));
        esLabel.setText(String.valueOf(activity.getEsDays()));
        esDatePicker.setValue(activity.getStartDate());
        efLabel.setText(String.valueOf(activity.getEfDays()));
        efDatePicker.setValue(Util.addWorkingDays(esDatePicker.getValue(), activity.getDuration() - 1));
        lsLabel.setText(String.valueOf(activity.getLsDays()));
        lsDatePicker.setValue(Util.addWorkingDays(esDatePicker.getValue(), activity.getSlack()));
        lfLabel.setText(String.valueOf(activity.getLfDays()));
        lfDatePicker.setValue(Util.addWorkingDays(lsDatePicker.getValue(), activity.getDuration() - 1));
        slackLabel.setText(String.valueOf(activity.getSlack()));
    }

    public void setProject(Project project) {
        pefLabel.setText(String.valueOf(project.getProjectEarliestFinish()) + " productive days");
        ptotalLabel.setText(String.valueOf(Util.getWorkingDays(project.getStartingDate(), project.getFinishDate())));
        ptotalDatePicker.setValue(project.getFinishDate());

        cpsListView.getItems().clear();
        for (ObservableList<Activity> a : project.getCriticalPaths()) {
            StringBuilder strB = new StringBuilder("<");
            for (int i = 0; i < a.size(); i++) {
                strB.append(a.get(i).getId());
                if (i != a.size() - 1) {
                    strB.append(", ");
                }
            }
            strB.append(">");
            cpsListView.getItems().add(String.valueOf(strB));
        }
    }

}
