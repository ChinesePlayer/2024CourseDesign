package com.teach.javafx.factories;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;
import org.fatmansoft.teach.models.Attendance;
import org.fatmansoft.teach.util.CommonMethod;

import java.time.format.DateTimeFormatter;


public class AttendanceValueFactory implements Callback<TableColumn.CellDataFeatures<Attendance, String>, ObservableValue<String>> {
    @Override
    public ObservableValue<String> call(TableColumn.CellDataFeatures<Attendance, String> param) {
        Attendance a = param.getValue();
        String id = param.getTableColumn().getId();
        switch (id) {
            case "courseName" -> {
                return new SimpleStringProperty(a.getCourseName());
            }
            case "status" -> {
                return new SimpleStringProperty(a.getStatus());
            }
            case "studentName" -> {
                return new SimpleStringProperty(a.getStudentName());
            }
            case "courseNum" -> {
                return new SimpleStringProperty(a.getCourseNum());
            }
            case "studentNum" -> {
                return new SimpleStringProperty(a.getStudentNum());
            }
            case "date" -> {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern(CommonMethod.DISPLAY_DATE_FORMAT);
                String date = a.getDate().toLocalDate().format(formatter);
                return new SimpleStringProperty(date);
            }
        }
        return new SimpleStringProperty("----");
    }
}
