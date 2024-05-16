package com.teach.javafx.controller.setting;

import com.teach.javafx.MainApplication;
import com.teach.javafx.controller.base.MessageDialog;
import com.teach.javafx.managers.Settable;
import com.teach.javafx.managers.SettingManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SystemSettingController {

    @FXML
    public VBox settingCategories;
    @FXML
    public BorderPane borderPane;

    public Button common;

    //设置的目录：设置名称 ---- 设置的fxml文件路径
    private Map<String, String> contentPath = new HashMap<>();
    private Map<String, Scene> sceneMap = new HashMap<>();
    private List<Settable> settables = new ArrayList<>();

    @FXML
    public void initialize(){
        contentPath.put("commonSetting", "setting/common-setting.fxml");
        common = new Button("通用设置");

        common.setOnAction(this::onCommonButton);

        settingCategories.getChildren().add(common);

        //打开设置页面默认展示通用设置
        onCommonButton(null);
    }

    public void onCommonButton(ActionEvent event){
        Scene s = sceneMap.get("commonSetting");
        //如果通用设置页面尚未加载，则加载，并将其存入已加载Map中
        if(s == null){
            FXMLLoader loader = new FXMLLoader(MainApplication.class.getResource(contentPath.get("commonSetting")));
            try {
                s = new Scene(loader.load(), 800, 500);
                Settable sta = loader.getController();
                settables.add(sta);
                sceneMap.put("commonSetting", s);
                borderPane.setCenter(s.getRoot());
            }
            catch (IOException e){
                e.printStackTrace();
            }
        }
        else{
            borderPane.setCenter(s.getRoot());
        }
    }

    //应用设置
    public void onSave(){
        for(Settable sta : settables){
            if(sta.isModified()){
                SettingManager.getInstance().set(sta.getPairs());
                sta.applyChange();
            }
        }
        if(SettingManager.getInstance().save()){
            MessageDialog.showDialog("保存成功!");
        }
    }
}
