package com.teach.javafx.controller;

import com.teach.javafx.request.HttpRequestUtil;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.SelectionMode;
import org.fatmansoft.teach.models.Course;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import org.fatmansoft.teach.payload.request.DataRequest;

import java.util.ArrayList;
import java.util.List;


public class CourseInfoController {
    @FXML
    private TableView<Course> courseTableView;
    @FXML
    private TableColumn<Course, String> courseName;
    @FXML
    private TableColumn<Course, String> courseNum;
    @FXML
    private TableColumn<Course, String> credit;
    //前序课程，格式: 课程编号-课程名
    @FXML
    private TableColumn<Course, String> preCourse;

    @FXML
    private MFXTextField searchField;

    @FXML
    private MFXButton searchButton;

    //用于储存课程
    private List<Course> courseList = new ArrayList<Course>();
    //用于展示课程
    private ObservableList<Course> observableList = FXCollections.observableArrayList();

    //根据courseList来设置observableList
    public void setTableViewData(){
        observableList.clear();
//        for(int i = 0; i < courseList.size(); i++){
//            observableList.add(courseList.get(i));
//        }
        //使用addAll方法避免显式循环
        observableList.addAll(FXCollections.observableArrayList(courseList));
        //设置显示数据源
        courseTableView.setItems(observableList);
    }

    @FXML
    public void initialize(){
        courseName.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getName()));
        courseNum.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getNum()));
        credit.setCellValueFactory(cellData -> new SimpleStringProperty(String.valueOf(cellData.getValue().getCredit())));
        preCourse.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getPreCourse().getNum() + "-" + cellData.getValue().getPreCourse().getName()));
        //设置只能选中一个
        courseTableView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        //通过网络请求获取课程列表
        getCourseList();
        setTableViewData();
    }

    public void getCourseList(){
        //搜索框的文本
        String searchText = searchField.getText();
        DataRequest req = new DataRequest();
        req.add("searchText", searchText);
        List<Course> updatedCourseList = HttpRequestUtil.requestDataList("/api/course/getStudentCourseList",req);
        if(updatedCourseList == null || updatedCourseList.isEmpty()){
            return;
        }
        courseList = updatedCourseList;
    }
}
