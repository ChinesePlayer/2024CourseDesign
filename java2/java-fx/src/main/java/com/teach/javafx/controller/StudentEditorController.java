package com.teach.javafx.controller;

import com.teach.javafx.controller.StudentController;
import com.teach.javafx.controller.base.MessageDialog;
import com.teach.javafx.models.Student;
import com.teach.javafx.request.HttpRequestUtil;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.stage.Stage;
import org.fatmansoft.teach.payload.request.DataRequest;
import org.fatmansoft.teach.payload.response.DataResponse;
import org.fatmansoft.teach.util.CommonMethod;

import java.util.HashMap;
import java.util.Map;

public class StudentEditorController {
    @FXML
    public MFXTextField studentNumField;
    @FXML
    public MFXTextField studentNameField;
    @FXML
    public MFXTextField deptField;
    @FXML
    public MFXTextField majorField;
    @FXML
    public MFXTextField classNameField;
    @FXML
    public MFXTextField cardField;
    @FXML
    public MFXTextField emailField;
    @FXML
    public MFXTextField phoneField;
    @FXML
    public MFXTextField addressField;
    @FXML
    public ComboBox<String> genderCombo;
    @FXML
    public DatePicker birthPicker;

    private Student student;
    private StudentController opener;

    private Map<String, Integer> genderMap = new HashMap<>();

    {
        genderMap.put("男", 1);
        genderMap.put("女", 2);
    }

    @FXML
    public void initialize(){
        genderCombo.setItems(FXCollections.observableArrayList("男", "女"));
    }

    public void init(Student s, StudentController studentController) {
        this.student = s;
        this.opener = studentController;
        setDataView(s);
    }

    //根据传入的数据来设置视图显示
    public void setDataView(Student s){
        if(s == null){
            return;
        }
        studentNameField.setText(s.getStudentName());
        studentNumField.setText(s.getStudentNum());
        deptField.setText(s.getDept());
        majorField.setText(s.getMajor());
        classNameField.setText(s.getClassName());
        cardField.setText(s.getCard());
        emailField.setText(s.getEmail());
        phoneField.setText(s.getPhone());
        addressField.setText(s.getAddress());
        birthPicker.setValue(s.getBirthday());
        genderCombo.getSelectionModel().select(s.getGenderName());
    }

    //保存数据
    public void onSave(){
        if( studentNumField.getText().equals("")) {
            MessageDialog.showDialog("学号为空，不能修改");
            return;
        }

        //判断邮箱是否合法，不合法就拒绝提交并弹出提示
        if(!CommonMethod.isValidEmail(emailField.getText()) && (emailField.getText() != null && !emailField.getText().isEmpty())){
            MessageDialog.showDialog("邮箱格式不合法!");
            return;
        }
        Map form = new HashMap();
        form.put("personNum",studentNumField.getText());
        form.put("personName",studentNameField.getText());
        form.put("dept",deptField.getText());
        form.put("major",majorField.getText());
        form.put("className",classNameField.getText());
        form.put("card",cardField.getText());
        if(genderCombo.getSelectionModel() != null && genderCombo.getSelectionModel().getSelectedItem() != null)
            form.put("gender",genderMap.get(genderCombo.getSelectionModel().getSelectedItem()));
        form.put("birthday",birthPicker.getValue() == null ? null: CommonMethod.getDateString(birthPicker.getValue(),CommonMethod.DATE_FORMAT));
        form.put("email",emailField.getText());
        form.put("phone",phoneField.getText());
        form.put("address",addressField.getText());
        DataRequest req = new DataRequest();
        req.add("studentId", this.student == null ? null : this.student.getStudentId());
        req.add("form", form);
        DataResponse res = HttpRequestUtil.request("/api/student/studentEditSave",req);
        if(res.getCode() == 0) {
            MessageDialog.showDialog("保存成功！");
            opener.hasSaves();
            //关闭当前窗口
            ((Stage)studentNumField.getScene().getWindow()).close();
        }
        else {
            MessageDialog.showDialog(res.getMsg());
        }

    }
}
