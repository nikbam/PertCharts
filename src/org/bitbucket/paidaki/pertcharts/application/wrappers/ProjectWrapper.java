package org.bitbucket.paidaki.pertcharts.application.wrappers;

import javafx.collections.FXCollections;
import org.bitbucket.paidaki.pertcharts.application.Activity;
import org.bitbucket.paidaki.pertcharts.application.Project;
import org.bitbucket.paidaki.pertcharts.application.util.Util;
import org.bitbucket.paidaki.pertcharts.application.util.XMLLocalDateAdapter;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@XmlRootElement(name = "project")
@XmlType(propOrder = {"projectStartDate", "activities"})
public class ProjectWrapper implements Serializable {

    private LocalDate projectStartDate;
    private ArrayList<ActivityWrapper> activities;

    @XmlElement(name = "activity")
    public ArrayList<ActivityWrapper> getActivities() {
        return activities;
    }

    public void setActivities(ArrayList<ActivityWrapper> activities) {
        this.activities = activities;
    }

    @XmlElement(name = "projectStartDate")
    @XmlJavaTypeAdapter(XMLLocalDateAdapter.class)
    public LocalDate getProjectStartDate() {
        return projectStartDate;
    }

    public void setProjectStartDate(LocalDate projectStartDate) {
        this.projectStartDate = projectStartDate;
    }

    public void setProject(Project project) {
        this.projectStartDate = project.getStartingDate();

        this.activities = new ArrayList<>();
        for (Activity a : project.getActivities()) {
            ActivityWrapper activity = new ActivityWrapper();
            activity.setActivity(a);
            this.activities.add(activity);
        }
    }

    public Project extractProject() {
        Project project = new Project();
        project.setStartingDate(this.projectStartDate);

        if (activities == null) {
            return project;
        }

        for (ActivityWrapper ignored : activities) {
            project.addActivity();
        }
        for (int i = 0; i < activities.size(); i++) {
            List<Activity> predecessors = Util.stringToList(activities.get(i).getPredecessors(), project.getActivities());
            if (predecessors != null) {
                project.getActivities().get(i).setPredecessors(FXCollections.observableArrayList(predecessors));
                project.getActivities().get(i).setDependencies(activities.get(i).getPredecessors());
            } else {
                project.getActivities().get(i).setPredecessors(FXCollections.observableArrayList());
            }

            List<Activity> successors = Util.stringToList(activities.get(i).getSuccessors(), project.getActivities());
            if (successors != null) {
                project.getActivities().get(i).setSuccessors(FXCollections.observableArrayList(successors));
            } else {
                project.getActivities().get(i).setSuccessors(FXCollections.observableArrayList());
            }
        }
        for (int i = 0; i < activities.size(); i++) {
            project.getActivities().get(i).setId(activities.get(i).getId());
            project.getActivities().get(i).setName(activities.get(i).getName());
            project.getActivities().get(i).setDescription(activities.get(i).getDescription());
            project.getActivities().get(i).commitStartDate(activities.get(i).getStartDate());
            project.getActivities().get(i).setMostLikely(activities.get(i).getMostLikely());
            project.getActivities().get(i).setOptimistic(activities.get(i).getOptimistic());
            project.getActivities().get(i).setPessimistic(activities.get(i).getPessimistic());
            project.getActivities().get(i).commitExpected();
        }
        project.updateStats();
        return project;
    }
}
