package com.teach.javafx.controller;

import com.teach.javafx.request.HttpRequestUtil;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.MapValueFactory;
import javafx.util.Callback;
import org.fatmansoft.teach.models.Course;
import org.fatmansoft.teach.payload.request.DataRequest;
import org.fatmansoft.teach.payload.response.DataResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class CourseSelectionController {
    @FXML
    public TableView<Map> courseTableView;
    @FXML
    public TableColumn<Map, String> courseName;
    @FXML
    public TableColumn<Map, String> courseNum;
    @FXML
    public TableColumn<Map, String> credit;
    @FXML
    public TableColumn<Map, String> preCourse;
    @FXML
    public TableColumn<Map, Button> action;

    private List<Map> courses = new ArrayList<>();
    private ObservableList<Map> observableList = FXCollections.observableArrayList();

    public void setTableViewData(){
        observableList.clear();
        observableList.addAll(FXCollections.observableArrayList(courses));
        courseTableView.setItems(observableList);
    }

    //通过网络请求获得所有课程
    public void getCourses(){
        DataRequest req = new DataRequest();
        DataResponse res = HttpRequestUtil.request("/api/course/getCourseList", req);
        if(res != null && res.getCode() == 0){
            courses = (ArrayList<Map>)res.getData();
            System.out.println(res.getData());
            System.out.println(courses);
        }
    }

    public Map findCourseById(Integer id){
        for(Map c : courses){
            if(c.get("courseId").equals(String.valueOf(id))){
                return c;
            }
        }
        return null;
    }

    @FXML
    public void initialize(){
        courseName.setCellValueFactory(new MapValueFactory<>("name"));
        courseNum.setCellValueFactory(new MapValueFactory<>("num"));
        preCourse.setCellValueFactory(cellData -> {
            if(cellData == null){
                return null;
            }
            Map value = (Map)cellData.getValue();
            if(value.get("preCourseId") == null){
                return new SimpleStringProperty("无");
            }
            Integer preCourseId = Integer.parseInt((String) value.get("preCourseId"));
            Map preCourse = findCourseById(preCourseId);
            if(preCourse == null){
                return new SimpleStringProperty("无");
            }
            return new SimpleStringProperty(preCourse.get("num") + "-" + preCourse.get("name"));
        });
        credit.setCellValueFactory(new MapValueFactory<>("credit"));
        action.setCellValueFactory(new MapValueFactory<>("action"));

        getCourses();
        setTableViewData();
    }
}
