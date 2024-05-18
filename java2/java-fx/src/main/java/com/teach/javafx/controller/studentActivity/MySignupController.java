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

public class MySignupController {
    @FXML
    public TableColumn<Activity, String> activityNum;
    @FXML
    public TableColumn<Activity, String> activityName;
    @FXML
    public TableColumn<Activity, String> start;
    @FXML
    public TableColumn<Activity, String> end;
    @FXML
    public TableColumn<Activity, HBox> actions;
    @FXML
    public TableView<Activity> tableView;
    @FXML
    public ComboBox<String> statusCombo;//注意该状态不是审核状态，是活动的进行状态，分为0: 未开始  1: 进行中  2: 已结束

    private Map<String, Integer> statusMap = new HashMap<>();

    private List<Activity> activityList = new ArrayList<>();

    private StudentActivityController opener;

    {
        statusMap.put("全部", null);
        statusMap.put("未开始",0);
        statusMap.put("进行中",1);
        statusMap.put("已结束",2);
    }

    @FXML
    public void initialize(){
        actions.setCellValueFactory(new ActivityActionsValueFactory());
        start.setCellValueFactory(new ActivityValueFactory());
        end.setCellValueFactory(new ActivityValueFactory());
        activityNum.setCellValueFactory(new ActivityValueFactory());
        activityName.setCellValueFactory(new ActivityValueFactory());

        statusCombo.setItems(FXCollections.observableArrayList("全部","未开始","进行中","已结束"));
        onQueryButtonClick();
    }

    public void init(StudentActivityController o){
        this.opener = o;
    }

    public void onQueryButtonClick(){
        Integer progressStatus = statusCombo.getValue() == null ? null : statusMap.get(statusCombo.getValue());
        DataRequest req = new DataRequest();
        req.add("studentId", AppStore.getJwt().getRoleId());
        req.add("progressStatus", progressStatus);
        DataResponse res = HttpRequestUtil.request("/api/activity/getMySignup", req);
        assert res != null;
        if(res.getCode() == 0){
            List<Map> rawData = (ArrayList<Map>)res.getData();
            activityList.clear();
            for(Map m : rawData){
                Activity a = new Activity(m);
                Button cancelSignup = new Button("取消报名");
                if(a.getProgressStatus() == 0){
                    cancelSignup.setOnAction(this::onCancelSignup);
                }
                else if(a.getProgressStatus() == 1){
                    cancelSignup.setDisable(true);
                    cancelSignup.setText("已开始, 无法取消");
                }
                else if(a.getProgressStatus() == 2){
                    cancelSignup.setDisable(true);
                    cancelSignup.setText("已结束");
                }
                a.getActions().add(cancelSignup);
                activityList.add(a);
            }
            setDataView();
        }
    }

    //取消报名
    public void onCancelSignup(ActionEvent event){
        Activity a = (Activity) CommonMethod.getRowValue(event, 2, tableView);
        int ret = MessageDialog.choiceDialog("你确定要取消 " + a.getActivityName() + " 的报名吗?");
        if(ret != MessageDialog.CHOICE_YES){
            return;
        }
        DataRequest req = new DataRequest();
        req.add("studentId", AppStore.getJwt().getRoleId());
        req.add("activityId", a.getActivityId());
        DataResponse res = HttpRequestUtil.request("/api/activity/cancelSignup",req);
        assert res != null;
        if(res.getCode() == 0){
            MessageDialog.showDialog("取消成功!");
            //调用学生活动查看主页面的更新方法来更新显示数据
            opener.hasSaved();
            //更新自身的页面信息
            onQueryButtonClick();
        }
        else {
            MessageDialog.showDialog(res.getMsg());
        }
    }

    public void setDataView(){
        tableView.getItems().clear();
        tableView.setItems(FXCollections.observableArrayList(activityList));
    }
}
