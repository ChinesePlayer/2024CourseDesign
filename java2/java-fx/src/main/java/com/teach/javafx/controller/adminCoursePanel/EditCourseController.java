package com.teach.javafx.controller.adminCoursePanel;

import com.teach.javafx.controller.CourseController;
import io.github.palexdev.materialfx.controls.MFXComboBox;
import io.github.palexdev.materialfx.controls.MFXSpinner;
import io.github.palexdev.materialfx.controls.MFXTextField;
import io.github.palexdev.materialfx.controls.models.spinner.DoubleSpinnerModel;
import javafx.fxml.FXML;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.layout.VBox;
import org.fatmansoft.teach.models.Course;
import org.fatmansoft.teach.models.Teacher;

import java.util.List;

public class EditCourseController {
    @FXML
    public MFXTextField name;
    @FXML
    public MFXSpinner<Double> credit;
    @FXML
    public VBox timeGroup;
    @FXML
    public MFXComboBox teacher;
    @FXML
    public MFXComboBox loc;
    @FXML
    public MFXComboBox preCourse;
    private Course course;

    private CourseController courseController;

    @FXML
    public void initialize(){
        //设置学分增长步长为0.5
        DoubleSpinnerModel model = new DoubleSpinnerModel();
        model.setIncrement(0.5);
        credit.setSpinnerModel(model);
    }

    public void initData(Course c){
        name.setText(c.getName());
        credit.setValue(c.getCredit());
    }


    public CourseController getCourseController() {
        return courseController;
    }

    public void setCourseController(CourseController courseController) {
        this.courseController = courseController;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

//    public Teacher getCurrentTeacher() {
//        return currentTeacher;
//    }
//
//    public void setCurrentTeacher(Teacher currentTeacher) {
//        this.currentTeacher = currentTeacher;
//    }
//
//    public List<Teacher> getTeachers() {
//        return teachers;
//    }
//
//    public void setTeachers(List<Teacher> teachers) {
//        this.teachers = teachers;
//    }
}
