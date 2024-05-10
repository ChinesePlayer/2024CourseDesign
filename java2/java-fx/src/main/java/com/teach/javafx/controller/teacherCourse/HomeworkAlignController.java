package com.teach.javafx.controller.teacherCourse;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import org.fatmansoft.teach.models.Course;

public class HomeworkAlignController {
    @FXML
    public TextField titleField;
    @FXML
    public TextArea contentArea;
    private Course course;
    private ViewHomeworkController viewHomeworkController;

    @FXML
    public void initialize(){

    }

    public void init(Course c, ViewHomeworkController controller){
        this.course = c;
        this.viewHomeworkController = controller;
    }

    //提交作业
    public void onSubmit(ActionEvent event){
        
    }
}
