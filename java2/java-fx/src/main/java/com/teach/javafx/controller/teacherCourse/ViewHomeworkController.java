package com.teach.javafx.controller.teacherCourse;

import com.teach.javafx.MainApplication;
import com.teach.javafx.controller.base.MessageDialog;
import com.teach.javafx.managers.WindowOpenAction;
import com.teach.javafx.managers.WindowsManager;
import com.teach.javafx.request.HttpRequestUtil;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.fatmansoft.teach.models.Course;
import org.fatmansoft.teach.models.Homework;
import org.fatmansoft.teach.payload.request.DataRequest;
import org.fatmansoft.teach.payload.response.DataResponse;
import org.fatmansoft.teach.util.CommonMethod;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ViewHomeworkController {
    @FXML
    public GridPane gridPane;

    private List<Homework> homeworkList = new ArrayList<>();

    private Course course;
    private MyTaughtController controller;

    public void init(Course c, MyTaughtController controller){
        this.controller = controller;
        this.course = c;
        update();
    }

    private void update(){
        getHomework();
        setViewData();
    }

    private void getHomework(){
        DataRequest req = new DataRequest();
        req.add("courseId", course.getCourseId());
        DataResponse res = HttpRequestUtil.request("/api/homework/getHomeworkList", req);
        assert res != null;
        if(res.getCode() == 0){
            List<Map> rawData = (ArrayList<Map>) res.getData();
            homeworkList.clear();
            for(Map m : rawData){
                Homework h = new Homework(m);
                //TODO: 为每行添加编辑按钮，让教师能够编辑作业内容
                homeworkList.add(h);
            }
        }
    }

    private void setViewData(){
        List<List> data = new ArrayList<>();
        clearDataView();
        clearColumnHead();
        if(homeworkList.isEmpty()){
            Label l1 = new Label("暂无作业");
            l1.getStyleClass().add("middle-label");
            gridPane.add(l1, 0, 0);
        }
        else{
            homeworkList.forEach(homework -> {
                List<Node> subData = new ArrayList<>();
                Label l1 = new Label(homework.getTitle());
                Label l2 = new Label(CommonMethod.getDateString(homework.getStart(), null));
                Label l3 = new Label(CommonMethod.getDateString(homework.getEnd(), null));
                Button editButton = new Button("编辑");
                editButton.setOnAction(event -> onEditButtonClick(homework));
                Button deleteButton = new Button("删除");
                deleteButton.setOnAction(event -> onDeleteButtonClick(homework));

                l1.getStyleClass().add("middle-label");
                l2.getStyleClass().add("middle-label");
                l3.getStyleClass().add("middle-label");

                HBox actionHBox = new HBox();
                actionHBox.getChildren().add(editButton);
                actionHBox.getChildren().add(deleteButton);
                actionHBox.setAlignment(Pos.CENTER);

                subData.add(l1);
                subData.add(l2);
                subData.add(l3);
                subData.add(actionHBox);
                data.add(subData);
            });
            //从第二行开始填充数据，第一行留给列头
            CommonMethod.addItemToGridPane(data,gridPane,1, 0);
            addColumnHead();
        }
    }

    //清理gridPane中的数据，不包括列头
    private void clearDataView(){
        int lastRow = gridPane.getRowCount() - 1;
        int lastColumn = gridPane.getColumnCount() - 1;
        CommonMethod.deleteRectFromGridPane(gridPane, 1, 0, lastRow, lastColumn);
    }

    //清理列头
    private void clearColumnHead(){
        int lastColumn = gridPane.getColumnCount() - 1;
        CommonMethod.deleteRectFromGridPane(gridPane, 0, 0, 0, lastColumn);
    }

    private void addColumnHead(){
        List<List> data = new ArrayList<>();
        List subData = new ArrayList<>();
        subData.add(new Label("作业标题"));
        subData.add(new Label("开始时间"));
        subData.add(new Label("截止时间"));
        subData.add(new Label("操作"));
        data.add(subData);
        CommonMethod.addItemToGridPane(data, gridPane, 0, 0);
    }

    //发布作业回调
    public void onAlignHomework(ActionEvent event){
        FXMLLoader loader = new FXMLLoader(MainApplication.class.getResource("teacherCourse/homework-align.fxml"));
        try{
            WindowsManager.getInstance().openNewWindow(
                    loader, 800, 600, "作业布置: " + course.getName(),
                    gridPane.getScene().getWindow(), Modality.WINDOW_MODAL,
                    controller -> {
                        HomeworkAlignController controller1 = (HomeworkAlignController) controller;
                        controller1.init(course, ViewHomeworkController.this);
                    }
            );
        }
        catch (IOException e){
            e.printStackTrace();
            MessageDialog.showDialog("打开作业布置页面失败! ");
        }
    }

    public void onEditButtonClick(Homework homework){
        //打开编辑页面
        try{
            FXMLLoader loader = new FXMLLoader(MainApplication.class.getResource("teacherCourse/homework-align.fxml"));
            WindowsManager.getInstance().openNewWindow(
                    loader, 800, 600, "编辑作业: " + homework.getTitle(),
                    gridPane.getScene().getWindow(), Modality.WINDOW_MODAL,
                    controller -> {
                        HomeworkAlignController controller1 = (HomeworkAlignController) controller;
                        controller1.init(course, ViewHomeworkController.this);
                        //填充数据
                        controller1.fillData(homework);
                    }
            );
        }
        catch (IOException e){
            e.printStackTrace();
            MessageDialog.showDialog("打开编辑页面失败!");
        }
    }

    //删除作业
    public void onDeleteButtonClick(Homework homework){
        int ret = MessageDialog.choiceDialog("你确定要删除 " + homework.getTitle() + " 吗");
        if(ret != MessageDialog.CHOICE_YES){
            return;
        }
        DataRequest req = new DataRequest();
        req.add("homeworkId", homework.getHomeworkId());
        DataResponse res = HttpRequestUtil.request("/api/homework/deleteHomework", req);
        assert res != null;
        if (res.getCode() == 0){
            MessageDialog.showDialog("删除成功! ");
            update();
        }
        else {
            MessageDialog.showDialog("删除失败! ");
        }
    }

    public void onClose(){
        update();
    }
}
