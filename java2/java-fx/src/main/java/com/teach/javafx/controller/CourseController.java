package com.teach.javafx.controller;

import com.teach.javafx.MainApplication;
import com.teach.javafx.controller.adminCoursePanel.EditCourseController;
import com.teach.javafx.controller.courseSelection.CourseAdminActionValueFactory;
import com.teach.javafx.controller.courseSelection.CourseTimeValueFactory;
import com.teach.javafx.controller.courseSelection.CourseValueFactory;
import com.teach.javafx.request.HttpRequestUtil;
import io.github.palexdev.materialfx.controls.MFXButton;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Callback;
import org.fatmansoft.teach.models.Course;
import org.fatmansoft.teach.payload.request.DataRequest;
import org.fatmansoft.teach.payload.response.DataResponse;

import javax.swing.*;
import java.io.IOException;
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
    public TableColumn<Course, String> days;
    @FXML
    public TableColumn<Course, String> sections;
    @FXML
    public TableColumn<Course, String> teacher;
    @FXML
    public TableColumn<Course, String> loc;
    @FXML
    public TableColumn<Course, MFXButton> action;
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

    //编辑页面的Stage
    private Stage editStage = null;

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
                MFXButton button = new MFXButton("编辑");
                c.setAction(button);
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
        courseNum.setCellValueFactory(new CourseValueFactory());
        courseName.setCellValueFactory(new CourseValueFactory());
        credit.setCellValueFactory(new CourseValueFactory());
        preCourse.setCellValueFactory(new CourseValueFactory());
        loc.setCellValueFactory(new CourseValueFactory());
        days.setCellValueFactory(new CourseTimeValueFactory());
        sections.setCellValueFactory(new CourseTimeValueFactory());
        teacher.setCellValueFactory(new CourseValueFactory());
        action.setCellValueFactory(new CourseAdminActionValueFactory());
        //设置按钮为居中显示
        action.setCellFactory(new Callback<>() {
            @Override
            public TableCell<Course, MFXButton> call(TableColumn<Course, MFXButton> courseMFXButtonTableColumn) {
                TableCell<Course, MFXButton> cell = new TableCell<>() {
                    @Override
                    protected void updateItem(MFXButton item, boolean empty) {
                        super.updateItem(item, empty);
                        if (item == null || empty) {
                            setText(null);
                            setGraphic(null);
                            return;
                        }
                        setText(null);
                        setGraphic(item);
                    }
                };
                cell.setAlignment(Pos.CENTER);
                return cell;
            }
        });
        onQueryButtonClick();
    }

    public void onEditButtonClick(ActionEvent event){
        TableCell<Course, MFXButton> cell = (TableCell<Course, MFXButton>) ((MFXButton)event.getTarget()).getParent();
        int rowIndex = cell.getIndex();
        Course c = observableList.get(rowIndex);
        FXMLLoader loader = new FXMLLoader(MainApplication.class.getResource("adminCoursePanel/edit-course.fxml"));
        try{
            Scene scene = new Scene(loader.load(), 500, 600);
            EditCourseController editCourseController = (EditCourseController) loader.getController();

            editStage = new Stage();
            editStage.setScene(scene);
            editStage.setResizable(false);
            editStage.initOwner(dataTableView.getScene().getWindow());
            editStage.initModality(Modality.WINDOW_MODAL);
            editStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
                @Override
                public void handle(WindowEvent windowEvent) {
                    editStage = null;
                }
            });
            editStage.show();
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }
}
