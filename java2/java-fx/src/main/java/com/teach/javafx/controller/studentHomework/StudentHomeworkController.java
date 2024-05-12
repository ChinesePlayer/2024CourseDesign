package com.teach.javafx.controller.studentHomework;

import com.teach.javafx.MainApplication;
import com.teach.javafx.controller.base.MessageDialog;
import com.teach.javafx.managers.WindowOpenAction;
import com.teach.javafx.managers.WindowsManager;
import com.teach.javafx.request.HttpRequestUtil;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import org.fatmansoft.teach.models.Course;
import org.fatmansoft.teach.payload.request.DataRequest;
import org.fatmansoft.teach.payload.response.DataResponse;
import org.fatmansoft.teach.util.CommonMethod;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class StudentHomeworkController {
    @FXML
    public GridPane gridPane;

    public List<Course> courseList = new ArrayList<>();

    @FXML
    public void initialize(){
        getCourseList();
        update();
    }

    public void getCourseList(){
        courseList.clear();
        DataRequest req = new DataRequest();
        //获取学生已选课程
        DataResponse res = HttpRequestUtil.request("/api/course/getStudentCourseList", req);
        assert res != null;
        if(res.getCode() == 0){
            List<Map> rawData = (ArrayList<Map>)res.getData();
            System.out.println(rawData.size());
            for(Map m : rawData){
                Course c = new Course(m);
                courseList.add(c);
            }
        }
    }

    //没有课程时显示的内容
    public void setNoCourseView(){
        gridPane.getChildren().clear();
        Label label = new Label("你还没有课程喔:)");
        label.getStyleClass().add("middle-label");
        gridPane.add(label, 0, 0);
    }

    //有课程时的数据显示
    public void setDataView(){
        gridPane.getChildren().clear();
        //添加列头
        List<List> heads = new ArrayList<>();
        List subData = new ArrayList<>();
        subData.add(new Label("课序号"));
        subData.add(new Label("课程名称"));
        subData.add(new Label("操作"));
        heads.add(subData);
        CommonMethod.addItemToGridPane(heads, gridPane, 0, 0);

        //添加实际数据
        List<List> data = new ArrayList<>();
        courseList.forEach(course -> {
            List subData1 = new ArrayList<>();
            String courseName = course.getName();
            String courseNum = course.getNum();

            Label l1 = new Label(courseNum);
            Label l2 = new Label(courseName);
            Button checkHomework = new Button("查看作业");
            checkHomework.setOnAction(event -> onCheckHomework(course));

            l1.getStyleClass().add("middle-label");
            l2.getStyleClass().add("middle-label");

            subData1.add(l1);
            subData1.add(l2);
            subData1.add(checkHomework);
            data.add(subData1);
        });
        CommonMethod.addItemToGridPane(data,gridPane,1,0);
    }

    public void update(){
        if(courseList.isEmpty()){
            setNoCourseView();
        }
        else{
            setDataView();
        }
    }

    public void onCheckHomework(Course c){
        try{
            FXMLLoader loader = new FXMLLoader(MainApplication.class.getResource("studentHomework/homework-checker.fxml"));
            WindowsManager.getInstance().openNewWindow(
                    loader, 800, 600, "查看作业: " + c.getName(),
                    gridPane.getScene().getWindow(), Modality.WINDOW_MODAL,
                    new WindowOpenAction() {
                        @Override
                        public void init(Object controller) {
                            HomeworkCheckerController cont = (HomeworkCheckerController) controller;
                            cont.init(c);
                        }
                    }
            );
        }
        catch (IOException e){
            e.printStackTrace();
            MessageDialog.showDialog("无法打开作业查看窗口");
        }
    }
}
