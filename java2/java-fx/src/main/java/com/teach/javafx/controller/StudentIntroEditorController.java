package com.teach.javafx.controller;

import com.teach.javafx.AppStore;
import com.teach.javafx.controller.base.MessageDialog;
import com.teach.javafx.request.HttpRequestUtil;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.web.HTMLEditor;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.fatmansoft.teach.payload.request.DataRequest;
import org.fatmansoft.teach.payload.response.DataResponse;
import org.fatmansoft.teach.util.CommonMethod;

import java.io.File;
import java.io.IOException;

public class StudentIntroEditorController {
    @FXML
    public HTMLEditor introduceHtml;

    private String originHtml;

    private Stage stage;

    @FXML
    public void initialize(){
        getCurrentHTML();
    }

    public void setStage(Stage s){
        this.stage = s;
        stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent windowEvent) {
                if( originHtml != null && !originHtml.equals(introduceHtml.getHtmlText())){
                    int ret = MessageDialog.choiceDialog("未保存的内容会丢失，你确定吗?");
                    if(ret != MessageDialog.CHOICE_YES){
                        //阻止关闭事件的进一步传播
                        windowEvent.consume();
                    }
                }
            }
        });
    }

    //从后端请求已有的个人简历的HTML文本数据然后填入introduceHTML中
    public void getCurrentHTML(){
        DataRequest req = new DataRequest();
        req.add("studentId", AppStore.getJwt().getRoleId());
        DataResponse res = HttpRequestUtil.request("/api/student/getStudentIntroHTML", req);
        assert res != null;
        if(res.getCode() == 0){
            String html = (String)res.getData();
            System.out.println(html);

            originHtml = html;
            introduceHtml.setHtmlText(html);
        }
    }

    //保存用户的更改
    public void saveChange(){
        String html = introduceHtml.getHtmlText();
        //检查是否有更改
        if(html.equals(originHtml)){
            MessageDialog.showDialog("保存成功");
            return;
        }
        DataRequest req = new DataRequest();
        req.add("studentId", AppStore.getJwt().getRoleId());
        req.add("introduce", html);
        DataResponse res =HttpRequestUtil.request("/api/student/saveStudentIntroduce", req);
        assert res != null;
        if(res.getCode() == 0){
            MessageDialog.showDialog("保存成功! ");
            originHtml = html;
        }
        else{
            MessageDialog.showDialog(res.getMsg());
        }
    }

    //从后端请求pdf格式的文件
    public void downloadPdf(){
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle("请选择保存路径");
        File file = chooser.showDialog(introduceHtml.getScene().getWindow());

        //下载前先上传到后端
        DataRequest req = new DataRequest();
        req.add("studentId", AppStore.getJwt().getRoleId());
        req.add("introduce", introduceHtml.getHtmlText());
        DataResponse res =HttpRequestUtil.request("/api/student/saveStudentIntroduce", req);
        assert res != null;
        if (res.getCode() != 0){
            MessageDialog.showDialog("上传失败，无法保存pdf");
            return;
        }
        originHtml = introduceHtml.getHtmlText();

        req = new DataRequest();
        req.add("studentId", AppStore.getJwt().getRoleId());
        byte[] pdfBytes = HttpRequestUtil.requestByteData("/api/student/getStudentIntroducePdf", req);
        if(pdfBytes == null){
            MessageDialog.showDialog("下载PDF失败");
            return;
        }
        try{
            CommonMethod.saveFile(file.getPath(),"个人简历.pdf", pdfBytes);
        }
        catch (IOException e){
            e.printStackTrace();
            MessageDialog.showDialog("保存失败! ");
        }
    }

}
