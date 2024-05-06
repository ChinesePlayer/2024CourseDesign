package com.teach.javafx.controller.setting;

import com.teach.javafx.MainApplication;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class SystemSettingController {

    @FXML
    public VBox settingCategories;
    @FXML
    public BorderPane borderPane;

    //设置的目录：设置名称 ---- 设置的fxml文件路径
    private Map<String, String> contentPath = new HashMap<>();
    private Map<String, Scene> sceneMap = new HashMap<>();

    @FXML
    public void initialize(){
        contentPath.put("commonSetting", "setting/common-setting.fxml");
    }

    public void changeContent(){

    }
}
