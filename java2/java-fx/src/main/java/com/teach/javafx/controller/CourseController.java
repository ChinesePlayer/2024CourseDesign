package com.teach.javafx.controller;

import com.teach.javafx.controller.courseSelection.CourseValueFactory;
import com.teach.javafx.request.HttpRequestUtil;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import org.fatmansoft.teach.models.Course;
import org.fatmansoft.teach.payload.request.DataRequest;
import org.fatmansoft.teach.payload.response.DataResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * CourseController 登录交互控制类 对应 course-panel.fxml
 *  @FXML  属性 对应fxml文件中的
 *  @FXML 方法 对应于fxml文件中的 on***Click的属性
 */
public class CourseController {
    @FXML
    private TableView<Course> dataTableView;
    @FXML
    private TableColumn<Course,String> courseNum;
    @FXML
    private TableColumn<Course,String> courseName;
    @FXML
    private TableColumn<Course,String> credit;
    @FXML
    private TableColumn<Course,String> preCourse;

    private List<Course> courseList = new ArrayList();  // 课程信息列表数据
    private ObservableList<Course> observableList= FXCollections.observableArrayList();  // TableView渲染列表

    @FXML
    private void onQueryButtonClick(){
        DataResponse res;
        DataRequest req =new DataRequest();
        res = HttpRequestUtil.request("/api/course/getCourseList",req); //从后台获取所有课程信息列表集合
        if(res != null && res.getCode()== 0) {
            List<Map> rawData = (ArrayList<Map>) res.getData();
            for(Map m : rawData){
                Course c = new Course(m);
                courseList.add(c);
            }
        }
        setTableViewData();
    }

    private void setTableViewData() {
        observableList.clear();
        for (int j = 0; j < courseList.size(); j++) {
            observableList.addAll(FXCollections.observableArrayList(courseList.get(j)));
        }
        dataTableView.setItems(observableList);
    }
    @FXML
    public void initialize() {
        courseNum.setCellValueFactory(new CourseValueFactory());  //设置列值工程属性
        courseName.setCellValueFactory(new CourseValueFactory());
        credit.setCellValueFactory(new CourseValueFactory());
        preCourse.setCellValueFactory(new CourseValueFactory());
        onQueryButtonClick();
    }


}
