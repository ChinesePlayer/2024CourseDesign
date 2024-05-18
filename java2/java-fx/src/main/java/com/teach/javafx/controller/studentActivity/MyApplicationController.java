package com.teach.javafx.controller.studentActivity;

import com.teach.javafx.AppStore;
import com.teach.javafx.controller.base.MessageDialog;
import com.teach.javafx.factories.ActivityActionsValueFactory;
import com.teach.javafx.factories.ActivityValueFactory;
import com.teach.javafx.request.HttpRequestUtil;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;
import org.fatmansoft.teach.models.Activity;
import org.fatmansoft.teach.payload.request.DataRequest;
import org.fatmansoft.teach.payload.response.DataResponse;
import org.fatmansoft.teach.util.CommonMethod;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

public class MyApplicationController {
    @FXML
    public TableColumn<Activity, String> activityNum;
    @FXML
    public TableColumn<Activity, String> activityName;
    @FXML
    public TableColumn<Activity, String> status;
    @FXML
    public TableColumn<Activity, HBox> actions;
    @FXML
    public ComboBox<String> statusCombo;
    @FXML
    public TableView<Activity> tableView;

    private Map<String, Integer> statusMap = new HashMap<>();

    private List<Activity> activityList = new ArrayList<>();

    {
        statusMap.put("全部", null);
        statusMap.put("审批中",0);
        statusMap.put("已通过",1);
        statusMap.put("未通过",2);
    }

    @FXML
    public void initialize(){
        activityName.setCellValueFactory(new ActivityValueFactory());
        activityNum.setCellValueFactory(new ActivityValueFactory());
        status.setCellValueFactory(new ActivityValueFactory());
        actions.setCellValueFactory(new ActivityActionsValueFactory());

        statusCombo.setItems(FXCollections.observableArrayList("全部","审批中","已通过","未通过"));
        onQueryButtonClick();
    }

    public void onQueryButtonClick(){
        DataRequest req = new DataRequest();
        req.add("studentId", AppStore.getJwt().getRoleId());
        req.add("status", statusCombo.getValue() == null ? null : statusMap.get(statusCombo.getValue()));
        DataResponse res = HttpRequestUtil.request("/api/activity/getStudentAppliedActivityList",req);
        assert res != null;
        if(res.getCode() == 0){
            List<Map> rawData = (ArrayList<Map>) res.getData();
            activityList.clear();
            for(Map m : rawData){
                Activity a = new Activity(m);
                //取消申请按钮
                Button cancelApply = new Button("取消申请");
                cancelApply.setOnAction(this::onCancelApply);
                //重新申请按钮
                Button reapply = new Button("重新申请");
                reapply.setOnAction(this::onReapply);
                if(a.getStatus() == 0){
                    a.getActions().add(cancelApply);
                }
                if(a.getStatus() == 2){
                    a.getActions().add(reapply);
                }
                activityList.add(a);
            }
            setDataView();
        }
    }

    //取消活动申请
    public void onCancelApply(ActionEvent event){
        Activity a = (Activity) CommonMethod.getRowValue(event, 2, tableView);
        int ret = MessageDialog.choiceDialog("确定要取消申请 " + a.getActivityName() + " 吗? ");
        if(ret != MessageDialog.CHOICE_YES){
            return;
        }
        DataRequest req = new DataRequest();
        req.add("activityId", a.getActivityId());
        DataResponse res = HttpRequestUtil.request("/api/activity/cancelApply", req);
        assert res != null;
        if(res.getCode() == 0){
            MessageDialog.showDialog("撤销成功!");
            onQueryButtonClick();
        }
        else{
            MessageDialog.showDialog(res.getMsg());
        }
    }

    //重新申请，仅对被拒绝的申请有用
    public void onReapply(ActionEvent event){
        Activity a = (Activity) CommonMethod.getRowValue(event,2,tableView);
        int ret = MessageDialog.choiceDialog("确定要重新提交申请?");
        if(ret != MessageDialog.CHOICE_YES){
            return;
        }
        DataRequest req = new DataRequest();
        req.add("activityId", a.getActivityId());
        DataResponse res = HttpRequestUtil.request("/api/activity/reapply", req);
        assert res != null;
        if(res.getCode() == 0){
            MessageDialog.showDialog("请耐心等待审核! ");
            onQueryButtonClick();
        }
        else{
            MessageDialog.showDialog(res.getMsg());
        }
    }

    public void setDataView(){
        tableView.getItems().clear();
        tableView.setItems(FXCollections.observableArrayList(activityList));
    }
}
