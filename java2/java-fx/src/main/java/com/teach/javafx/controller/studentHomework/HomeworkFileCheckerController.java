package com.teach.javafx.controller.studentHomework;

import com.teach.javafx.controller.base.MessageDialog;
import com.teach.javafx.request.HttpRequestUtil;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.stage.DirectoryChooser;
import org.fatmansoft.teach.models.Homework;
import org.fatmansoft.teach.models.HomeworkFile;
import org.fatmansoft.teach.payload.request.DataRequest;
import org.fatmansoft.teach.payload.response.DataResponse;
import org.fatmansoft.teach.util.CommonMethod;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class HomeworkFileCheckerController {
    @FXML
    public GridPane gridPane;

    public Homework homework;

    public List<HomeworkFile> homeworkFileList = new ArrayList<>();

    @FXML
    public void initialize(){

    }

    public void init(Homework h){
        this.homework = h;
        getFileList();
        update();
    }

    public void getFileList(){
        homeworkFileList.clear();
        DataRequest req = new DataRequest();
        req.add("homeworkId", homework.getHomeworkId());
        DataResponse res = HttpRequestUtil.request("/api/homework/getFileList", req);
        assert res != null;
        if(res.getCode() == 0){
            List<Map> rawData = (ArrayList<Map>) res.getData();
            for(Map m : rawData){
                HomeworkFile hf = new HomeworkFile(m);
                homeworkFileList.add(hf);
            }
        }
    }

    public void update(){
        if(homeworkFileList.isEmpty()){
            setNoFilesView();
        }
        else {
            setDataView();
        }
    }

    //没有文件时显示的内容
    public void setNoFilesView(){
        gridPane.getChildren().clear();
        Label label = new Label("老师还没有上传文件哦~~");
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
        homeworkFileList.forEach(hFile -> {
            List subData1 = new ArrayList<>();
            Label name = new Label(hFile.getFileName());
            Label fileSize = new Label(hFile.getFileSize() == null ? "暂无" : CommonMethod.formatFileSize(hFile.getFileSize()));

            //若文件为远程文件，则添加一个下载按钮，可用于下载文件
            //id不为空则为远程文件
            Button downloadButton=null;
            if(hFile.getFileId() != null){
                downloadButton = new Button("下载");
                downloadButton.setOnAction(event -> onDownloadButtonClick(hFile));
            }

            subData1.add(name);
            subData1.add(fileSize);
            if(downloadButton != null){
                subData1.add(downloadButton);
            }
            fileData.add(subData1);
        });
        CommonMethod.addItemToGridPane(fileData,gridPane,1,0);
    }

    public void onDownloadButtonClick(HomeworkFile file){
        //打开目录选择器
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle("选择保存路径");
        File directory = chooser.showDialog(null);
        Path p = Paths.get(directory.getPath(), CommonMethod.generateUniqueFileName(file.getFileName(), directory.getPath()));
        //下载指定文件
        DataRequest req = new DataRequest();
        req.add("fileId", file.getFileId());
        byte[] byteData = HttpRequestUtil.requestByteData("/api/homework/downloadFile", req);
        if(byteData != null){
            try{
                Files.write(p, byteData);
                MessageDialog.showDialog("保存成功! ");
            }
            catch (IOException e){
                e.printStackTrace();
                MessageDialog.showDialog("文件保存失败! ");
            }
        }
        else {
            MessageDialog.showDialog("获取文件失败! ");
        }
    }
}
