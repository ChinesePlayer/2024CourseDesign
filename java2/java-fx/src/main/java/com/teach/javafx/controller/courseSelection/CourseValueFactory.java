package com.teach.javafx.controller.courseSelection;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;
import org.fatmansoft.teach.models.Course;


public class CourseValueFactory implements Callback<TableColumn.CellDataFeatures<Course, String>, ObservableValue<String>> {
    @Override
    public ObservableValue<String> call(TableColumn.CellDataFeatures<Course, String> param) {
        Course c = param.getValue();
        String id = param.getTableColumn().getId();
        if(param == null){
            return null;
        }
        else if(id.equals("courseName")){
            return new SimpleStringProperty(c.getName());
        }
        else if(id.equals("courseNum")){
            return new SimpleStringProperty(c.getNum());
        }
        else if(id.equals("preCourse")){
            if(c.getPreCourse() == null){
                return new SimpleStringProperty("无");
            }
            return new SimpleStringProperty(c.getPreCourse().getNum() + "-" + c.getPreCourse().getName());
        }
        else if(id.equals("credit")){
            return new SimpleStringProperty(c.getCredit());
        }
        else if(id.equals("teacher")){
            if(c.getTeacher() == null || c.getTeacher().isEmpty()){
                return new SimpleStringProperty("暂未公布");
            }
            else {
                return new SimpleStringProperty(c.getTeacher());
            }
        }
        else if(id.equals("loc")){
            if(c.getLocation() == null || c.getLocation().isEmpty()){
                return new SimpleStringProperty("暂未公布");
            }
            else {
                return new SimpleStringProperty(c.getLocation());
            }
        }
        return new SimpleStringProperty("----");
    }
}
