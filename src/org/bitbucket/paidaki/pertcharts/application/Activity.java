package org.bitbucket.paidaki.pertcharts.application;

import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.bitbucket.paidaki.pertcharts.application.util.Util;

import java.time.LocalDate;
import java.util.concurrent.atomic.AtomicInteger;

public class Activity {

    private static AtomicInteger uniqueID = new AtomicInteger();

    private ProjectManager pm = ProjectManager.getInstance();

    private IntegerProperty id;
    private StringProperty name, description, dependencies;
    private ObjectProperty<LocalDate> startDate, endDate;
    private DoubleProperty expected;
    private IntegerProperty duration, mostLikely, optimistic, pessimistic, esDays, efDays, lsDays, lfDays, slack;
    private LocalDate projectStartDate;

    private ObservableList<Activity> predecessors, successors;

    // Clone constructor
    public Activity(Activity activity) {
        this();
        setActivity(activity);
    }

    public Activity() {
        this(uniqueID.getAndIncrement(), "New_Activity", Util.firstWorkingDate(LocalDate.now()), Util.firstWorkingDate(LocalDate.now()));
    }

    public Activity(int id) {
        this(id, "New_Activity", Util.firstWorkingDate(LocalDate.now()), Util.firstWorkingDate(LocalDate.now()));
    }

    public Activity(int id, String name, LocalDate start, LocalDate end) {
        this.id = new SimpleIntegerProperty(id);
        this.name = new SimpleStringProperty(name);
        this.dependencies = new SimpleStringProperty("-");
        this.description = new SimpleStringProperty();
        this.startDate = new SimpleObjectProperty<>(start);
        this.endDate = new SimpleObjectProperty<>(end);
        this.duration = new SimpleIntegerProperty(Util.getWorkingDays(startDate.get(), endDate.get()));
        this.mostLikely = new SimpleIntegerProperty(duration.get());
        this.optimistic = new SimpleIntegerProperty(duration.get());
        this.pessimistic = new SimpleIntegerProperty(duration.get());
        this.expected = new SimpleDoubleProperty(calculateExpected());
        this.esDays = new SimpleIntegerProperty(0);
        this.efDays = new SimpleIntegerProperty(esDays.get() + duration.get());
        this.lfDays = new SimpleIntegerProperty(efDays.get());
        this.lsDays = new SimpleIntegerProperty(lfDays.get() - duration.get());
        this.slack = new SimpleIntegerProperty(lsDays.get() - esDays.get());
        this.projectStartDate = start;
        this.predecessors = FXCollections.observableArrayList();
        this.successors = FXCollections.observableArrayList();
    }

    public void commitStartDate(LocalDate date) {
        startDate.set(date);
        if (date.isAfter(endDate.get()) || mostLikely.get() != duration.get() || optimistic.get() != duration.get() || pessimistic.get() != duration.get()) {
            endDate.set(Util.addWorkingDays(startDate.get(), duration.get() - 1));
        } else {
            duration.set(Util.getWorkingDays(startDate.get(), endDate.get()));
            setFixedDates();
        }
    }

    public void commitEndDate(LocalDate date) {
        endDate.set(date);
        if (date.isBefore(startDate.get())) {
            date = Util.subtractWorkingDays(endDate.get(), duration.get() - 1);
            if (date.isBefore(projectStartDate)) {
                date = projectStartDate;
            }
            startDate.set(date);
        }
        duration.set(Util.getWorkingDays(startDate.get(), endDate.get()));
        setFixedDates();
    }

    public void commitDuration(int dur) {
        if (dur > 0) {
            duration.set(dur);
            endDate.set(Util.addWorkingDays(startDate.get(), duration.get() - 1));
            setFixedDates();
        }
    }

    public void commitExpected() {
        expected.set(calculateExpected());
        duration.set((int) Math.ceil(expected.get()));
        endDate.set(Util.addWorkingDays(startDate.get(), duration.get() - 1));
    }

    private void setFixedDates() {
        mostLikely.set(duration.get());
        optimistic.set(duration.get());
        pessimistic.set(duration.get());
        expected.set(calculateExpected());
    }

    private double calculateExpected() {
        return (double) (optimistic.get() + 4 * mostLikely.get() + pessimistic.get()) / 6;
    }

    public void fixDates() {
        for (Activity a : successors) {
            if (endDate.get().isEqual(a.getStartDate()) || endDate.get().isAfter(a.getStartDate())) {
                a.commitStartDate(Util.firstWorkingDate(endDate.get().plusDays(1)));
                for (Activity ignored : a.getSuccessors()) {
                    a.fixDates();
                }
            }
        }
    }

    public void calculateStats() {
        pm.updateProjectStats();
        pm.setActivityStatsTab(this);
    }

    // Setters - Getters
    public void setActivity(Activity newActivity) {
        setId(newActivity.getId());
        setName(newActivity.getName());
        setDependencies(newActivity.getDependencies());
        setDescription(newActivity.getDescription());
        setProjectStartDate(newActivity.getProjectStartDate());
        setStartDate(newActivity.getStartDate());
        setEndDate(newActivity.getEndDate());
        setDuration(newActivity.getDuration());
        setMostLikely(newActivity.getMostLikely());
        setOptimistic(newActivity.getOptimistic());
        setPessimistic(newActivity.getPessimistic());
        setExpected(newActivity.getExpected());
        setPredecessors(newActivity.getPredecessors());
        setSuccessors(newActivity.getSuccessors());
        calculateStats();
    }

    public int getId() {
        return id.get();
    }

    public IntegerProperty idProperty() {
        return id;
    }

    public void setId(int id) {
        this.id.set(id);
    }

    public String getName() {
        return name.get();
    }

    public StringProperty nameProperty() {
        return name;
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public String getDescription() {
        return description.get();
    }

    public StringProperty descriptionProperty() {
        return description;
    }

    public void setDescription(String description) {
        this.description.set(description);
    }

    public LocalDate getStartDate() {
        return startDate.get();
    }

    public ObjectProperty<LocalDate> startDateProperty() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate.set(startDate);
    }

    public LocalDate getEndDate() {
        return endDate.get();
    }

    public ObjectProperty<LocalDate> endDateProperty() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate.set(endDate);
    }

    public int getDuration() {
        return duration.get();
    }

    public IntegerProperty durationProperty() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration.set(duration);
    }

    public int getMostLikely() {
        return mostLikely.get();
    }

    public IntegerProperty mostLikelyProperty() {
        return mostLikely;
    }

    public void setMostLikely(int mostLikely) {
        this.mostLikely.set(mostLikely);
    }

    public int getOptimistic() {
        return optimistic.get();
    }

    public IntegerProperty optimisticProperty() {
        return optimistic;
    }

    public void setOptimistic(int optimistic) {
        this.optimistic.set(optimistic);
    }

    public int getPessimistic() {
        return pessimistic.get();
    }

    public IntegerProperty pessimisticProperty() {
        return pessimistic;
    }

    public void setPessimistic(int pessimistic) {
        this.pessimistic.set(pessimistic);
    }

    public LocalDate getProjectStartDate() {
        return projectStartDate;
    }

    public void setProjectStartDate(LocalDate projectStartDate) {
        this.projectStartDate = projectStartDate;
    }

    public double getExpected() {
        return expected.get();
    }

    public DoubleProperty expectedProperty() {
        return expected;
    }

    public void setExpected(double expected) {
        this.expected.set(expected);
    }

    public ObservableList<Activity> getPredecessors() {
        return predecessors;
    }

    public void setPredecessors(ObservableList<Activity> predecessors) {
        this.predecessors = predecessors;
    }

    public ObservableList<Activity> getSuccessors() {
        return successors;
    }

    public void setSuccessors(ObservableList<Activity> successors) {
        this.successors = successors;
    }

    public String getDependencies() {
        return dependencies.get();
    }

    public StringProperty dependenciesProperty() {
        return dependencies;
    }

    public void setDependencies(String dependencies) {
        this.dependencies.set(dependencies);
    }

    public int getEsDays() {
        return esDays.get();
    }

    public IntegerProperty esDaysProperty() {
        return esDays;
    }

    public void setEsDays(int esDays) {
        this.esDays.set(esDays);
    }

    public int getEfDays() {
        return efDays.get();
    }

    public IntegerProperty efDaysProperty() {
        return efDays;
    }

    public void setEfDays(int efDays) {
        this.efDays.set(efDays);
    }

    public int getLsDays() {
        return lsDays.get();
    }

    public IntegerProperty lsDaysProperty() {
        return lsDays;
    }

    public void setLsDays(int lsDays) {
        this.lsDays.set(lsDays);
    }

    public int getLfDays() {
        return lfDays.get();
    }

    public IntegerProperty lfDaysProperty() {
        return lfDays;
    }

    public void setLfDays(int lfDays) {
        this.lfDays.set(lfDays);
    }

    public int getSlack() {
        return slack.get();
    }

    public IntegerProperty slackProperty() {
        return slack;
    }

    public void setSlack(int slack) {
        this.slack.set(slack);
    }
}
