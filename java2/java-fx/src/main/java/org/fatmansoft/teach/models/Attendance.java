package org.fatmansoft.teach.models;

import javafx.scene.control.Button;
import org.fatmansoft.teach.util.CommonMethod;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Attendance {
    private Integer attendanceId;
    private Integer studentId;
    private String studentName;
    private String studentNum;
    private String courseName;
    private Integer courseId;
    private String courseNum;
    private LocalDateTime date;
    private String status;
    //可以保存一些按钮，按钮绑定的事件可以对Attendance本身进行一些操作
    private List<Button> actions = new ArrayList<>();
    public Attendance(){

    }

    public Attendance(Map m){
        this.studentId = CommonMethod.getInteger(m, "studentId");
        this.studentName = CommonMethod.getString(m, "studentName");
        this.courseName = CommonMethod.getString(m, "courseName");
        this.courseId = CommonMethod.getInteger(m, "courseId");
        this.date = CommonMethod.getDateTime(m, "date");
        this.status = CommonMethod.getString(m, "status");
        this.attendanceId = CommonMethod.getInteger(m, "attendanceId");
        this.courseNum = CommonMethod.getString(m, "courseNum");
        this.studentNum = CommonMethod.getString(m,"studentNum");
    }


    public Integer getStudentId() {
        return studentId;
    }

    public void setStudentId(Integer studentId) {
        this.studentId = studentId;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getAttendanceId() {
        return attendanceId;
    }

    public void setAttendanceId(Integer attendanceId) {
        this.attendanceId = attendanceId;
    }


    public List<Button> getActions() {
        return actions;
    }

    public void setActions(List<Button> actions) {
        this.actions = actions;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public Integer getCourseId() {
        return courseId;
    }

    public void setCourseId(Integer courseId) {
        this.courseId = courseId;
    }

    public String getStudentNum() {
        return studentNum;
    }

    public void setStudentNum(String studentNum) {
        this.studentNum = studentNum;
    }

    public String getCourseNum() {
        return courseNum;
    }

    public void setCourseNum(String courseNum) {
        this.courseNum = courseNum;
    }
}
