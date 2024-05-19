package com.teach.javafx.controller.application;

import com.teach.javafx.AppStore;
import com.teach.javafx.MainApplication;
import com.teach.javafx.controller.base.MessageDialog;
import com.teach.javafx.factories.ApplicationActionValueFactory;
import com.teach.javafx.factories.ApplicationValueFactory;
import com.teach.javafx.managers.WindowOpenAction;
import com.teach.javafx.managers.WindowsManager;
import com.teach.javafx.request.HttpRequestUtil;
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
import org.fatmansoft.teach.models.Application;
import org.fatmansoft.teach.payload.request.DataRequest;
import org.fatmansoft.teach.payload.response.DataResponse;
import org.fatmansoft.teach.util.CommonMethod;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class StudentApplicationController {
    @FXML
    public TableColumn<Application, String> applicationType;
    @FXML
    public TableColumn<Application, String> leaveDate;
    @FXML
    public TableColumn<Application, String> returnDate;
    @FXML
    public TableColumn<Application,String> status;
    @FXML
    public TableColumn<Application, HBox> actions;
    @FXML
    public TableView<Application> tableView;
    public ComboBox<String> statusCombo;

    private List<Application> applicationList = new ArrayList<>();

    @FXML
    public void initialize(){
        actions.setCellValueFactory(new ApplicationActionValueFactory());
        applicationType.setCellValueFactory(new ApplicationValueFactory());
        leaveDate.setCellValueFactory(new ApplicationValueFactory());
        returnDate.setCellValueFactory(new ApplicationValueFactory());
        status.setCellValueFactory(new ApplicationValueFactory());

        statusCombo.setItems(FXCollections.observableArrayList("全部", "审批中", "已通过","未通过","已销假"));

        onQueryButtonClick();
    }

    public void onQueryButtonClick(){
        Integer status = statusCombo.getSelectionModel().getSelectedItem() == null ? null : Application.getStatusCode(statusCombo.getSelectionModel().getSelectedItem());
        DataRequest req = new DataRequest();
        req.add("studentId", AppStore.getJwt().getRoleId());
        req.add("status",status);
        DataResponse res = HttpRequestUtil.request("/api/application/getApplicationList",req);
        assert res != null;
        if(res.getCode() == 0){
            List<Map> rawData = (ArrayList<Map>)res.getData();
            applicationList.clear();
            for(Map m : rawData){
                Application a = new Application(m);
                Button checkDetail = new Button("查看详情");
                Button editButton = new Button("编辑");
                if(a.getStatus() != 0){
                    editButton.setText("无法编辑");
                    editButton.setDisable(true);
                }
                else{
                    editButton.setOnAction(this::onEditApplication);
                }
                if(a.getStatus() == 0){
                    Button cancelButton = new Button("取消请假");
                    cancelButton.setOnAction(this::onCancelApplication);
                    a.getActions().add(cancelButton);
                }
                if(a.getStatus() == 1){
                    Button destroyButton = new Button("销假");
                    destroyButton.setOnAction(this::onDestroyButton);
                    a.getActions().add(destroyButton);
                }
                checkDetail.setOnAction(this::onCheckDetail);
                a.getActions().add(checkDetail);
                a.getActions().add(editButton);
                applicationList.add(a);
            }
            setDataView();
        }
    }

    public void onEditApplication(ActionEvent event){
        Application a = (Application) CommonMethod.getRowValue(event,2,tableView);
        try{
            FXMLLoader loader = new FXMLLoader(MainApplication.class.getResource("application/application-editor.fxml"));
            WindowsManager.getInstance().openNewWindow(
                    loader, 500, 600, "编辑假条",
                    tableView.getScene().getWindow(), Modality.WINDOW_MODAL,
                    new WindowOpenAction() {
                        @Override
                        public void init(Object controller) {
                            WindowOpenAction.super.init(controller);
                            ApplicationEditorController aeCont = (ApplicationEditorController) controller;
                            aeCont.init(a,StudentApplicationController.this);
                        }
                    }
            );
        }
        catch (IOException e){
            e.printStackTrace();
            MessageDialog.showDialog("打开编辑页面失败");
        }
    }

    public void onCheckDetail(ActionEvent event){
        Application a = (Application) CommonMethod.getRowValue(event,2,tableView);
        try{
            FXMLLoader loader = new FXMLLoader(MainApplication.class.getResource("application/application-detail.fxml"));
            WindowsManager.getInstance().openNewWindow(
                    loader, 700, 400, "假条详情",
                    tableView.getScene().getWindow(), Modality.WINDOW_MODAL,
                    new WindowOpenAction() {
                        @Override
                        public void init(Object controller) {
                            WindowOpenAction.super.init(controller);
                            ApplicationDetailController saCont = (ApplicationDetailController) controller;
                            saCont.init(a);
                        }
                    }
            );
        }
        catch (IOException e){
            e.printStackTrace();
            MessageDialog.showDialog("无法打开详情页面");
        }
    }

    //取消申请
    public void onCancelApplication(ActionEvent event){
        Application a = (Application) CommonMethod.getRowValue(event,2,tableView);
        int ret = MessageDialog.choiceDialog("确定要取消请假吗");
        if(ret != MessageDialog.CHOICE_YES){
            return;
        }
        DataRequest req = new DataRequest();
        req.add("applicationId", a.getApplicationId());
        DataResponse res = HttpRequestUtil.request("/api/application/deleteApplication",req);
        assert res != null;
        if(res.getCode() == 0){
            MessageDialog.showDialog("取消成功!");
            onQueryButtonClick();
        }
        else{
            MessageDialog.showDialog(res.getMsg());
        }
    }

    //请假
    public void onNewApplication(){
        try{
            FXMLLoader loader = new FXMLLoader(MainApplication.class.getResource("application/application-editor.fxml"));
            WindowsManager.getInstance().openNewWindow(
                loader, 700, 400, "请假",
                tableView.getScene().getWindow(), Modality.WINDOW_MODAL,
                new WindowOpenAction() {
                    @Override
                    public void init(Object controller) {
                        WindowOpenAction.super.init(controller);
                        ApplicationEditorController aeCont = (ApplicationEditorController) controller;
                        aeCont.init(null,StudentApplicationController.this);
                    }
                }
            );
        }
        catch (IOException e){
            e.printStackTrace();
            MessageDialog.showDialog("打开请假窗口失败");
        }
    }

    public void onDestroyButton(ActionEvent event){
        Application a = (Application) CommonMethod.getRowValue(event, 2, tableView);
        int ret = MessageDialog.choiceDialog("确定要销假吗, 请确保您已返校");
        if(ret != MessageDialog.CHOICE_YES){
            return;
        }
        //检测时间是否能销假
        LocalDate now = LocalDate.now();
        if(now.isBefore(a.getLeaveDate())){
            MessageDialog.showDialog("还未到离校日期, 无法销假!");
            return;
        }
        DataRequest req = new DataRequest();
        req.add("applicationId",a.getApplicationId());
        DataResponse res = HttpRequestUtil.request("/api/application/destroyApplication",req);
        assert res != null;
        if(res.getCode() == 0){
            MessageDialog.showDialog("销假成功!");
            onQueryButtonClick();
        }
        else{
            MessageDialog.showDialog(res.getMsg());
        }
    }

    private void setDataView(){
        tableView.getItems().clear();
        tableView.setItems(FXCollections.observableArrayList(applicationList));
    }

    public void hasSaved() {
        onQueryButtonClick();
    }
}
