package com.teach.javafx.controller.adminCoursePanel;

import com.teach.javafx.controller.CourseController;
import com.teach.javafx.customWidget.TimeSelector;
import com.teach.javafx.request.HttpRequestUtil;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXComboBox;
import io.github.palexdev.materialfx.controls.MFXSpinner;
import io.github.palexdev.materialfx.controls.MFXTextField;
import io.github.palexdev.materialfx.controls.models.spinner.DoubleSpinnerModel;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.layout.VBox;
import org.fatmansoft.teach.models.Course;
import org.fatmansoft.teach.models.CourseTime;
import org.fatmansoft.teach.models.Teacher;
import org.fatmansoft.teach.payload.request.DataRequest;
import org.fatmansoft.teach.payload.response.DataResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class EditCourseController {
    @FXML
    public MFXTextField name;
    @FXML
    public MFXSpinner<Double> credit;
    @FXML
    public VBox timeGroup;
    @FXML
    public MFXComboBox<Teacher> teacher;
    @FXML
    public MFXComboBox<String> loc;
    @FXML
    public MFXComboBox<Course> preCourse;
    private Course course;

    private CourseController courseController;
    private List<Teacher> teacherList = new ArrayList<>();

    @FXML
    public void initialize(){
        //设置学分增长步长为0.5
        DoubleSpinnerModel model = new DoubleSpinnerModel();
        model.setIncrement(0.5);
        credit.setSpinnerModel(model);
    }

    //  从后端获取所有老师数据
    public void getTeacherList(){
        DataRequest req = new DataRequest();
        DataResponse res = HttpRequestUtil.request("/api/teacher/getTeacherList", req);
        if(res != null && res.getCode() == 0){
            Teacher t = new Teacher((Map) res.getData());
            teacherList.add(t);
        }

    }

    public void initData(Course c){
        name.setText(c.getName());
        credit.setValue(c.getCredit());
        getTeacherList();
        teacher.setItems(FXCollections.observableArrayList(teacherList));
        //选中当前老师
        teacher.setValue(c.getTeacher());
        if(c.getCourseTimes() != null){
            for(CourseTime ct : c.getCourseTimes()){
                MFXButton button = new MFXButton("删除");
                button.setOnAction(this::onDeleteTimeSelector);
                TimeSelector ts = new TimeSelector(ct.getDay(), ct.getSection(), button);
                timeGroup.getChildren().add(ts);
            }
        }
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

    public void addTimeSelector(){
        MFXButton button = new MFXButton("删除");
        button.setOnAction(this::onDeleteTimeSelector);
        timeGroup.getChildren().add(new TimeSelector(button));
    }

    public void onDeleteTimeSelector(ActionEvent event){
        TimeSelector timeSelector = (TimeSelector) ((MFXButton)event.getTarget()).getParent();
        timeGroup.getChildren().remove(timeSelector);
    }
}
