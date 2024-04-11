package com.teach.javafx.controller.base;

import com.teach.javafx.request.MyTreeNode;
import io.github.palexdev.materialfx.controls.MFXCheckbox;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.fxml.FXML;

public class UpdateMenuDialog {
    @FXML
    private MFXTextField id;
    @FXML
    private MFXTextField pid;
    @FXML
    private MFXTextField title;
    @FXML
    private MFXTextField name;
    @FXML
    private MFXCheckbox checkAdmin;
    @FXML
    private MFXCheckbox checkStudent;
    @FXML
    private MFXCheckbox checkTeacher;

    private String userTypeIds;

    public void onSaveButtonClick(){
        //TODO: 实现保存按钮按下后的逻辑，目前暂时用控制台输出代替
        System.out.println("保存按钮被按下了!");
    }

    //参数：需要更新的数据
    public UpdateMenuDialog(MyTreeNode editNode){
        //根据调用者传入的数据初始化显示的数据
        id.setText(String.valueOf(editNode.getId()));
        pid.setText(String.valueOf(editNode.getPid()));
        title.setText(editNode.getTitle());
        name.setText(editNode.getValue());
        userTypeIds = editNode.getUserTypeIds();

        //将允许哪些用户访问的字符串分解，并将允许访问的类型的复选框设为选中状态
        //"1"表示允许管理员访问, "2"表示允许学生访问, "3"表示允许教师访问
        String[] userTypeInfo = userTypeIds.split(",");
        for(String i : userTypeInfo){
            switch (i) {
                case "1" -> checkAdmin.setSelected(true);
                case "2" -> checkStudent.setSelected(true);
                case "3" -> checkTeacher.setSelected(true);
            }
        }
    }

    //参数: 一个回调函数, 封装了调用者想通过该弹窗产生的数据执行的操作
    public void showUpdateMenuWindow(DialogCallback<MyTreeNode> callback){
        MyTreeNode newNode = new MyTreeNode();
        newNode.setId(Integer.parseInt(id.getText()));
        newNode.setPid(Integer.parseInt(pid.getText()));
        newNode.setTitle(title.getText());
        newNode.setValue(name.getText());
        callback.onDialogClose(newNode);
    }
}
