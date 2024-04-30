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
        switch (id) {
            case "courseName" -> {
                return new SimpleStringProperty(c.getName());
            }
            case "courseNum" -> {
                return new SimpleStringProperty(c.getNum());
            }
            case "preCourse" -> {
                if (c.getPreCourse() == null) {
                    return new SimpleStringProperty("无");
                }
                return new SimpleStringProperty(c.getPreCourse().getNum() + "-" + c.getPreCourse().getName());
            }
            case "credit" -> {
                return new SimpleStringProperty(String.valueOf(c.getCredit()));
            }
            case "teacher" -> {
                if (c.getTeacher() == null) {
                    return new SimpleStringProperty("暂未公布");
                } else {
                    return new SimpleStringProperty(c.getTeacher().getName());
                }
            }
            case "loc" -> {
                if (c.getLocation() == null) {
                    return new SimpleStringProperty("暂未公布");
                } else {
                    return new SimpleStringProperty(c.getLocation().getValue());
                }
            }
            case "status" -> {
                if (c.getStatus() == null) {
                    return new SimpleStringProperty("未知");
                }
                if (c.getStatus() != null) {
                    return switch (c.getStatus()) {
                        case 0 -> new SimpleStringProperty("修读中");
                        case 1 -> new SimpleStringProperty("已及格");
                        case 2 -> new SimpleStringProperty("未及格");
                        default -> new SimpleStringProperty("未知");
                    };
                }
            }
        }
        return new SimpleStringProperty("----");
    }
}
