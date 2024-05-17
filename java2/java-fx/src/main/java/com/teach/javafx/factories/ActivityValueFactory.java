package com.teach.javafx.factories;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TableColumn;
import javafx.scene.layout.HBox;
import javafx.util.Callback;
import org.fatmansoft.teach.models.Activity;
import org.fatmansoft.teach.util.CommonMethod;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class ActivityValueFactory implements Callback<TableColumn.CellDataFeatures<Activity, String>, ObservableValue<String>> {
    @Override
    public ObservableValue<String> call(TableColumn.CellDataFeatures<Activity, String> param) {
        Activity a = param.getValue();
        String id = param.getTableColumn().getId();
        switch (id){
            case "activityNum":{
                String num = a.getActivityId() == null ? "" : a.getActivityId().toString();
                return new SimpleStringProperty(num);
            }
            case "activityName":{
                String name = a.getActivityName() == null ? "" : a.getActivityName();
                return new SimpleStringProperty(name);
            }
            case "directorName":{
                String name = a.getDirectorName() == null ? "" : a.getDirectorName();
                return new SimpleStringProperty(name);
            }
            case "number":{
                Integer n = a.getNumber();
                String number = n == null ? "未知" : n.toString();
                return new SimpleStringProperty(number);
            }
            case "status":{
                Integer s = a.getStatus();
                String status;
                if(s == 0){
                    status = "审批中";
                }
                else if(s == 1){
                    status = "已通过";
                }
                else if (s == 2) {
                    status = "未通过";
                }
                else{
                    status = "未知";
                }
                return new SimpleStringProperty(status);
            }
            case "start":{
                LocalDate date = a.getStart();
                String start;
                if(date != null){
                    DateTimeFormatter f = DateTimeFormatter.ofPattern(CommonMethod.DISPLAY_DATE_FORMAT);
                    start = date.format(f);
                }
                else{
                    start = "----";
                }
                return new SimpleStringProperty(start);
            }
            case "end":{
                LocalDate date = a.getEnd();
                String end;
                if(date != null){
                    DateTimeFormatter f = DateTimeFormatter.ofPattern(CommonMethod.DISPLAY_DATE_FORMAT);
                    end = date.format(f);
                }
                else{
                    end = "----";
                }
                return new SimpleStringProperty(end);
            }
            case "maxNumber":{
                String maxNumber;
                Integer max = a.getMaxNumber();
                if(max == null || max < 0){
                    maxNumber = "----";
                }
                else{
                    maxNumber = a.getMaxNumber().toString();
                }
                return new SimpleStringProperty(maxNumber);
            }
            default:{
                return new SimpleStringProperty("----");
            }
        }
    }
}
