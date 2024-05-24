package com.teach.javafx.controller;

import com.teach.javafx.controller.base.FamilyMemberEditorOpener;
import com.teach.javafx.controller.base.MessageDialog;
import com.teach.javafx.request.HttpRequestUtil;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.stage.Stage;
import org.fatmansoft.teach.models.FamilyMember;
import org.fatmansoft.teach.payload.request.DataRequest;
import org.fatmansoft.teach.payload.response.DataResponse;
import org.fatmansoft.teach.util.CommonMethod;

import java.util.HashMap;
import java.util.Map;

public class FamilyMemberEditorController {
    @FXML
    public MFXTextField nameField;
    @FXML
    public MFXTextField relationField;
    @FXML
    public MFXTextField unitField;
    @FXML
    public ComboBox<String> genderCombo;
    @FXML
    public DatePicker birthPicker;

    private FamilyMember familyMember;
    private Integer studentId;
    private FamilyMemberEditorOpener opener;

    @FXML
    public void initialize(){
        genderCombo.setItems(FXCollections.observableArrayList("男", "女"));
    }

    public void init(FamilyMember fm,Integer stuId, FamilyMemberEditorOpener fmc){
        this.familyMember = fm;
        this.studentId = stuId;
        this.opener = fmc;
        setDataView(fm);
    }

    public void setDataView(FamilyMember fm){
        if(fm == null){
            return;
        }
        nameField.setText(fm.getName());
        relationField.setText(fm.getRelation());
        unitField.setText(fm.getUnit());
        birthPicker.setValue(fm.getBirthday());
        genderCombo.getSelectionModel().select(fm.getGender());
    }

    public void onSave(){
        //验证数据有效性
        //工作单位不要求强制输入
        if(nameField.getText() == null || nameField.getText().isEmpty()){
            MessageDialog.showDialog("请输入姓名!");
            return;
        }
        if(relationField.getText() == null || relationField.getText().isEmpty()){
            MessageDialog.showDialog("请输入与该学生的关系");
            return;
        }
        if(genderCombo.getSelectionModel().getSelectedItem() == null){
            MessageDialog.showDialog("请选择性别");
            return;
        }
        if(birthPicker.getValue() == null){
            MessageDialog.showDialog("请选择生日");
            return;
        }
        DataRequest req = new DataRequest();
        Map form = new HashMap<>();
        form.put("studentId",studentId);
        form.put("memberId",familyMember == null ? null : familyMember.getMemberId());
        form.put("name",nameField.getText());
        form.put("relation",relationField.getText());
        form.put("unit",unitField.getText());
        form.put("gender",genderCombo.getSelectionModel().getSelectedItem());
        form.put("birthday", CommonMethod.getDateString(birthPicker.getValue(),CommonMethod.DATE_FORMAT));
        req.add("form",form);
        DataResponse res = HttpRequestUtil.request("/api/student/familyMemberSave",req);
        assert res != null;
        if(res.getCode() == 0){
            MessageDialog.showDialog("保存成功!");
            ((Stage)nameField.getScene().getWindow()).close();
            opener.hasSaved();
        }
        else{
            MessageDialog.showDialog(res.getMsg());
        }
    }
}
