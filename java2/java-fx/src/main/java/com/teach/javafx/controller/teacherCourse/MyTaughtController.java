package com.teach.javafx.controller.teacherCourse;

import com.teach.javafx.AppStore;
import com.teach.javafx.MainApplication;
import com.teach.javafx.controller.base.MessageDialog;
import com.teach.javafx.controller.courseSelection.CourseActionValueFactory;
import com.teach.javafx.controller.courseSelection.CourseTimeValueFactory;
import com.teach.javafx.controller.courseSelection.CourseValueFactory;
import com.teach.javafx.managers.WindowOpenAction;
import com.teach.javafx.managers.WindowsManager;
import com.teach.javafx.request.HttpRequestUtil;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.fatmansoft.teach.models.Course;
import org.fatmansoft.teach.payload.request.DataRequest;
import org.fatmansoft.teach.payload.response.DataResponse;
import org.fatmansoft.teach.util.CommonMethod;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MyTaughtController {
    @FXML
    public TableView<Course> courseTableView;
    @FXML
    public TableColumn<Course, String> courseNum;
    @FXML
    public TableColumn<Course, String> courseName;
    @FXML
    public TableColumn<Course, String> days;
    @FXML
    public TableColumn<Course, String> sections;
    @FXML
    public TableColumn<Course, String> loc;
    @FXML
    public TableColumn<Course, HBox> action;

    private List<Course> courseList = new ArrayList<>();
    private ObservableList<Course> observableList = FXCollections.observableArrayList();

    private Stage studentViewerStage;

    @FXML
    public void initialize(){
        //设置列工厂属性
        courseName.setCellValueFactory(new CourseValueFactory());
        courseNum.setCellValueFactory(new CourseValueFactory());
        days.setCellValueFactory(new CourseTimeValueFactory());
        sections.setCellValueFactory(new CourseTimeValueFactory());
        loc.setCellValueFactory(new CourseValueFactory());
        action.setCellValueFactory(new CourseActionValueFactory());
        update();
    }

    private void getCourseList(){
        DataRequest req = new DataRequest();
        //获取老师ID并加入请求参数中
        req.add("teacherId", AppStore.getJwt().getRoleId());
        DataResponse res = HttpRequestUtil.request("/api/course/getTeacherCourseList", req);
        assert res != null;
        if(res.getCode() == 0){
            List<Map> rawData = (ArrayList<Map>)res.getData();
            for(Map m : rawData){
                Course c = new Course(m);
                //添加按钮
                //学生管理按钮
                Button checkStudent = new Button("学生管理");
                checkStudent.setOnAction(this::onViewStudentClick);

                Button viewHomework = new Button("作业管理");
                viewHomework.setOnAction(this::onViewHomework);

                List<Button> buttonList = new ArrayList<>();
                buttonList.add(checkStudent);
                buttonList.add(viewHomework);
                c.setAction(buttonList);

                courseList.add(c);
            }
        }
    }

    //更新显示数据
    private void updateViewData(){
        observableList.clear();
        observableList.addAll(courseList);
        courseTableView.setItems(observableList);
    }

    public void update(){
        //获取所有数据并更新显示
        getCourseList();
        updateViewData();
    }

    //查看某个课程的所有学生
    public void onViewStudentClick(ActionEvent event){
        Course c = (Course) CommonMethod.getRowValue(event, 2, courseTableView);
        FXMLLoader loader = new FXMLLoader(MainApplication.class.getResource("teacherCourse/student-viewer.fxml"));
        try{
            studentViewerStage = WindowsManager.getInstance().openNewWindow(
                    loader, 900, 600, "学生管理",
                    courseTableView.getScene().getWindow(), Modality.WINDOW_MODAL,
                    new WindowOpenAction() {
                        @Override
                        public void init(Object controller) {
                            WindowOpenAction.super.init(controller);
                            ((StudentViewerController)controller).init(c);
                        }
                    }
            );
            studentViewerStage.setOnCloseRequest(windowEvent -> studentViewerStage = null);
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }



    //查看作业回调
    private void onViewHomework(ActionEvent event){
        Course c = (Course) CommonMethod.getRowValue(event, 2, courseTableView);
        FXMLLoader loader = new FXMLLoader(MainApplication.class.getResource("teacherCourse/view-homework.fxml"));
        try{
            WindowsManager.getInstance().openNewWindow(
                    loader, 800, 600, "管理作业: " + c.getName(),
                    courseTableView.getScene().getWindow(), Modality.WINDOW_MODAL,
                    new WindowOpenAction() {
                        @Override
                        public void init(Object controller) {
                            WindowOpenAction.super.init(controller);
                            ViewHomeworkController controller1 = (ViewHomeworkController) controller;
                            controller1.init(c, MyTaughtController.this);
                        }
                    });
        }
        catch (IOException e){
            e.printStackTrace();
            MessageDialog.showDialog("打开作业管理页面失败");
        }
    }
}
