package com.teach.javafx.controller.studentFamily;

import com.teach.javafx.AppStore;
import com.teach.javafx.MainApplication;
import com.teach.javafx.controller.FamilyMemberController;
import com.teach.javafx.controller.FamilyMemberEditorController;
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

public class FamilyManage implements FamilyMemberEditorOpener {
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
    private List<FamilyMember> familyMemberList = new ArrayList<>();

    @FXML
    public void initialize(){
        memberName.setCellValueFactory(new FamilyMemberValueFactory());
        memberNum.setCellValueFactory(new FamilyMemberValueFactory());
        relation.setCellValueFactory(new FamilyMemberValueFactory());
        gender.setCellValueFactory(new FamilyMemberValueFactory());
        birth.setCellValueFactory(new FamilyMemberValueFactory());
        unit.setCellValueFactory(new FamilyMemberValueFactory());
        getFamilyMemberList();
    }

    public void getFamilyMemberList(){
        DataRequest req = new DataRequest();
        req.add("studentId", AppStore.getJwt().getRoleId());
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
                            fmeCont.init(null, AppStore.getJwt().getRoleId(), FamilyManage.this);
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
                            fmeCont.init(fm, AppStore.getJwt().getRoleId(), FamilyManage.this);
                        }
                    }
            );
        }
        catch (IOException e){
            e.printStackTrace();
            MessageDialog.showDialog("无法打开编辑页面!");
        }
    }

    public void setDataView(){
        tableView.getItems().clear();
        tableView.setItems(FXCollections.observableArrayList(familyMemberList));
    }

    @Override
    public void hasSaved() {
        getFamilyMemberList();
    }
}
