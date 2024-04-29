package com.teach.javafx.controller.studentScore;

import com.teach.javafx.AppStore;
import com.teach.javafx.controller.base.MessageDialog;
import com.teach.javafx.request.HttpRequestUtil;
import io.github.palexdev.materialfx.controls.MFXButton;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import org.fatmansoft.teach.models.Score;
import org.fatmansoft.teach.payload.request.DataRequest;
import org.fatmansoft.teach.payload.response.DataResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class StudentScoreController {
    @FXML
    public TableView<Score> scoreTableView;
    @FXML
    public TableColumn<Score, String> courseName;
    @FXML
    public TableColumn<Score, String> courseNum;
    @FXML
    public TableColumn<Score, String> credit;
    @FXML
    public TableColumn<Score, String> status;
    @FXML
    public TableColumn<Score, String> mark;
    @FXML
    public TableColumn<Score, String> rank;
    @FXML
    public TableColumn<Score, String> gpa;
    @FXML
    public MFXButton gpaCalc;

    //正在修读的全部课程
    private List<Score> scoreList = new ArrayList<>();
    //已及格的课程
    private List<Score> passedList = new ArrayList<>();
    //修读中的课程
    private List<Score> readingList = new ArrayList<>();
    //不及格的课程
    private List<Score> failedList = new ArrayList<>();

    private ObservableList<Score> observableList = FXCollections.observableArrayList();



    @FXML
    public void initialize(){
        //TODO:设置表格工厂
        courseName.setCellValueFactory(new StudentScoreValueFactory());
        courseNum.setCellValueFactory(new StudentScoreValueFactory());
        credit.setCellValueFactory(new StudentScoreValueFactory());
        status.setCellValueFactory(new StudentScoreValueFactory());
        mark.setCellValueFactory(new StudentScoreValueFactory());
        rank.setCellValueFactory(new StudentScoreValueFactory());
        gpa.setCellValueFactory(new StudentScoreValueFactory());

        //设置无课程可选时的占位组件
        Label placeholder = new Label("暂无修读课程 ¯\\_(ツ)_/¯");
        placeholder.setStyle("-fx-font-size: 30px");
        scoreTableView.setPlaceholder(placeholder);
        update();
    }

    //从后端获取该学生所选的所有课程
    public void getScoreList(){
        DataRequest req = new DataRequest();
        Integer studentId = AppStore.getJwt().getRoleId();
        req.add("studentId", studentId);
        DataResponse res = HttpRequestUtil.request("/api/score/getScoreList", req);
        if (res != null && res.getCode() == 0) {
            List<Map> rawData = (ArrayList<Map>)res.getData();
            for(Map m : rawData){
                Score s = new Score(m);
                scoreList.add(s);
                if(s.getStatus() == 0){
                    readingList.add(s);
                }
                else if(s.getStatus() == 1){
                    passedList.add(s);
                }
                else if(s.getStatus() == 2){
                    failedList.add(s);
                }
            }
        }
    }

    public void setDataView(){
        observableList.addAll(FXCollections.observableArrayList(scoreList));
        scoreTableView.setItems(observableList);
    }

    public void update(){
        getScoreList();
        setDataView();
    }

    //计算GPA并显示一个弹窗展示计算结果
    //计算公式：课程绩点加权平均（此处的权即为课程学分），此处的平均即除以学分总和
    public void showGpa(){
        double a1 = 0.0;
        double sumCredit = 0.0;
        for(Score s : passedList){
            a1 += s.getGpa() * s.getCredit();
            sumCredit += s.getCredit();
        }
        for(Score s : failedList){
            sumCredit += s.getCredit();
        }
        double res = a1/sumCredit;
        MessageDialog.showDialog("你的GPA为: " + String.format("%.1f", res));
    }
}
