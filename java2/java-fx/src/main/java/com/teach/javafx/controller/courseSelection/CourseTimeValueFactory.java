package com.teach.javafx.controller.courseSelection;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;
import org.fatmansoft.teach.models.Course;
import org.fatmansoft.teach.models.CourseTime;

import java.util.List;

public class CourseTimeValueFactory implements Callback<TableColumn.CellDataFeatures<Course, String>, ObservableValue<String>> {
    @Override
    public ObservableValue<String> call(TableColumn.CellDataFeatures<Course, String> param) {
        Course c = param.getValue();
        List<CourseTime> cts = c.getCourseTimes();
        String id = param.getTableColumn().getId();
        if(id.equals("days")){
            String res = "";
            for(int i = 0 ; i < cts.size(); i++){
                res += cts.get(i).getDay();
                if(i < cts.size() - 1){
                    res += ",";
                }
            }
            if(res.equals("")){
                res = "暂无";
            }
            return new SimpleStringProperty(res);
        }
        else if(id.equals("sections")){
            String res = "";
            for(int i = 0 ; i < cts.size(); i++){
                res += cts.get(i).getSection();
                if(i < cts.size() - 1){
                    res += ",";
                }
            }
            if(res.equals("")){
                res = "暂无";
            }
            return new SimpleStringProperty(res);
        }
        return new SimpleStringProperty("");
    }
}
