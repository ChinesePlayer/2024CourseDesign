package com.teach.javafx.controller.studentScore;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;
import org.fatmansoft.teach.models.Score;

public class StudentScoreValueFactory implements Callback<TableColumn.CellDataFeatures<Score, String>, ObservableValue<String>> {

    @Override
    public ObservableValue<String> call(TableColumn.CellDataFeatures<Score, String> param) {
        Score s = param.getValue();
        String id = param.getTableColumn().getId();
        switch(id){
            case "courseName"->{
                if(s.getCourseName() == null || s.getCourseName().isEmpty()){
                    return new SimpleStringProperty("----");
                }
                return new SimpleStringProperty(s.getCourseName());
            }
            case "courseNum"->{
                if(s.getCourseNum() == null || s.getCourseNum().isEmpty()){
                    return new SimpleStringProperty("----");
                }
                return new SimpleStringProperty(s.getCourseNum());
            }
            case "credit"->{
                if(s.getCredit() == null){
                    return new SimpleStringProperty("----");
                }
                return new SimpleStringProperty(s.getCredit()+"");
            }
            case "status"->{
                if(s.getStatus() == null){
                    return new SimpleStringProperty("----");
                }
                Integer status = s.getStatus();
                if(status == 0){
                    return new SimpleStringProperty("修读中");
                }
                else if(status == 1){
                    return new SimpleStringProperty("已及格");
                }
                else if(status == 2){
                    return new SimpleStringProperty("不及格");
                }
                else {
                    return new SimpleStringProperty("----");
                }
            }
            case "mark"->{
                if(s.getMark() == null){
                    return new SimpleStringProperty("----");
                }
                return new SimpleStringProperty(s.getMark()+"");
            }
            case "rank"->{
                if(s.getRank() == null){
                    return new SimpleStringProperty("----");
                }
                return new SimpleStringProperty(s.getRank()+"");
            }
            case "gpa"->{
                if(s.getGpa() == null){
                    return new SimpleStringProperty("----");
                }
                if(s.getMark() < 60){
                    return new SimpleStringProperty("----");
                }
                else {
                    return new SimpleStringProperty(String.format("%.1f", s.getGpa()));
                }
            }
        }
        return new SimpleStringProperty("----");
    }
}
