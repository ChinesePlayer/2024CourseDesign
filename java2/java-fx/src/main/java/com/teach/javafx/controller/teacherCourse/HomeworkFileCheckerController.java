package com.teach.javafx.controller.teacherCourse;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import org.fatmansoft.teach.util.CommonMethod;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class HomeworkFileCheckerController {
    @FXML
    public GridPane gridPane;

    public HomeworkAlignController controller;
    public List<String> filePaths = new ArrayList<>();

    @FXML
    public void initialize(){
        
    }

    public void init(HomeworkAlignController controller){
        this.controller = controller;
        filePaths = controller.getFilePaths();
        if (this.filePaths.isEmpty()){

        }
    }

    //没有文件时显示的内容
    public void setNoFilesView(){
        Label label = new Label("还没有上传文件，点击上方按钮来上传文件吧! ");
        label.getStyleClass().add("middle-label");
        gridPane.add(label, 0, 0);
    }

    //有文件时显示的内容
    public void setDataView(){
        //添加列头
        List<List> data = new ArrayList<>();
        List subData = new ArrayList<>();
        subData.add(new Label("文件名称"));
        subData.add(new Label("文件路径"));
        subData.add(new Label("文件大小"));
        subData.add(new Label("操作"));
        data.add(subData);
        CommonMethod.addItemToGridPane(data, gridPane, 0, 0);

        //添加实际数据
        List<List> fileData = new ArrayList<>();
        filePaths.forEach(new Consumer<String>() {
            @Override
            public void accept(String path) {
                List subData = new ArrayList<>();
                File f = new File(path);
                Label name = new Label(f.getName());
                Label filePath = new Label(path);
                Label fileSize = new Label();
                Button deleteButton = new Button("移除");

                if(f.exists()){
                    long fileByteSize = f.length();
                    fileSize.setText(fileByteSize+"");
                }
                else{
                    fileSize.setText("该文件不存在");
                }
                subData.add(name);
                subData.add(filePath);
                subData.add(fileSize);
                subData.add(deleteButton);
            }
        });
    }

    public void addColumnHead(){

    }

    public void onUploadButtonClick(ActionEvent event){
        FileChooser chooser = new FileChooser();
        List<File> files = chooser.showOpenMultipleDialog(null);

    }
}
