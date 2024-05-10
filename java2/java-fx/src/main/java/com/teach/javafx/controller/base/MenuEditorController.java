package com.teach.javafx.controller.base;

import com.teach.javafx.request.MyTreeNode;
import io.github.palexdev.materialfx.controls.MFXCheckbox;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.util.StringJoiner;

public class MenuEditorController {
    @FXML
    public TextField menuName;
    @FXML
    public MFXCheckbox adminC;
    @FXML
    public MFXCheckbox studentC;
    @FXML
    public MFXCheckbox teacherC;
    public MenuControllerV2 menuControllerV2;
    public Stage stage;

    @FXML
    public void initialize(){

    }

    //将已有数据填入
    public void setData(MyTreeNode node){
        String[] typeIds = node.getUserTypeIds().split(",");
        menuName.setText(node.getTitle());
        //根据传入的用户权限字符串来选中响应的复选框
        for(String s : typeIds){
            switch (s){
                case "1":{
                    adminC.setSelected(true);
                    break;
                }
                case "2":{
                    studentC.setSelected(true);
                    break;
                }
                case "3":{
                    teacherC.setSelected(true);
                    break;
                }
            }
        }
    }

    private String buildUserTypeIds(){
        String res = "";
        if(adminC.isSelected()){
            res += "1";
        }
        if(studentC.isSelected()){
            res += "2";
        }
        if(teacherC.isSelected()){
            res += "3";
        }
        StringJoiner joiner = new StringJoiner(",");
        for(char c: res.toCharArray()){
            joiner.add(String.valueOf(c));
        }
        return joiner.toString();
    }

    public void onSave(){
        if(menuControllerV2.onEditMenu(menuName.getText(), buildUserTypeIds())){
            stage.close();
            return;
        }
        MessageDialog.showDialog("保存失败");
    }
}
