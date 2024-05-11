package com.teach.javafx.controller.teacherCourse;

import com.teach.javafx.controller.base.MessageDialog;
import com.teach.javafx.request.HttpRequestUtil;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import org.fatmansoft.teach.models.Homework;
import org.fatmansoft.teach.models.HomeworkFile;
import org.fatmansoft.teach.payload.request.DataRequest;
import org.fatmansoft.teach.payload.response.DataResponse;
import org.fatmansoft.teach.util.CommonMethod;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class HomeworkFileCheckerController {
    @FXML
    public GridPane gridPane;

    public HomeworkAlignController controller;
    public List<HomeworkFile> homeworkFiles = new ArrayList<>();
    public Homework homework;

    @FXML
    public void initialize(){

    }

    public void init(HomeworkAlignController controller, Homework homework){
        this.controller = controller;
        this.homework = homework;
        this.homeworkFiles = controller.getHomeworkFile();
        update();
    }

    public void update(){
        if(homeworkFiles.isEmpty()){
            setNoFilesView();
        }
        else {
            setDataView();
        }
    }

    //没有文件时显示的内容
    public void setNoFilesView(){
        gridPane.getChildren().clear();
        Label label = new Label("还没有上传文件，点击上方按钮来上传文件吧! ");
        label.getStyleClass().add("middle-label");
        gridPane.add(label, 0, 0);
    }

    //有文件时显示的内容
    public void setDataView(){
        gridPane.getChildren().clear();
        //添加列头
        List<List> data = new ArrayList<>();
        List subData = new ArrayList<>();
        subData.add(new Label("文件名称"));
        subData.add(new Label("文件大小"));
        subData.add(new Label("操作"));
        data.add(subData);
        CommonMethod.addItemToGridPane(data, gridPane, 0, 0);

        //添加实际数据
        List<List> fileData = new ArrayList<>();
        homeworkFiles.forEach(hFile -> {
            List subData1 = new ArrayList<>();
            Label name = new Label(hFile.getFileName());
            Label fileSize = new Label(hFile.getFileSize() == null ? "暂无" : CommonMethod.formatFileSize(hFile.getFileSize()));

            Button deleteButton = new Button("移除");
            deleteButton.setOnAction(event -> onDeleteButtonClick(hFile));

            subData1.add(name);
            subData1.add(fileSize);
            subData1.add(deleteButton);
            fileData.add(subData1);
        });
        CommonMethod.addItemToGridPane(fileData,gridPane,1,0);
    }

    public void onUploadButtonClick(ActionEvent event){
        FileChooser chooser = new FileChooser();
        chooser.setTitle("选择作业文件");
        File file = chooser.showOpenDialog(null);
        if(file.exists()){
            HomeworkFile hf = new HomeworkFile();
            hf.setFileName(file.getName());
            hf.setFilePath(file.getPath());
            hf.setFileSize(file.length());
            homeworkFiles.add(hf);
        }
        else{
            MessageDialog.showDialog("所选文件不存在或已被迁移");
        }
        update();
    }


    //删除指定文件
    //注意：区分新增文件和已有文件是靠fileId是否存在
    //有id的文件为已存在文件，无id的文件为新增但尚未上传到后端的文件
    public void onDeleteButtonClick(HomeworkFile hFile){
        Integer fileId = hFile.getFileId();
        if(fileId != null){
            DataRequest req = new DataRequest();
            req.add("fileId", hFile.getFileId());
            DataResponse res = HttpRequestUtil.request("/api/homework/deleteFile", req);
            assert res != null;
            if(res.getCode() == 0){
                MessageDialog.showDialog("文件删除成功! ");
            }
            else{
                MessageDialog.showDialog(res.getMsg());
            }
        }
        else{
            //仅仅从前端本地移除该文件
            homeworkFiles.remove(hFile);
        }
        update();
    }
}
