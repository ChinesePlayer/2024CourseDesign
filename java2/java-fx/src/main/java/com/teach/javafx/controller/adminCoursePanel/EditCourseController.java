package com.teach.javafx.controller.adminCoursePanel;

import com.teach.javafx.controller.CourseController;
import com.teach.javafx.controller.base.MessageDialog;
import com.teach.javafx.customWidget.CourseCell;
import com.teach.javafx.customWidget.TimeSelector;
import com.teach.javafx.request.HttpRequestUtil;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXComboBox;
import io.github.palexdev.materialfx.controls.MFXSpinner;
import io.github.palexdev.materialfx.controls.MFXTextField;
import io.github.palexdev.materialfx.controls.models.spinner.DoubleSpinnerModel;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.fatmansoft.teach.models.Course;
import org.fatmansoft.teach.models.CourseLocation;
import org.fatmansoft.teach.models.CourseTime;
import org.fatmansoft.teach.models.Teacher;
import org.fatmansoft.teach.payload.request.DataRequest;
import org.fatmansoft.teach.payload.response.DataResponse;

import java.sql.Time;
import java.util.*;

public class EditCourseController {
    @FXML
    public MFXTextField name;
    @FXML
    public MFXSpinner<Double> credit;
    @FXML
    public VBox timeGroup;
    @FXML
    public ComboBox<Teacher> teacher;
    @FXML
    public ComboBox<CourseLocation> loc;
    @FXML
    public ComboBox<Course> preCourse;
    @FXML
    public MFXButton save;
    @FXML
    public MFXButton cancel;
    @FXML
    public MFXTextField num;
    private Course course;

    private CourseController courseController;
    private List<Teacher> teacherList = new ArrayList<>();
    private List<CourseLocation> locationList = new ArrayList<>();
    private List<Course> preCourseList = new ArrayList<>();



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
        List<Teacher> res = HttpRequestUtil.requestDataList("/api/teacher/getTeacherList", req);
        if(res != null && !res.isEmpty()){
            teacherList.clear();
            for(Teacher m : res){
                m.setName(m.getPerson().getName());
                teacherList.add(m);
            }
        }
    }

    public void getPreCourseList(){
        DataRequest req = new DataRequest();
        DataResponse res = HttpRequestUtil.request("/api/course/getCourseList", req);
        if(res != null && res.getCode() == 0){
            preCourseList.clear();
            //在第一位加入一个为ID为-1的空课程
            Course emptyCourse = new Course();
            emptyCourse.setCourseId(-1);
            preCourseList.add(emptyCourse);
            List<Map> maps = (ArrayList<Map>) res.getData();
            for(Map m : maps){
                preCourseList.add(new Course(m));
            }
            //从列表中删除该课程本身，因为自己不能是自己的前序课程
            preCourseList.remove(course);
        }
    }

    public void getCourseLocations(){
        DataRequest req = new DataRequest();
        DataResponse res = HttpRequestUtil.request("/api/location/getCourseLocationList", req);
        if(res != null && res.getCode() == 0){
            locationList.clear();

            List<Map> maps = (ArrayList<Map>) res.getData();
            for(Map m : maps){
                CourseLocation cl = new CourseLocation(m);
                locationList.add(cl);
            }
        }
        else{
            assert res != null;
            MessageDialog.showDialog(res.getMsg());
        }
    }

    public void initData(Course c, CourseController courseController){
        course = c;
        name.setText(c.getName());
        credit.setValue(c.getCredit());
        num.setText(c.getNum());

        getTeacherList();
        teacher.setItems(FXCollections.observableArrayList(teacherList));
        //选中当前老师
        teacher.setValue(c.getTeacher());

        getCourseLocations();
        loc.setItems(FXCollections.observableArrayList(locationList));
        //选中当前地点
        loc.setValue(c.getLocation());

        getPreCourseList();
        preCourse.setItems(FXCollections.observableArrayList(preCourseList));
        //选中当前前序课程
        preCourse.setValue(c.getPreCourse());

        if(c.getCourseTimes() != null){
            for(CourseTime ct : c.getCourseTimes()){
                MFXButton button = new MFXButton("删除");
                button.setOnAction(this::onDeleteTimeSelector);
                TimeSelector ts = new TimeSelector(ct.getDay(), ct.getSection(), button);
                timeGroup.getChildren().add(ts);
                timeGroup.setAlignment(Pos.TOP_CENTER);
            }
        }
        setCourseController(courseController);

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

    public void onCancelButton(){
        Stage stage = (Stage) teacher.getScene().getWindow();
        stage.close();
    }

    public void updateCourseFromForm(){
        course.setName(name.getText());
        course.setCredit(credit.getValue());
        if(Objects.equals(preCourse.getValue().getCourseId(), -1)){
            course.setPreCourse(null);
        }
        else{
            course.setPreCourse(preCourse.getValue());
        }
        course.setTeacher(teacher.getValue());
        course.setLocation(loc.getValue());
        course.setNum(num.getText());
        List<CourseTime> cts = new ArrayList<>();
        for(Node n : timeGroup.getChildren()){
            TimeSelector ts = (TimeSelector) n;
            CourseTime ct = new CourseTime(ts.getDay(), ts.getSection());
            cts.add(ct);
        }
        course.setCourseTimes(cts);
    }

    //判断新输入的数据是否合法
    public boolean isDataValid(){
        List<TimeSelector> tss = new ArrayList<>();
        //将节点转移到tss数组中
        for(Node n : timeGroup.getChildren()){
            tss.add((TimeSelector) n);
        }
        for(int i = 0 ; i < tss.size() ; i++){
            for(int j = 0 ; j < tss.size(); j++){
                if(i == j){
                    continue;
                }
                if(tss.get(i).equals(tss.get(j))){
                    return false;
                }
            }
        }
        return true;
    }

    public void onSaveButton(){
        if(!isDataValid()){
            MessageDialog.showDialog("上课时间有重复，请检查!");
            return;
        }
        DataRequest req = new DataRequest();
        req.add("courseId", course.getCourseId());
        //注意此处的名字为新名字
        req.add("courseName", name.getText());
        req.add("num", num.getText());
        req.add("locationId", loc.getValue().getId());
        req.add("teacherId", teacher.getValue().getTeacherId());
        req.add("credit", credit.getValue());
        if(preCourse.getValue() == null){
            req.add("preCourseId", null);
        }
        else{
            req.add("preCourseId", preCourse.getValue().getCourseId());
        }
        List<Map> timeMapList = new ArrayList<>();
        for(Node n : timeGroup.getChildren()){
            TimeSelector ts = (TimeSelector) n;
            Map m = new HashMap<>();
            m.put("day", ts.getDay());
            m.put("section", ts.getSection());
            timeMapList.add(m);
        }
        req.add("times", timeMapList);
        DataResponse res = HttpRequestUtil.request("/api/course/courseSave", req);
        if(res != null && res.getCode() == 0){
            Map returnData = (Map) res.getData();
            Integer courseId = Integer.parseInt((String) returnData.get("courseId"));
            updateCourseFromForm();
            courseController.onHasSavedCourse(course);
            System.out.println("修改的课程ID: " + courseId);
            //关闭窗口
            onCancelButton();
        }
        else {
            MessageDialog.showDialog(res.getMsg());
        }
    }

}
