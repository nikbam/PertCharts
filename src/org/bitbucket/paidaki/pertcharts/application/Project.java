package org.bitbucket.paidaki.pertcharts.application;

import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import org.bitbucket.paidaki.pertcharts.application.util.Util;

import java.io.File;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class Project {

    private static final String DEFAULT_FILENAME = "New Project";
    private static AtomicInteger nextProjectId = new AtomicInteger();

    private ProjectManager pm;

    private int nextActivityId;
    private int projectId;

    private BooleanProperty isEditing;
    private StringProperty saveAsterisk, projectName;
    private File file;
    private IntegerProperty projectEarliestFinish;
    private ObjectProperty<LocalDate> startingDate, finishDate;
    private ObservableList<Activity> activities, criticalActivities;
    private ObservableList<ObservableList<Activity>> criticalPaths;

    public Project() {
        this.pm = ProjectManager.getInstance();
        this.nextActivityId = 1;
        this.projectId = nextProjectId.getAndAdd(1);
        this.isEditing = new SimpleBooleanProperty(false);
        this.projectName = new SimpleStringProperty(Project.DEFAULT_FILENAME + " " + projectId);
        this.projectEarliestFinish = new SimpleIntegerProperty();
        this.startingDate = new SimpleObjectProperty<>(Util.firstWorkingDate(LocalDate.now()));
        this.finishDate = new SimpleObjectProperty<>();
        this.saveAsterisk = new SimpleStringProperty("");
        this.file = null;
        this.activities = FXCollections.observableArrayList();
        this.criticalActivities = FXCollections.observableArrayList();
        this.criticalPaths = FXCollections.observableArrayList();
        initialize();
    }

    // Initialize
    private void initialize() {
        isEditing.addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                saveAsterisk.setValue("*");
            } else {
                saveAsterisk.setValue("");
            }
        });
        startingDate.addListener(observable -> {
            updateActivitiesDates();
        });
        activities.addListener((ListChangeListener<? super Activity>) observable -> checkSaveState());
        startingDate.addListener(observable -> checkSaveState());
    }

    // Check for save status
    private void checkSaveState() {
        if (activities.isEmpty() && file == null) {
            setIsEditing(false);
        } else {
            setIsEditing(true);
        }
    }

    // Add activity
    public void addActivity() {
        addActivity("New_Activity", getStartingDate(), Util.firstWorkingDate(getStartingDate()));
    }

    public void addActivity(String name, LocalDate start, LocalDate end) {
        Activity newActivity = new Activity(nextActivityId++, name, start, end);
        newActivity.nameProperty().addListener((observable1, oldValue, newValue) -> checkSaveState());
        newActivity.startDateProperty().addListener(observable -> checkSaveState());
        newActivity.endDateProperty().addListener(observable -> checkSaveState());
        newActivity.durationProperty().addListener(observable -> checkSaveState());
        newActivity.mostLikelyProperty().addListener(observable -> checkSaveState());
        newActivity.optimisticProperty().addListener(observable -> checkSaveState());
        newActivity.pessimisticProperty().addListener(observable -> checkSaveState());
        newActivity.dependenciesProperty().addListener(observable -> checkSaveState());
        activities.add(newActivity);
        try {
            pm.addChartActivity(newActivity);
        } catch (Exception ex) {
            // eat exception
        }
        updateStats();
    }

    // Remove activity
    public void deleteActivity(Activity activity) {
        activities.remove(activity);
        for (Activity a : activities) {
            a.getPredecessors().remove(activity);
            a.getSuccessors().remove(activity);
            a.setDependencies(Util.listToString(a.getPredecessors()));
        }
        try {
            pm.removeChartActivity(activity);
        } catch (Exception ex) {
            // eat exception
        }
        updateActivitiesId();
        updateStats();
    }

    // Remove all activities
    public void clearActivities() {
        activities.clear();
        updateActivitiesId();
        updateStats();
    }

    // Clear all dependencies between activities
    public void clearDependencies() {
        for (Activity a : activities) {
            a.getPredecessors().clear();
            a.getSuccessors().clear();
            a.setDependencies(Util.listToString(a.getPredecessors()));
        }
        updateStats();
    }

    // Update the activities ID
    private void updateActivitiesId() {
        for (int i = 0; i < activities.size(); i++) {
            activities.get(i).setId(i + 1);
        }
        nextActivityId = activities.size() + 1;
    }

    // Update the activities Dates
    private void updateActivitiesDates() {
        for (Activity a : activities) {
            a.setProjectStartDate(startingDate.get());
            if (a.getStartDate().isBefore(startingDate.get())) {
                int duration = a.getDuration();
                a.commitStartDate(startingDate.get());
                a.commitDuration(duration);
                a.fixDates();
                a.calculateStats();
            } else {
                try {
                    pm.setProjectStatsTab(this);
                } catch (Exception ex) {
                    // eat exception
                }
            }
        }
    }

    // Update statistics
    public void updateStats() {
        for (Activity a : activities) {
            if (a.getPredecessors().isEmpty()) {
                a.setEsDays(0);
                a.setEfDays(a.getEsDays() + a.getDuration());
                forwardPass(a);
            }
        }
        projectEarliestFinish.set(Util.maxEFLeafs(activities));
        for (Activity a : activities) {
            if (a.getSuccessors().isEmpty()) {
                a.setLfDays(projectEarliestFinish.get());
                a.setLsDays(a.getLfDays() - a.getDuration());
                a.setSlack(a.getLsDays() - a.getEsDays());
                backwardPass(a);
            }
        }
        extractCriticalPaths();
        finishDate.set(Util.maxEndDate(activities));
        try {
            pm.setProjectStatsTab(this);
            pm.updateChartCriticalPath();
        } catch (Exception ex) {
            // eat exception
        }
    }

    void extractCriticalPaths() {
        criticalActivities.clear();
        criticalPaths.clear();
        for (Activity a : activities) {
            if (a.getSlack() == 0) {
                criticalActivities.add(a);
            }
        }
        for (Activity a : criticalActivities) {
            if (a.getPredecessors().isEmpty()) {
                ObservableList<Activity> list = FXCollections.observableArrayList();
                extractCriticalPaths(a, list);
            }
        }
    }

    private void extractCriticalPaths(Activity root, List<Activity> list) {
        list.add(root);
        if (root.getSuccessors().isEmpty()) {
            criticalPaths.add(FXCollections.observableArrayList(list));
        } else {
            for (Activity a : root.getSuccessors()) {
                if (a.getSlack() == 0) {
                    extractCriticalPaths(a, FXCollections.observableArrayList(list));
                }
            }
        }
    }

    // First pass calculating ES and EF
    private void forwardPass(Activity root) {
        for (Activity a : root.getSuccessors()) {
            a.setEsDays(Util.maxEarliestFinish(a.getPredecessors()));
            a.setEfDays(a.getEsDays() + a.getDuration());
            forwardPass(a);
        }
    }

    // Second pass calculating LF, LS and Slack
    private void backwardPass(Activity root) {
        for (Activity a : root.getPredecessors()) {
            a.setLfDays(Util.minLatestStart(a.getSuccessors()));
            a.setLsDays(a.getLfDays() - a.getDuration());
            a.setSlack(a.getLsDays() - a.getEsDays());
            backwardPass(a);
        }
    }

    // Search for activities containing the search term
    public ArrayList<Activity> searchForActivities(String term) {
        ArrayList<Activity> results = new ArrayList<>();
        for (Activity a : activities) {
            if (a.getName().toLowerCase().contains(term.toLowerCase())) {
                results.add(a);
            }
        }
        return results;
    }

    // Move activity up
    public void moveActivityUp(Activity activity) {
        int index = activities.indexOf(activity);
        if (index > 0) {
            Collections.swap(activities, index, index - 1);
        }
    }

    // Move activity down
    public void moveActivityDown(Activity activity) {
        int index = activities.indexOf(activity);
        if (index < activities.size() - 1) {
            Collections.swap(activities, index, index + 1);
        }
    }

    // Setters - Getters
    public StringProperty projectNameProperty() {
        return projectName;
    }

    public ObservableList<Activity> getActivities() {
        return activities;
    }

    public int getProjectId() {
        return projectId;
    }

    public String getProjectName() {
        return projectName.get();
    }

    public void setActivities(ObservableList<Activity> activities) {
        this.activities = activities;
        this.nextActivityId = activities.size() + 1;
    }

    public ObservableList<Activity> getCriticalActivities() {
        return criticalActivities;
    }

    public ObservableList<ObservableList<Activity>> getCriticalPaths() {
        return criticalPaths;
    }

    public void setFile(File file) {
        this.file = file;
        projectName.set(file == null ? Project.DEFAULT_FILENAME + projectId : (file.getName().contains(".") ? file.getName().substring(0, file.getName().lastIndexOf('.')) : file.getName()));
    }

    public int getNextActivityId() {
        return nextActivityId;
    }

    public File getFile() {
        return file;
    }

    public void setProjectName(String projectName) {
        this.projectName.set(projectName);
    }

    public void setIsEditing(boolean isEditing) {
        this.isEditing.set(isEditing);
    }

    public boolean getIsEditing() {
        return isEditing.get();
    }

    public StringProperty saveAsteriskProperty() {
        return saveAsterisk;
    }

    public LocalDate getStartingDate() {
        return startingDate.get();
    }

    public LocalDate getFinishDate() {
        return finishDate.get();
    }

    public int getProjectEarliestFinish() {
        return projectEarliestFinish.get();
    }

    public ObjectProperty<LocalDate> startingDateProperty() {
        return startingDate;
    }

    public void setStartingDate(LocalDate startingDate) {
        this.startingDate.set(startingDate);
    }

    public ObjectProperty<LocalDate> finishDateProperty() {
        return finishDate;
    }

    public static void clearNextProjectId() {
        Project.nextProjectId.set(0);
    }

    public BooleanProperty isEditingProperty() {
        return isEditing;
    }

    public String getSaveAsterisk() {
        return saveAsterisk.get();
    }

    public IntegerProperty projectEarliestFinishProperty() {
        return projectEarliestFinish;
    }
}
