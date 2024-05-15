package com.teach.javafx.controller.adminAttendance;

import com.teach.javafx.MainApplication;
import com.teach.javafx.controller.base.AttendanceEditorOpener;
import com.teach.javafx.controller.base.MessageDialog;
import com.teach.javafx.factories.AttendanceActionFactory;
import com.teach.javafx.factories.AttendanceValueFactory;
import com.teach.javafx.managers.WindowOpenAction;
import com.teach.javafx.managers.WindowsManager;
import com.teach.javafx.request.HttpRequestUtil;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import org.fatmansoft.teach.models.Attendance;
import org.fatmansoft.teach.payload.request.DataRequest;
import org.fatmansoft.teach.payload.response.DataResponse;
import org.fatmansoft.teach.util.CommonMethod;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AttendancePanelController implements AttendanceEditorOpener {
    @FXML
    public TableView<Attendance> tableView;
    @FXML
    public TableColumn<Attendance, String> studentName;
    @FXML
    public TableColumn<Attendance, String> date;
    @FXML
    public TableColumn<Attendance, String> status;
    @FXML
    public TableColumn<Attendance, String> courseName;
    @FXML
    public TableColumn<Attendance, String> studentNum;
    @FXML
    public TableColumn<Attendance, String> courseNum;
    @FXML
    public TableColumn<Attendance, HBox> actions;

    @FXML
    public MFXTextField courseNameField;
    @FXML
    public MFXTextField studentNameField;

    public List<Attendance> attendanceList = new ArrayList<>();

    public ObservableList<Attendance> observableList = FXCollections.observableArrayList();



    @FXML
    public void initialize(){
        studentName.setCellValueFactory(new AttendanceValueFactory());
        date.setCellValueFactory(new AttendanceValueFactory());
        status.setCellValueFactory(new AttendanceValueFactory());
        courseName.setCellValueFactory(new AttendanceValueFactory());
        actions.setCellValueFactory(new AttendanceActionFactory());
        studentNum.setCellValueFactory(new AttendanceValueFactory());
        courseNum.setCellValueFactory(new AttendanceValueFactory());
        onQueryButtonClick();
    }

    public void onQueryButtonClick(){
        //根据当前查询条件，从后端更新attendanceList
        String courseName = courseNameField.getText() == null ? "" : courseNameField.getText();
        String studentName = studentNameField.getText() == null ? "" : studentNameField.getText();
        DataRequest req = new DataRequest();
        req.add("courseName", courseName);
        req.add("studentName", studentName);
        DataResponse res = HttpRequestUtil.request("/api/attendance/getAttendanceList",req);
        assert res != null;
        if(res.getCode() == 0){
            attendanceList.clear();
            List<Map> rawData = (ArrayList<Map>) res.getData();
            for(Map m : rawData){
                Attendance a = new Attendance(m);
                List<Button> buttonList = new ArrayList<>();
                Button editButton = new Button("编辑");
                editButton.setOnAction(this::onEditButtonClick);
                buttonList.add(editButton);
                a.setActions(buttonList);
                attendanceList.add(a);
            }
            setDataView();
        }
        else{
            MessageDialog.showDialog("无法获取考勤信息");
        }
    }

    public void setDataView(){
        observableList.clear();
        observableList.addAll(attendanceList);
        tableView.setItems(observableList);
    }

    public void onClearQueryConditions(){
        studentNameField.setText("");
        courseNameField.setText("");
        onQueryButtonClick();
    }

    public void onEditButtonClick(ActionEvent event){
        //获取被点击的编辑按钮的那一行的Attendance数据
        Attendance attendance = (Attendance) CommonMethod.getRowValue(event, 2,tableView);
        try{
            FXMLLoader loader = new FXMLLoader(MainApplication.class.getResource("adminAttendance/attendance-editor.fxml"));
            WindowsManager.getInstance().openNewWindow(
                    loader, 400, 300, "编辑考勤信息",
                    tableView.getScene().getWindow(), Modality.WINDOW_MODAL,
                    new WindowOpenAction() {
                        @Override
                        public void init(Object controller) {
                            WindowOpenAction.super.init(controller);
                            AttendanceEditorController cont = (AttendanceEditorController)  controller;
                            cont.init(attendance, AttendancePanelController.this);
                        }
                    }
            );
        }
        catch (IOException e){
            e.printStackTrace();
            MessageDialog.showDialog("打开编辑页面失败! ");
        }
    }

    public void hasSaved(){
        //直接刷新数据
        onQueryButtonClick();
    }

    //新增考勤信息的的操作
    public void onNewAttendance(){
        try{
            FXMLLoader loader = new FXMLLoader(MainApplication.class.getResource("adminAttendance/attendance-editor.fxml"));
            WindowsManager.getInstance().openNewWindow(
                    loader, 400, 300, "新增考勤信息",
                    tableView.getScene().getWindow(), Modality.WINDOW_MODAL,
                    new WindowOpenAction() {
                        @Override
                        public void init(Object controller) {
                            WindowOpenAction.super.init(controller);
                            AttendanceEditorController cont = (AttendanceEditorController) controller;
                            cont.init(null, AttendancePanelController.this);
                        }
                    }
            );
        }
        catch (IOException e){
            e.printStackTrace();
            MessageDialog.showDialog("打开新增考勤信息窗口失败! ");
        }
    }

    public void onDeleteAttendance(){
        Attendance a = tableView.getSelectionModel().getSelectedItem();

        if(a == null){
            MessageDialog.showDialog("未选中，无法删除");
            return;
        }
        //让用户确认是否删除考勤信息
        int ret = MessageDialog.choiceDialog("你确定要删除该考勤吗? ");
        if(ret != MessageDialog.CHOICE_YES){
            return;
        }
        DataRequest req = new DataRequest();
        req.add("attendanceId", a.getAttendanceId());
        DataResponse res = HttpRequestUtil.request("/api/attendance/deleteAttendance", req);
        assert res != null;
        if(res.getCode()==0){
            MessageDialog.showDialog("删除成功! ");
            //刷新数据
            onQueryButtonClick();
        }
        else{
            MessageDialog.showDialog(res.getMsg());
        }
    }

}
