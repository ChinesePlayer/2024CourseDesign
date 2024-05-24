package com.teach.javafx.controller.studentActivity;
import com.teach.javafx.MainApplication;
import com.teach.javafx.controller.base.MessageDialog;
import com.teach.javafx.factories.ActivityActionsValueFactory;
import com.teach.javafx.factories.ActivityValueFactory;
import com.teach.javafx.managers.WindowOpenAction;
import com.teach.javafx.managers.WindowsManager;
import com.teach.javafx.request.HttpRequestUtil;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import org.fatmansoft.teach.models.Activity;
import org.fatmansoft.teach.payload.request.DataRequest;
import org.fatmansoft.teach.payload.response.DataResponse;
import org.fatmansoft.teach.util.CommonMethod;

import javax.swing.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ActivityController {
    @FXML
    public MFXTextField activityNameField;
    @FXML
    public MFXTextField directorNameField;
    @FXML
    public ComboBox<String> statusCombo;
    @FXML
    public TableView<Activity> tableView;
    @FXML
    public TableColumn<Activity, String> activityNum;
    @FXML
    public TableColumn<Activity, String> activityName;
    @FXML
    public TableColumn<Activity, String> directorName;
    @FXML
    public TableColumn<Activity, String> number;
    @FXML
    public TableColumn<Activity, String> status;
    @FXML
    public TableColumn<Activity, String> start;
    @FXML
    public TableColumn<Activity, String> end;
    @FXML
    public TableColumn<Activity, HBox> actions;
    public TableColumn<Activity, String> maxNumber;

    private List<Activity> activityList = new ArrayList<>();


    private Map<String, Integer> statusMap  = new HashMap<>();

    {
        statusMap.put("全部", null);
        statusMap.put("审批中",0);
        statusMap.put("已通过",1);
        statusMap.put("未通过",2);
    }

    @FXML
    public void initialize(){
        statusCombo.setItems(FXCollections.observableArrayList("全部", "审批中","已通过", "未通过"));

        actions.setCellValueFactory(new ActivityActionsValueFactory());
        start.setCellValueFactory(new ActivityValueFactory());
        end.setCellValueFactory(new ActivityValueFactory());
        activityNum.setCellValueFactory(new ActivityValueFactory());
        activityName.setCellValueFactory(new ActivityValueFactory());
        directorName.setCellValueFactory(new ActivityValueFactory());
        number.setCellValueFactory(new ActivityValueFactory());
        status.setCellValueFactory(new ActivityValueFactory());
        maxNumber.setCellValueFactory(new ActivityValueFactory());

        onQueryButtonClick();

    }

    //从后端查询活动信息并刷新数据
    public void onQueryButtonClick(){
        String activityName = activityNameField.getText();
        String studentName = directorNameField.getText();
        Integer status = statusMap.get(statusCombo.getValue());
        DataRequest req = new DataRequest();
        req.add("activityName", activityName);
        req.add("directorName", studentName);
        req.add("status", status);

        DataResponse res = HttpRequestUtil.request("/api/activity/getActivityList",req);
        assert res != null;
        if(res.getCode() == 0){
            List<Map> rawData = (ArrayList<Map>)res.getData();
            activityList.clear();
            for(Map m : rawData){
                Activity a = new Activity(m);
                Button editButton = new Button("编辑");
                editButton.setOnAction(this::onEditButtonClick);
                Button deleteButton = new Button("删除");
                deleteButton.setOnAction(this::onDeleteButtonClick);
                if(a.getStatus() != 1){
                    Button passButton = new Button("通过");
                    passButton.setOnAction(this::onPass);
                    a.getActions().add(passButton);
                }
                if(a.getStatus() != 2){
                    Button refuseButton = new Button("拒绝");
                    refuseButton.setOnAction(this::onRefuse);
                    a.getActions().add(refuseButton);
                }

                a.getActions().add(editButton);
                a.getActions().add(deleteButton);
                activityList.add(a);
            }
            setDataView();
        }
        else{
            MessageDialog.showDialog("无法查询活动信息");
        }
    }

    //刷新数据
    public void setDataView(){
        tableView.getItems().clear();
        tableView.setItems(FXCollections.observableArrayList(activityList));
    }

    public void onDeleteButtonClick(ActionEvent event){
        Activity a = (Activity) CommonMethod.getRowValue(event, 2, tableView);
        int ret = MessageDialog.choiceDialog("你确定要删除 " + a.getActivityName() + " 吗?");
        if(ret != MessageDialog.CHOICE_YES){
            return;
        }
        DataRequest req = new DataRequest();
        req.add("activityId", a.getActivityId());
        DataResponse res = HttpRequestUtil.request("/api/activity/deleteActivity", req);
        assert res != null;
        if(res.getCode() == 0){
            MessageDialog.showDialog("删除成功");
            onQueryButtonClick();
        }
        else{
            MessageDialog.showDialog(res.getMsg());
        }
    }

    //审批通过按钮按下时进行的操作
    public void onPass(ActionEvent event){
        Activity a = (Activity) CommonMethod.getRowValue(event,2,tableView);
        DataRequest req = new DataRequest();
        req.add("activityId", a.getActivityId());
        DataResponse res = HttpRequestUtil.request("/api/activity/passActivity",req);
        assert res != null;
        if(res.getCode() == 0){
            MessageDialog.showDialog("审批成功!");
            onQueryButtonClick();
        }
        else{
            MessageDialog.showDialog(res.getMsg());
        }
    }

    //拒绝按钮按下时进行的操作
    public void onRefuse(ActionEvent event){
        Activity a = (Activity) CommonMethod.getRowValue(event,2,tableView);
        DataRequest req = new DataRequest();
        req.add("activityId", a.getActivityId());
        DataResponse res = HttpRequestUtil.request("/api/activity/refuseActivity",req);
        assert res != null;
        if(res.getCode() == 0){
            MessageDialog.showDialog("审批成功!");
            onQueryButtonClick();
        }
        else{
            MessageDialog.showDialog(res.getMsg());
        }
    }

    public void onEditButtonClick(ActionEvent event){
        Activity a = (Activity) CommonMethod.getRowValue(event, 2, tableView);
        try{
            FXMLLoader loader = new FXMLLoader(MainApplication.class.getResource("studentActivity/activity-editor.fxml"));
            WindowsManager.getInstance().openNewWindow(
                    loader, 500, 640, "编辑活动",
                    tableView.getScene().getWindow(), Modality.WINDOW_MODAL,
                    new WindowOpenAction() {
                        @Override
                        public void init(Object controller) {
                            WindowOpenAction.super.init(controller);
                            ActivityEditorController ae = (ActivityEditorController) controller;
                            ae.init(a,ActivityController.this);
                        }
                    }
            );
        }
        catch (IOException e){
            e.printStackTrace();
            MessageDialog.showDialog("打开编辑窗口失败! ");
        }
    }

    //新建按钮按下时的操作
    public void onNewActivity(){
        try{
            FXMLLoader loader = new FXMLLoader(MainApplication.class.getResource("studentActivity/activity-editor.fxml"));
            WindowsManager.getInstance().openNewWindow(
                    loader, 500, 640, "新建活动",
                    tableView.getScene().getWindow(), Modality.WINDOW_MODAL,
                    new WindowOpenAction() {
                        @Override
                        public void init(Object controller) {
                            WindowOpenAction.super.init(controller);
                            ActivityEditorController ae = (ActivityEditorController) controller;
                            ae.init(null,ActivityController.this);
                        }
                    }
            );
        }
        catch (IOException e){
            e.printStackTrace();
            MessageDialog.showDialog("打开新建窗口失败! ");
        }
    }

    //用于外界获取审批状态映射Map
    public Map<String, Integer> getStatusMapper(){
        return statusMap;
    }

    public void hasSaved(){
        onQueryButtonClick();
    }

}



