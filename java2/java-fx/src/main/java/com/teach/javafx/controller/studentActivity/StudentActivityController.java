package com.teach.javafx.controller.studentActivity;

import com.teach.javafx.AppStore;
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
import org.apache.batik.apps.rasterizer.Main;
import org.fatmansoft.teach.models.Activity;
import org.fatmansoft.teach.payload.request.DataRequest;
import org.fatmansoft.teach.payload.response.DataResponse;
import org.fatmansoft.teach.util.CommonMethod;

import java.io.IOException;
import java.util.*;

public class StudentActivityController {
    @FXML
    public MFXTextField activityNameField;
    @FXML
    public MFXTextField directorNameField;
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
    public TableColumn<Activity, String> start;
    @FXML
    public TableColumn<Activity, String> end;
    @FXML
    public TableColumn<Activity, HBox> actions;
    public TableColumn<Activity, String> maxNumber;

    private List<Activity> activityList = new ArrayList<>();


    @FXML
    public void initialize(){

        actions.setCellValueFactory(new ActivityActionsValueFactory());
        start.setCellValueFactory(new ActivityValueFactory());
        end.setCellValueFactory(new ActivityValueFactory());
        activityNum.setCellValueFactory(new ActivityValueFactory());
        activityName.setCellValueFactory(new ActivityValueFactory());
        directorName.setCellValueFactory(new ActivityValueFactory());
        number.setCellValueFactory(new ActivityValueFactory());
        maxNumber.setCellValueFactory(new ActivityValueFactory());

        onQueryButtonClick();

    }

    //根据当前设定的查询条件查询活动信息
    public void onQueryButtonClick() {
        String activityName = activityNameField.getText();
        String studentName = directorNameField.getText();
        DataRequest req = new DataRequest();
        req.add("activityName", activityName);
        req.add("directorName", studentName);
        req.add("studentId", AppStore.getJwt().getRoleId());

        DataResponse res = HttpRequestUtil.request("/api/activity/getStudentActivityList",req);
        assert res != null;
        if(res.getCode() == 0){
            List<Map> rawData = (ArrayList<Map>)res.getData();
            activityList.clear();
            for(Map m : rawData){
                Activity a = new Activity(m);
                Button signupButton = new Button("报名");
                signupButton.setOnAction(this::onSignupActivity);
                //不允许报名自己是负责人的活动
                if(Objects.equals(a.getDirectorId(), AppStore.getJwt().getId())){
                    signupButton.setDisable(true);
                    signupButton.setText("负责人不能报名自己的活动");
                }
                //不允许报名已报名的活动
                if(a.isSignup()){
                    signupButton.setText("已报名");
                    signupButton.setDisable(true);
                }
                a.getActions().add(signupButton);
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

    //发起新活动申请
    public void onApplyNewActivity(){
        try{
            FXMLLoader loader = new FXMLLoader(MainApplication.class.getResource("studentActivity/student-activity-editor.fxml"));
            WindowsManager.getInstance().openNewWindow(
                    loader, 400, 600, "申请开展新活动",
                    tableView.getScene().getWindow(), Modality.WINDOW_MODAL,
                    new WindowOpenAction() {
                        @Override
                        public void init(Object controller) {
                            WindowOpenAction.super.init(controller);
                            StudentActivityEditorController econt = (StudentActivityEditorController) controller;
                            econt.init(StudentActivityController.this);
                        }
                    }
            );
        }
        catch (IOException e){
            e.printStackTrace();
            MessageDialog.showDialog("打开申请页面失败!");
        }
    }

    //报名活动
    public void onSignupActivity(ActionEvent event){
        Activity a = (Activity) CommonMethod.getRowValue(event, 2, tableView);
        int ret = MessageDialog.choiceDialog("确定报名该活动?");
        if(ret != MessageDialog.CHOICE_YES){
            return;
        }
        DataRequest req = new DataRequest();
        req.add("studentId",AppStore.getJwt().getRoleId());
        req.add("activityId", a.getActivityId());
        DataResponse res = HttpRequestUtil.request("/api/activity/signup",req);
        assert res != null;
        if(res.getCode() == 0){
            MessageDialog.showDialog("报名成功!");
            onQueryButtonClick();
        }
        else{
            MessageDialog.showDialog(res.getMsg());
        }
    }

    //查看我的申请
    public void onViewMyApplication(){
        try{
            FXMLLoader loader = new FXMLLoader(MainApplication.class.getResource("studentActivity/my-application.fxml"));
            WindowsManager.getInstance().openNewWindow(
                    loader,700,400,"我的申请",
                    tableView.getScene().getWindow(),Modality.WINDOW_MODAL,
                    null
            );
        }
        catch (IOException e){
            e.printStackTrace();
            MessageDialog.showDialog("无法查看申请列表! ");
        }
    }

    //查看已报名的活动
    public void onViewSignup(){
        try{
            FXMLLoader loader = new FXMLLoader(MainApplication.class.getResource("studentActivity/my-signup.fxml"));
            WindowsManager.getInstance().openNewWindow(
                    loader, 700, 500, "已报名的活动",
                    tableView.getScene().getWindow(), Modality.WINDOW_MODAL,
                    new WindowOpenAction() {
                        @Override
                        public void init(Object controller) {
                            WindowOpenAction.super.init(controller);
                            MySignupController msc = (MySignupController) controller;
                            msc.init(StudentActivityController.this);
                        }
                    }
            );
        }
        catch (IOException e){
            e.printStackTrace();
            MessageDialog.showDialog("无法查看已报名的活动!");
        }
    }

    public void hasSaved(){
        onQueryButtonClick();
    }

}
