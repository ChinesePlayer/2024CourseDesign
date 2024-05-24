package com.teach.javafx.controller.studentHomework;

import com.teach.javafx.AppStore;
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
import org.fatmansoft.teach.models.Answer;
import org.fatmansoft.teach.models.Course;
import org.fatmansoft.teach.models.Homework;
import org.fatmansoft.teach.payload.request.DataRequest;
import org.fatmansoft.teach.payload.response.DataResponse;
import org.fatmansoft.teach.util.CommonMethod;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class HomeworkCheckerController {
    @FXML
    public GridPane gridPane;

    public Course course;
    public List<Homework> homeworkList = new ArrayList<>();

    @FXML
    public void initialize(){

    }

    public void init(Course c){
        this.course = c;
        getHomeworkList();
        update();
    }

    //获取对应课程的所有作业
    public void getHomeworkList(){
        homeworkList.clear();
        DataRequest req = new DataRequest();
        req.add("courseId", course.getCourseId());
        req.add("studentId", AppStore.getJwt().getRoleId());
        DataResponse res = HttpRequestUtil.request("/api/homework/getStudentHomeworkList", req);
        assert res != null;
        if(res.getCode() == 0){
            List<Map> rawData = (ArrayList<Map>) res.getData();
            for(Map m : rawData){
                Homework h = new Homework(m);
                if(m.get("answer") != null){
                    Map aMap = (Map) m.get("answer");
                    Answer a = new Answer(aMap);
                    h.setAnswer(a);
                }
                homeworkList.add(h);
            }
        }
    }

    //有作业时的数据显示
    public void setDataView(){
        gridPane.getChildren().clear();
        //添加列头
        List<List> heads = new ArrayList<>();
        List subData = new ArrayList<>();
        subData.add(new Label("作业标题"));
        subData.add(new Label("开始时间"));
        subData.add(new Label("结束时间"));
        subData.add(new Label("操作"));
        heads.add(subData);
        CommonMethod.addItemToGridPane(heads, gridPane, 0, 0);

        //添加实际数据
        List<List> data = new ArrayList<>();
        homeworkList.forEach(homework -> {
            List subData1 = new ArrayList<>();
            String homeworkTitle = homework.getTitle();
            String startTime = CommonMethod.getDateTimeString(homework.getStart(),null);
            String endTime = CommonMethod.getDateTimeString(homework.getEnd(), null);
            LocalDateTime dateTime = LocalDateTime.now();

            Label l1 = new Label(homeworkTitle);
            Label l2 = new Label(startTime);
            Label l3 = new Label(endTime);

            Button answerButton = new Button("作答");
            Button lookHomeworkFile = new Button("查看附件");

            l1.getStyleClass().add("middle-label");
            l2.getStyleClass().add("middle-label");
            l3.getStyleClass().add("middle-label");

            subData1.add(l1);
            subData1.add(l2);
            subData1.add(l3);

            if(!dateTime.isAfter(homework.getStart())){
                answerButton.setText("还未到作答时间");
                answerButton.setDisable(true);
            }
            else if(!dateTime.isBefore(homework.getEnd())){
                answerButton.setText("作答时间时间已过, 无法作答");
                answerButton.setDisable(true);
            }
            else{
                if(homework.getAnswer() != null){
                    answerButton.setText("修改答案");
                }
                answerButton.setDisable(false);
                answerButton.setOnAction(event -> onAnswerButton(homework));
            }
            lookHomeworkFile.setOnAction(event -> onLookHomeworkFile(homework));

            subData1.add(answerButton);
            subData1.add(lookHomeworkFile);

            data.add(subData1);
        });
        CommonMethod.addItemToGridPane(data,gridPane,1,0);
    }

    //没有作业时显示的内容
    public void setNoHomeworkView(){
        gridPane.getChildren().clear();
        Label label = new Label(course.getName() + " 暂时还没有作业喔 :)");
        label.getStyleClass().add("middle-label");
        gridPane.add(label, 0, 0);
    }

    //更新显示信息
    public void update(){
        if(homeworkList.isEmpty()){
            setNoHomeworkView();
        }
        else{
            setDataView();
        }
    }

    public void onAnswerButton(Homework homework){
        try{
            FXMLLoader loader = new FXMLLoader(MainApplication.class.getResource("studentHomework/answer-window.fxml"));
            WindowsManager.getInstance().openNewWindow(
                    loader, 800, 600, "作答: " + homework.getTitle(), gridPane.getScene().getWindow(),
                    Modality.WINDOW_MODAL,
                    new WindowOpenAction() {
                        @Override
                        public void init(Object controller) {
                            AnswerWindowController controller1 = (AnswerWindowController) controller;
                            controller1.init(homework, HomeworkCheckerController.this);
                        }
                    }
            );
        }
        catch (IOException e){
            e.printStackTrace();
            MessageDialog.showDialog("打开作答窗口失败");
        }
    }

    public void onLookHomeworkFile(Homework homework){
        try{
            FXMLLoader loader = new FXMLLoader(MainApplication.class.getResource("studentHomework/homework-file-checker.fxml"));
            WindowsManager.getInstance().openNewWindow(
                    loader, 800, 600, "查看附件",
                    gridPane.getScene().getWindow(), Modality.WINDOW_MODAL,
                    new WindowOpenAction() {
                        @Override
                        public void init(Object controller) {
                            HomeworkFileCheckerController cont = (HomeworkFileCheckerController) controller;
                            cont.init(homework);
                        }
                    }
            );
        }
        catch (IOException e){
            e.printStackTrace();
            MessageDialog.showDialog("打开文件查看窗口失败! ");
        }
    }

    public void hasSubmit(){
        getHomeworkList();
        update();
    }
}
