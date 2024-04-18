package com.teach.javafx.controller.base;


import com.teach.javafx.request.HttpRequestUtil;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import org.fatmansoft.teach.payload.request.DataRequest;
import org.fatmansoft.teach.payload.response.DataResponse;

public class ResponseTestController {
    @FXML
    public MFXButton launRequest;
    @FXML
    public MFXTextField urlField;
    @FXML
    public Label resField;

    @FXML
    public void initialize(){

    }

    public void onRequest(){
        String url = urlField.getText();
        if(url == null || url.isEmpty()){
            resField.setText("错误: 未输入URL");
            return;
        }
        DataRequest req = new DataRequest();
        DataResponse res = HttpRequestUtil.request(url, req);
        String result = "";
        result += (res.toString() + "\n");
        result += (res.getData() + "\n");
        result += (res.getMsg() + "\n");
        result += (res.getCode() + "\n");
        resField.setText(result);
    }
}
