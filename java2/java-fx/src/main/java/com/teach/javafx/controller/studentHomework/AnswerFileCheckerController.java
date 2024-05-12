package com.teach.javafx.controller.studentHomework;

import com.teach.javafx.controller.base.MessageDialog;
import com.teach.javafx.request.HttpRequestUtil;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import org.fatmansoft.teach.models.Answer;
import org.fatmansoft.teach.models.AnswerFile;
import org.fatmansoft.teach.models.HomeworkFile;
import org.fatmansoft.teach.payload.request.DataRequest;
import org.fatmansoft.teach.payload.response.DataResponse;
import org.fatmansoft.teach.util.CommonMethod;

import java.io.File;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class AnswerFileCheckerController {
    @FXML
    public GridPane gridPane;

    public List<AnswerFile> answerFileList = new ArrayList<>();
    public Answer answer;

    public void init(Answer a, List<AnswerFile> afs){
        this.answerFileList = afs;
        this.answer = a;
        update();
    }

    //没有文件时显示的内容
    public void setNoFileView(){
        gridPane.getChildren().clear();
        Label label = new Label( "还未添加文件，点击右上角按钮添加吧~");
        label.getStyleClass().add("middle-label");
        gridPane.add(label, 0, 0);
    }

    //有文件时的数据显示
    public void setDataView(){
        gridPane.getChildren().clear();
        //添加列头
        List<List> heads = new ArrayList<>();
        List subData = new ArrayList<>();
        subData.add(new Label("文件名称"));
        subData.add(new Label("文件大小"));
        subData.add(new Label("操作"));
        heads.add(subData);
        CommonMethod.addItemToGridPane(heads, gridPane, 0, 0);

        //添加实际数据
        List<List> data = new ArrayList<>();
        answerFileList.forEach(aFile -> {
            List subData1 = new ArrayList<>();
            Label l1 = new Label(aFile.getFileName());
            Label l2 = new Label(CommonMethod.formatFileSize(aFile.getFileSize()));
            Button deleteButton = new Button("移除");
            deleteButton.setOnAction(event -> onDeleteButton(aFile));

            subData1.add(l1);
            subData1.add(l2);subData1.add(deleteButton);

            data.add(subData1);
        });
        CommonMethod.addItemToGridPane(data,gridPane,1,0);
    }

    public void update(){
        if(answerFileList.isEmpty()){
            setNoFileView();
        }
        else{
            setDataView();
        }
    }

    public void onDeleteButton(AnswerFile af){
        //删除文件
        //判断是否为远程文件, 以ID为依据
        //若id不为空，则为远程文件
        if(af.getFileId() != null){
            //TODO: 删除远程文件
            DataRequest req = new DataRequest();
            req.add("fileId", af.getFileId());
            DataResponse res = HttpRequestUtil.request("/api/homework/deleteAnswerFile", req);
            assert res != null;
            if(res.getCode() == 0){
                MessageDialog.showDialog("删除成功");
                //从列表中移除文件
                answerFileList.remove(af);
                update();
            }
            else{
                MessageDialog.showDialog("删除失败: " + res.getMsg());
            }
        }
        //非远程文件, 直接从列表中删除
        else{
            answerFileList.remove(af);
            update();
        }
    }

    //上传文件回调
    public void onUploadFile(){
        FileChooser chooser = new FileChooser();
        chooser.setTitle("选择文件");
        File f = chooser.showOpenDialog(gridPane.getScene().getWindow());
        if(f.exists()){
            AnswerFile af = new AnswerFile();
            af.setFileName(f.getName());
            af.setFilePath(f.getPath());
            af.setFileSize(f.length());
            answerFileList.add(af);
            update();
        }
        else{
            MessageDialog.showDialog("所选文件不存在或已被迁移");
        }
    }

}
