package org.fatmansoft.teach.models;

import javafx.scene.control.Button;
import org.fatmansoft.teach.util.CommonMethod;

import java.awt.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Activity {
    private Integer activityId;
    private String activityName;
    private Integer status;
    private LocalDate start;
    private LocalDate end;
    //负责人ID, 此处为personId
    private Integer directorId;
    private String directorName;
    //参与人数
    private Integer number;
    //最大参与人数
    private Integer maxNumber;
    //外界可执行的一些操作
    private List<Button> actions=new ArrayList<>();

    public Activity(){

    }

    public Activity(Map m){
        this.activityId = CommonMethod.getInteger(m, "activityId");
        this.activityName = CommonMethod.getString(m, "activityName");
        this.status = CommonMethod.getInteger(m, "status");
        this.start = CommonMethod.getLocalDateFromString(CommonMethod.getString(m, "start"),CommonMethod.DATE_FORMAT);
        this.end = CommonMethod.getLocalDateFromString(CommonMethod.getString(m, "end"),CommonMethod.DATE_FORMAT);
        this.directorId = CommonMethod.getInteger(m, "directorId");
        this.directorName = CommonMethod.getString(m, "directorName");
        this.number = CommonMethod.getInteger(m, "number");
        this.maxNumber = CommonMethod.getInteger(m, "maxNumber");
    }

    public Integer getActivityId() {
        return activityId;
    }

    public void setActivityId(Integer activityId) {
        this.activityId = activityId;
    }

    public String getActivityName() {
        return activityName;
    }

    public void setActivityName(String activityName) {
        this.activityName = activityName;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public LocalDate getStart() {
        return start;
    }

    public void setStart(LocalDate start) {
        this.start = start;
    }

    public LocalDate getEnd() {
        return end;
    }

    public void setEnd(LocalDate end) {
        this.end = end;
    }

    public Integer getDirectorId() {
        return directorId;
    }

    public void setDirectorId(Integer directorId) {
        this.directorId = directorId;
    }

    public String getDirectorName() {
        return directorName;
    }

    public void setDirectorName(String directorName) {
        this.directorName = directorName;
    }

    public Integer getNumber() {
        return number;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }

    public List<Button> getActions() {
        return actions;
    }

    public void setActions(List<Button> actions) {
        this.actions = actions;
    }

    public Integer getMaxNumber() {
        return maxNumber;
    }

    public void setMaxNumber(Integer maxNumber) {
        this.maxNumber = maxNumber;
    }
}
