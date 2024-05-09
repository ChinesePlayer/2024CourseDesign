package com.teach.javafx.controller.teacherCourse;

import com.teach.javafx.controller.courseSelection.CourseActionValueFactory;
import com.teach.javafx.controller.courseSelection.CourseValueFactory;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;
import org.fatmansoft.teach.models.Course;
import org.fatmansoft.teach.payload.request.DataRequest;
import org.fatmansoft.teach.payload.response.DataResponse;

import java.util.ArrayList;
import java.util.List;

public class MyTaughtController {
    @FXML
    public TableView<Course> courseTableView;
    @FXML
    public TableColumn<Course, String> courseNum;
    @FXML
    public TableColumn<Course, String> courseName;
    @FXML
    public TableColumn<Course, String> day;
    @FXML
    public TableColumn<Course, String> section;
    @FXML
    public TableColumn<Course, String> loc;
    @FXML
    public TableColumn<Course, HBox> action;

    private List<Course> courseList = new ArrayList<>();

    @FXML
    public void initialize(){
        //设置列工厂属性
        courseName.setCellValueFactory(new CourseValueFactory());
        courseNum.setCellValueFactory(new CourseValueFactory());
        day.setCellValueFactory(new CourseValueFactory());
        section.setCellValueFactory(new CourseValueFactory());
        loc.setCellValueFactory(new CourseValueFactory());
        action.setCellValueFactory(new CourseActionValueFactory());
    }

    private void getCourseList(){
        DataRequest req = new DataRequest();

    }
}
