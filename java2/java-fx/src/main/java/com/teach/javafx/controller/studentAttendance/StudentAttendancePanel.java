package com.teach.javafx.controller.studentAttendance;

import com.teach.javafx.AppStore;
import com.teach.javafx.controller.base.MessageDialog;
import com.teach.javafx.factories.AttendanceValueFactory;
import com.teach.javafx.request.HttpRequestUtil;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.fatmansoft.teach.models.Attendance;
import org.fatmansoft.teach.models.Course;
import org.fatmansoft.teach.payload.request.DataRequest;
import org.fatmansoft.teach.payload.response.DataResponse;
import org.fatmansoft.teach.util.CommonMethod;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class StudentAttendancePanel {
    @FXML
    public TableColumn<Attendance, String> courseName;
    @FXML
    public TableColumn<Attendance, String> courseNum;
    @FXML
    public TableColumn<Attendance, String> date;
    @FXML
    public TableColumn<Attendance, String> status;
    @FXML
    public TableView<Attendance> tableView;
    @FXML
    public ComboBox<Course> courseComboBox;
    @FXML
    public DatePicker datePicker;

    public List<Attendance> attendanceList = new ArrayList<>();
    public List<Course> courseList = new ArrayList<>();

    public Course emptyCourse = new Course();

    {
        emptyCourse.setCourseId(-1);
    }

    @FXML
    public void initialize(){
        courseName.setCellValueFactory(new AttendanceValueFactory());
        courseNum.setCellValueFactory(new AttendanceValueFactory());
        date.setCellValueFactory(new AttendanceValueFactory());
        status.setCellValueFactory(new AttendanceValueFactory());
        getCourseList();
        courseComboBox.setItems(FXCollections.observableArrayList(courseList));

        onQueryButtonClick();
    }

    //获取所有课程信息
    public void getCourseList(){
        //清空已有课程
        courseList.clear();
        //从后端获取所有课程
        DataRequest req =new DataRequest();
        DataResponse res = HttpRequestUtil.request("/api/course/getCourseList",req);
        assert res != null;
        if(res.getCode() == 0){
            List<Map> rawData = (ArrayList<Map>) res.getData();
            //在第一个位置添加一个空的课程
            courseList.add(emptyCourse);
            for(Map m : rawData){
                Course c = new Course(m);
                courseList.add(c);
            }
        }
    }

    public void onQueryButtonClick(){
        //根据当前查询条件，从后端更新attendanceList
        DataRequest req = new DataRequest();
        req.add("studentId", AppStore.getJwt().getRoleId());
        req.add("courseId", courseComboBox.getValue() == null ? -1 : courseComboBox.getValue().getCourseId());
        if(datePicker.getValue() == null){
            req.add("date", null);
        }
        else{
            LocalDateTime dateTime = LocalDateTime.of(datePicker.getValue(), LocalTime.of(0,0,0));
            String dateTimeString = CommonMethod.getDateTimeString(dateTime, CommonMethod.DATE_TIME_FORMAT);
            req.add("date", dateTimeString);
        }
        DataResponse res = HttpRequestUtil.request("/api/attendance/getStudentAttendanceList",req);
        assert res != null;
        if(res.getCode() == 0){
            attendanceList.clear();
            List<Map> rawData = (ArrayList<Map>) res.getData();
            for(Map m : rawData){
                Attendance a = new Attendance(m);
                attendanceList.add(a);
            }
            setDataView();
        }
        else{
            MessageDialog.showDialog("无法获取考勤信息");
        }
    }

    public void onClearButtonClick(){
        //清除查询条件
        courseComboBox.getSelectionModel().select(null);
        datePicker.setValue(null);

        onQueryButtonClick();
    }

    public void setDataView(){
        tableView.setItems(FXCollections.observableArrayList(attendanceList));
    }
}
