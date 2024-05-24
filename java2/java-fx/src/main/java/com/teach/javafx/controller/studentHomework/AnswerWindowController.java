package com.teach.javafx.controller.studentHomework;

import com.teach.javafx.AppStore;
import com.teach.javafx.MainApplication;
import com.teach.javafx.controller.base.MessageDialog;
import com.teach.javafx.managers.WindowOpenAction;
import com.teach.javafx.managers.WindowsManager;
import com.teach.javafx.request.HttpRequestUtil;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.TextArea;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.fatmansoft.teach.models.AnswerFile;
import org.fatmansoft.teach.models.Homework;
import org.fatmansoft.teach.payload.request.DataRequest;
import org.fatmansoft.teach.payload.response.DataResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class AnswerWindowController {
    @FXML
    public TextArea contentArea;

    public Homework homework;

    public List<AnswerFile> answerFileList = new ArrayList<>();
    private HomeworkCheckerController homeworkCheckerController;
    @FXML
    public void initialize(){

    }
    public void init(Homework h, HomeworkCheckerController cont){
        this.homework = h;
        this.homeworkCheckerController = cont;
        setDataView();
        getAnswerFile();
    }

    public void setDataView(){
        //根据传进来的作业类来显示信息
        //若该作业已作答:
        if(homework.getAnswer() != null){
            contentArea.setText(homework.getAnswer().getContent());
        }
    }

    //从后端获取答案文件(如果已作答的话)
    public void getAnswerFile(){
        //未作答，不进行任何操作
        if(homework.getAnswer() == null){
            return;
        }
        //已作答，从后端获取已上传的文件
        //清空列表以刷新
        answerFileList.clear();
        DataRequest req = new DataRequest();
        req.add("answerId", homework.getAnswer().getAnswerId());
        DataResponse res = HttpRequestUtil.request("/api/homework/getStudentAnswerFileList", req);
        assert res != null;
        if(res.getCode() == 0){
            List<Map> rawData = (ArrayList<Map>) res.getData();
            for(Map m : rawData){
                AnswerFile af = new AnswerFile(m);
                answerFileList.add(af);
            }
        }
    }

    //打开文件查看窗口
    public void onCheckFile(){
        try{
            FXMLLoader loader = new FXMLLoader(MainApplication.class.getResource("studentHomework/answer-file-checker.fxml"));
            WindowsManager.getInstance().openNewWindow(
                    loader, 700, 600, "上传文件",
                    contentArea.getScene().getWindow(), Modality.WINDOW_MODAL,
                    new WindowOpenAction() {
                        @Override
                        public void init(Object controller) {
                            WindowOpenAction.super.init(controller);
                            AnswerFileCheckerController cont = (AnswerFileCheckerController) controller;
                            //注意此处homework.getAnswer()可能为null
                            cont.init(homework.getAnswer(), answerFileList);
                        }
                    }
            );
        }
        catch (IOException e){
            e.printStackTrace();
            MessageDialog.showDialog("打开文件查看窗口失败");
        }
    }

    //提交作业
    public void onSubmit(){
        //TODO: 后端检查是否仍在作答时间内
        String content = contentArea.getText();

        DataRequest req = new DataRequest();
        req.add("homeworkId", homework.getHomeworkId());
        req.add("answerId", homework.getAnswer() == null?null:homework.getAnswer().getAnswerId());
        req.add("content", content);
        req.add("studentId", AppStore.getJwt().getRoleId());

        //获取新增文件
        List<AnswerFile> newFile = new ArrayList<>();
        answerFileList.forEach(answerFile -> {
            if(answerFile.getFileId() == null){
                newFile.add(answerFile);
            }
        });
        DataResponse res = HttpRequestUtil.request("/api/homework/saveAnswer", req , AnswerFile.buildFilePaths(newFile));
        assert res != null;
        if(res.getCode() == 0){
            MessageDialog.showDialog("保存成功");
            ((Stage)contentArea.getScene().getWindow()).close();
        }
        else{
            MessageDialog.showDialog(res.getMsg());
        }
        homeworkCheckerController.hasSubmit();
    }
}
