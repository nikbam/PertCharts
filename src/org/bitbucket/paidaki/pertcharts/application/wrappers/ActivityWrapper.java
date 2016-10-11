package org.bitbucket.paidaki.pertcharts.application.wrappers;

import org.bitbucket.paidaki.pertcharts.application.Activity;
import org.bitbucket.paidaki.pertcharts.application.util.Util;
import org.bitbucket.paidaki.pertcharts.application.util.XMLLocalDateAdapter;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.io.Serializable;
import java.time.LocalDate;

@XmlRootElement(name = "activity")
@XmlType(propOrder = {"id", "name", "description", "predecessors", "successors", "startDate", "mostLikely", "optimistic", "pessimistic"})
public class ActivityWrapper implements Serializable {

    private int id, mostLikely, optimistic, pessimistic;
    private String name, description, predecessors, successors;
    private LocalDate startDate;

    @XmlElement(name = "id")
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @XmlElement(name = "mostLikely")
    public int getMostLikely() {
        return mostLikely;
    }

    public void setMostLikely(int mostLikely) {
        this.mostLikely = mostLikely;
    }

    @XmlElement(name = "optimistic")
    public int getOptimistic() {
        return optimistic;
    }

    public void setOptimistic(int optimistic) {
        this.optimistic = optimistic;
    }

    @XmlElement(name = "pessimistic")
    public int getPessimistic() {
        return pessimistic;
    }

    public void setPessimistic(int pessimistic) {
        this.pessimistic = pessimistic;
    }

    @XmlElement(name = "name")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @XmlElement(name = "description")
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @XmlElement(name = "startDate")
    @XmlJavaTypeAdapter(XMLLocalDateAdapter.class)
    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    @XmlElement(name = "predecessors")
    public String getPredecessors() {
        return predecessors;
    }

    public void setPredecessors(String predecessors) {
        this.predecessors = predecessors;
    }

    @XmlElement(name = "successors")
    public String getSuccessors() {
        return successors;
    }

    public void setSuccessors(String successors) {
        this.successors = successors;
    }

    public void setActivity(Activity activity) {
        this.id = activity.getId();
        this.name = activity.getName();
        this.description = activity.getDescription();
        this.startDate = activity.getStartDate();
        this.mostLikely = activity.getMostLikely();
        this.optimistic = activity.getOptimistic();
        this.pessimistic = activity.getPessimistic();
        this.predecessors = Util.listToString(activity.getPredecessors());
        this.successors = Util.listToString(activity.getSuccessors());
    }
}
