package com.teach.javafx.controller.teacherCourse;

import com.teach.javafx.AppStore;
import com.teach.javafx.MainApplication;
import com.teach.javafx.controller.courseSelection.CourseActionValueFactory;
import com.teach.javafx.controller.courseSelection.CourseTimeValueFactory;
import com.teach.javafx.controller.courseSelection.CourseValueFactory;
import com.teach.javafx.request.HttpRequestUtil;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.apache.batik.apps.rasterizer.Main;
import org.fatmansoft.teach.models.Course;
import org.fatmansoft.teach.models.CourseTime;
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
                //发布作业按钮
                Button alignHomework = new Button("发布作业");
                alignHomework.setOnAction(this::);

                List<Button> buttonList = new ArrayList<>();
                buttonList.add(checkStudent);
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
        //TODO: 弹出新页面，展示选择了该课程的学生
        FXMLLoader loader = new FXMLLoader(MainApplication.class.getResource("teacherCourse/student-viewer.fxml"));
        try{
            Scene scene = new Scene(loader.load(), 900, 600);
            StudentViewerController controller = loader.getController();
            controller.init(c);
            studentViewerStage = new Stage();
            studentViewerStage.setTitle("学生管理");
            studentViewerStage.setScene(scene);
            studentViewerStage.initModality(Modality.APPLICATION_MODAL);
            studentViewerStage.setOnCloseRequest(windowEvent -> studentViewerStage = null);
            studentViewerStage.show();
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

    //发布作业回调
    private void onAlignHomework(ActionEvent event){
        Course m = (Course) CommonMethod.getRowValue(event, 2, courseTableView);
        FXMLLoader loader = new FXMLLoader(MainApplication.class.getResource())
    }
}
