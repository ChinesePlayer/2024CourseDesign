package com.teach.javafx.controller.adminAttendance;

import com.teach.javafx.controller.base.AttendanceEditorOpener;
import com.teach.javafx.controller.base.MessageDialog;
import com.teach.javafx.models.Student;
import com.teach.javafx.request.HttpRequestUtil;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.stage.Stage;
import org.fatmansoft.teach.models.Attendance;
import org.fatmansoft.teach.models.Course;
import org.fatmansoft.teach.payload.request.DataRequest;
import org.fatmansoft.teach.payload.response.DataResponse;
import org.fatmansoft.teach.util.CommonMethod;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class AttendanceEditorController {
    public final String PRESENCE = "出勤";
    public final String ABSENCE = "缺勤";
    @FXML
    public ComboBox<Course> courseCombo;
    @FXML
    public ComboBox<Student> studentCombo;
    @FXML
    public ComboBox<String> statusCombo;
    @FXML
    public DatePicker datePicker;


    public List<Student> studentList = new ArrayList<>();
    public List<Course> courseList = new ArrayList<>();
    public Attendance attendance;
    public AttendanceEditorOpener opener;

    @FXML
    public void initialize(){
        statusCombo.setItems(FXCollections.observableArrayList(PRESENCE, ABSENCE));
        getStudentList();
        getCourseList();
        courseCombo.setItems(FXCollections.observableArrayList(courseList));
        studentCombo.setItems(FXCollections.observableArrayList(studentList));
    }

    //外部调用的初始化方法
    public void init(Attendance a, AttendanceEditorOpener ope){
        this.opener = ope;
        this.attendance = a;
        setDataView();
    }

    public void getStudentList(){
        //清空已有学生
        studentList.clear();
        //从后端获取所有学生
        DataRequest req =new DataRequest();
        DataResponse res = HttpRequestUtil.request("/api/student/getStudentList",req);
        assert res != null;
        if(res.getCode() == 0){
            List<Map> rawData = (ArrayList<Map>) res.getData();
            for(Map m : rawData){
                Student s = new Student(m);
                studentList.add(s);
            }
            System.out.println(studentList.size());
        }
    }

    public void getCourseList(){
        //清空已有课程
        courseList.clear();
        //从后端获取所有课程
        DataRequest req =new DataRequest();
        DataResponse res = HttpRequestUtil.request("/api/course/getCourseList",req);
        assert res != null;
        if(res.getCode() == 0){
            List<Map> rawData = (ArrayList<Map>) res.getData();
            for(Map m : rawData){
                Course c = new Course(m);
                courseList.add(c);
            }
        }
    }

    //根据学生ID查找Student对象
    public Student findStudentById(List<Student> sL, Integer studentId){
        for(Student s : sL){
            if(s != null && s.getStudentId() != null && Objects.equals(s.getStudentId(), studentId)){
                return s;
            }
        }
        return null;
    }

    //根据课程ID查找Course对象
    public Course findCourseById(List<Course> cL, Integer courseId){
        for(Course c : cL){
            if(c != null && c.getCourseId() != null && Objects.equals(c.getCourseId(), courseId)){
                return c;
            }
        }
        return null;
    }

    //初始化视图(如果有外部传来的初始数据的话)
    public void setDataView(){
        if(attendance == null){
            return;
        }
        Student currentStudent = findStudentById(studentList,attendance.getStudentId());
        Course currentCourse = findCourseById(courseList, attendance.getCourseId());
        courseCombo.getSelectionModel().select(currentCourse);
        studentCombo.getSelectionModel().select(currentStudent);
        statusCombo.getSelectionModel().select(attendance.getStatus());
        datePicker.setValue(attendance.getDate().toLocalDate());
    }

    public void onSubmit(){
        //检查数据有效性
        //课程不能为空
        if(courseCombo.getSelectionModel().getSelectedItem() == null){
            MessageDialog.showDialog("请选择课程!");
            return;
        }
        //学生不能为空
        if(studentCombo.getSelectionModel().getSelectedItem() == null){
            MessageDialog.showDialog("请选择学生!");
            return;
        }
        //考勤状态不能为空
        if(statusCombo.getSelectionModel().getSelectedItem() == null){
            MessageDialog.showDialog("请选择考勤状态!");
            return;
        }
        if(datePicker.getValue() == null){
            MessageDialog.showDialog("请选择考勤日期! ");
            return;
        }
        Integer courseId = courseCombo.getSelectionModel().getSelectedItem().getCourseId();
        Integer studentId = studentCombo.getSelectionModel().getSelectedItem().getStudentId();
        String status = statusCombo.getSelectionModel().getSelectedItem();
        LocalDate date = datePicker.getValue();
        //将日期对象转化为日期时间对象，以让后端能够从中解析数据，时间信息设为0时0分0秒
        LocalDateTime dateTime = LocalDateTime.of(date, LocalTime.of(0,0,0));

        //向后端发送数据
        DataRequest req = new DataRequest();
        req.add("courseId", courseId);
        req.add("studentId", studentId);
        req.add("status", status);
        req.add("date", CommonMethod.getDateTimeString(dateTime, CommonMethod.DATE_TIME_FORMAT));
        req.add("attendanceId", attendance == null ? null : attendance.getAttendanceId());

        DataResponse res = HttpRequestUtil.request("/api/attendance/saveAttendance",req);
        assert res != null;
        if(res.getCode() == 0){
            opener.hasSaved();
            //关闭该窗口
            ((Stage)studentCombo.getScene().getWindow()).close();
        }
        else{
            MessageDialog.showDialog(res.getMsg());
        }
    }
}
