package com.teach.javafx.controller;

import com.teach.javafx.AppStore;
import com.teach.javafx.MainApplication;
import com.teach.javafx.controller.base.FamilyMemberEditorOpener;
import com.teach.javafx.controller.base.MessageDialog;
import com.teach.javafx.factories.FamilyMemberValueFactory;
import com.teach.javafx.managers.WindowOpenAction;
import com.teach.javafx.managers.WindowsManager;
import com.teach.javafx.models.Student;
import com.teach.javafx.request.HttpRequestUtil;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Modality;
import org.fatmansoft.teach.models.FamilyMember;
import org.fatmansoft.teach.payload.request.DataRequest;
import org.fatmansoft.teach.payload.response.DataResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FamilyMemberController implements FamilyMemberEditorOpener {
    @FXML
    public TableColumn<FamilyMember, String> memberNum;
    @FXML
    public TableColumn<FamilyMember, String> memberName;
    @FXML
    public TableColumn<FamilyMember, String> relation;
    @FXML
    public TableColumn<FamilyMember, String> gender;
    @FXML
    public TableColumn<FamilyMember, String> birth;
    @FXML
    public TableColumn<FamilyMember, String> unit;
    @FXML
    public TableView<FamilyMember> tableView;
    public Label bigTitle;
    private Student student;
    private List<FamilyMember> familyMemberList = new ArrayList<>();

    @FXML
    public void initialize(){
        memberName.setCellValueFactory(new FamilyMemberValueFactory());
        memberNum.setCellValueFactory(new FamilyMemberValueFactory());
        relation.setCellValueFactory(new FamilyMemberValueFactory());
        gender.setCellValueFactory(new FamilyMemberValueFactory());
        birth.setCellValueFactory(new FamilyMemberValueFactory());
        unit.setCellValueFactory(new FamilyMemberValueFactory());
    }

    public void init(Student s) {
        this.student = s;
        getFamilyMemberList();
    }

    public void getFamilyMemberList(){
        DataRequest req = new DataRequest();
        req.add("studentId", student.getStudentId());
        DataResponse res = HttpRequestUtil.request("/api/student/getFamilyMemberList",req);
        assert res != null;
        if(res.getCode() == 0){
            List<Map> rawData = (ArrayList<Map>)res.getData();
            familyMemberList.clear();
            for(Map m : rawData){
                FamilyMember fm = new FamilyMember(m);
                familyMemberList.add(fm);
            }
            setDataView();
        }
    }

    public void setDataView(){
        tableView.getItems().clear();
        tableView.setItems(FXCollections.observableArrayList(familyMemberList));
        bigTitle.setText(student.getStudentName() + " 的家庭成员");
        bigTitle.setStyle("-fx-font-size: 30px");
    }

    //添加家庭成员
    public void onAddFamilyMember(){
        try{
            FXMLLoader loader = new FXMLLoader(MainApplication.class.getResource("family-member-editor.fxml"));
            WindowsManager.getInstance().openNewWindow(
                    loader, 400, 500, "新增家庭成员",
                    tableView.getScene().getWindow(), Modality.WINDOW_MODAL,
                    new WindowOpenAction() {
                        @Override
                        public void init(Object controller) {
                            WindowOpenAction.super.init(controller);
                            FamilyMemberEditorController fmeCont = (FamilyMemberEditorController) controller;
                            fmeCont.init(null, student.getStudentId(), FamilyMemberController.this);
                        }
                    }
            );
        }
        catch (IOException e){
            e.printStackTrace();
            MessageDialog.showDialog("无法打开新增页面");
        }
    }

    //删除家庭成员
    public void onDeleteFamilyMember(){
        FamilyMember fm = tableView.getSelectionModel().getSelectedItem();
        if(fm == null){
            MessageDialog.showDialog("请选择一个家庭成员删除!");
            return;
        }
        int ret = MessageDialog.choiceDialog("你确定要删除 " + fm.getName() + " 吗?");
        if(ret != MessageDialog.CHOICE_YES){
            return;
        }
        DataRequest req = new DataRequest();
        req.add("memberId", fm.getMemberId());
        DataResponse res = HttpRequestUtil.request("/api/student/familyMemberDelete",req);
        assert res != null;
        if(res.getCode() == 0){
            MessageDialog.showDialog("删除成功!");
            //刷新数据
            getFamilyMemberList();
        }
        else{
            MessageDialog.showDialog(res.getMsg());
        }
    }

    //编辑家庭成员
    public void onEditFamilyMember(){
        FamilyMember fm = tableView.getSelectionModel().getSelectedItem();
        if(fm == null){
            MessageDialog.showDialog("请选择一个家庭成员来编辑");
            return;
        }
        try{
            FXMLLoader loader = new FXMLLoader(MainApplication.class.getResource("family-member-editor.fxml"));
            WindowsManager.getInstance().openNewWindow(
                    loader, 400, 500, "编辑: " + fm.getName(),
                    tableView.getScene().getWindow(), Modality.WINDOW_MODAL,
                    new WindowOpenAction() {
                        @Override
                        public void init(Object controller) {
                            WindowOpenAction.super.init(controller);
                            FamilyMemberEditorController fmeCont = (FamilyMemberEditorController) controller;
                            fmeCont.init(fm, student.getStudentId(), FamilyMemberController.this);
                        }
                    }
            );
        }
        catch (IOException e){
            e.printStackTrace();
            MessageDialog.showDialog("无法打开编辑页面!");
        }
    }

    public void hasSaved() {
        getFamilyMemberList();
    }
}
