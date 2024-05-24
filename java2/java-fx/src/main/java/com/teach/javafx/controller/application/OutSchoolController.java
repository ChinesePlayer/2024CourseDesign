package com.teach.javafx.controller.application;

import com.teach.javafx.MainApplication;
import com.teach.javafx.controller.base.MessageDialog;
import com.teach.javafx.factories.ApplicationActionValueFactory;
import com.teach.javafx.factories.ApplicationValueFactory;
import com.teach.javafx.managers.WindowOpenAction;
import com.teach.javafx.managers.WindowsManager;
import com.teach.javafx.models.Student;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class OutSchoolController {
    @FXML
    public TableView<Application> tableView;
    @FXML
    public TableColumn<Application, String> studentName;
    @FXML
    public TableColumn<Application, String> applicationType;
    @FXML
    public TableColumn<Application, String> leaveDate;
    @FXML
    public TableColumn<Application, String> returnDate;
    @FXML
    public TableColumn<Application, String> status;
    @FXML
    public TableColumn<Application, HBox> actions;

    @FXML
    public ComboBox<Student> studentCombo;
    @FXML
    public ComboBox<String> statusCombo;

    private List<Student> studentList = new ArrayList<>();
    private List<Application> applicationList = new ArrayList<>();
    private Student emptyStudent;

    {
        emptyStudent = new Student();
        emptyStudent.setStudentId(0);
    }

    @FXML
    public void initialize(){
        studentName.setCellValueFactory(new ApplicationValueFactory());
        applicationType.setCellValueFactory(new ApplicationValueFactory());
        leaveDate.setCellValueFactory(new ApplicationValueFactory());
        returnDate.setCellValueFactory(new ApplicationValueFactory());
        status.setCellValueFactory(new ApplicationValueFactory());
        actions.setCellValueFactory(new ApplicationActionValueFactory());

        statusCombo.setItems(FXCollections.observableArrayList("全部","审批中","已通过","未通过","已销假"));

        onQueryButtonClick();
    }

    //查询所有学生
    public void getStudentList(){
        DataRequest req = new DataRequest();
        DataResponse res = HttpRequestUtil.request("/api/student/getStudentList",req);
        assert res != null;
        if(res.getCode() == 0){
            List<Map> rawData = (ArrayList<Map>)res.getData();
            studentList.clear();
            //添加空学生
            studentList.add(emptyStudent);
            for(Map m : rawData){
                Student s = new Student(m);
                studentList.add(s);
            }
            studentCombo.getItems().clear();
            studentCombo.setItems(FXCollections.observableArrayList(studentList));
        }
    }

    public void setDataView(){
        tableView.getItems().clear();
        tableView.setItems(FXCollections.observableArrayList(applicationList));
    }

    private Student findByStudentId(Integer studentId){
        for(Student s : studentList){
            if(Objects.equals(s.getStudentId(), studentId)){
                return s;
            }
        }
        return null;
    }

    //按筛选条件查询Application
    public void onQueryButtonClick(){
        Student s = studentCombo.getSelectionModel().getSelectedItem();
        String status = statusCombo.getSelectionModel().getSelectedItem();
        getStudentList();
        studentCombo.getSelectionModel().select(s);
        Integer studentId = null;
        Integer statusCode = null;
        if(s != null){
            studentId = s.getStudentId();
            if(s.isEmptyStudent()){
                studentId = null;
            }
        }
        if(status != null){
            statusCode = Application.getStatusCode(status);
        }
        DataRequest req = new DataRequest();
        req.add("studentId",studentId);
        req.add("status",statusCode);
        DataResponse res = HttpRequestUtil.request("/api/application/getApplicationList",req);
        assert res!=null;
        if(res.getCode() == 0){
            List<Map> rawData = (ArrayList<Map>)res.getData();
            applicationList.clear();
            for(Map m : rawData){
                Application a = new Application(m);
                Button detailCheck = new Button("查看详情");
                detailCheck.setOnAction(this::onCheckDetail);
                if(a.getStatus() == 0){
                    Button allowButton = new Button("批准");
                    Button refuseButton = new Button("拒绝");
                    refuseButton.setOnAction(this::refuseApplication);
                    allowButton.setOnAction(this::allowApplication);
                    a.getActions().add(allowButton);
                }
                a.getActions().add(detailCheck);
                applicationList.add(a);
            }
            setDataView();
        }
    }

    public void onCheckDetail(ActionEvent event){
        Application a = (Application) CommonMethod.getRowValue(event,2,tableView);
        try{
            FXMLLoader loader = new FXMLLoader(MainApplication.class.getResource("studentAffair/application-detail.fxml"));
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

    //批准假条
    public void allowApplication(ActionEvent event){
        Application a = (Application) CommonMethod.getRowValue(event,2,tableView);
        DataRequest req = new DataRequest();
        req.add("applicationId", a.getApplicationId());
        DataResponse res = HttpRequestUtil.request("/api/application/allowApplication",req);
        assert res!=null;
        if(res.getCode() == 0){
            MessageDialog.showDialog("批准成功!");
            onQueryButtonClick();
        }
        else{
            MessageDialog.showDialog(res.getMsg());
        }
    }

    //不批准假条
    public void refuseApplication(ActionEvent event){
        Application a = (Application) CommonMethod.getRowValue(event,2,tableView);
        DataRequest req = new DataRequest();
        req.add("applicationId", a.getApplicationId());
        DataResponse res = HttpRequestUtil.request("/api/application/refuseApplication",req);
        assert res!=null;
        if(res.getCode() == 0){
            MessageDialog.showDialog("已拒绝批假!");
            onQueryButtonClick();
        }
        else{
            MessageDialog.showDialog(res.getMsg());
        }
    }

    //删除假条
    public void onDeleteApplication(){
        Application a = tableView.getSelectionModel().getSelectedItem();
        if(a == null){
            MessageDialog.showDialog("未选择假条，无法删除!");
            return;
        }
        int ret = MessageDialog.choiceDialog("确定要删除该记录吗?");
        if(ret != MessageDialog.CHOICE_YES){
            return;
        }
        DataRequest req = new DataRequest();
        req.add("applicationId",a.getApplicationId());
        DataResponse res = HttpRequestUtil.request("/api/application/deleteApplication",req);
        assert res != null;
        if(res.getCode() == 0){
            MessageDialog.showDialog("删除成功");
            onQueryButtonClick();
        }
        else{
            MessageDialog.showDialog(res.getMsg());
        }
    }
}
