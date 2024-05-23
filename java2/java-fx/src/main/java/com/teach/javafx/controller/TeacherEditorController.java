package com.teach.javafx.controller;

import com.teach.javafx.controller.base.MessageDialog;
import com.teach.javafx.models.Student;
import com.teach.javafx.request.HttpRequestUtil;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.stage.Stage;
import org.fatmansoft.teach.models.Person;
import org.fatmansoft.teach.models.Teacher;
import org.fatmansoft.teach.payload.request.DataRequest;
import org.fatmansoft.teach.payload.response.DataResponse;
import org.fatmansoft.teach.util.CommonMethod;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class TeacherEditorController {
    @FXML
    public MFXTextField teacherNumField;
    @FXML
    public MFXTextField teacherNameField;
    @FXML
    public MFXTextField deptField;
    @FXML
    public MFXTextField titleField;
    @FXML
    public MFXTextField degreeField;
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

    private TeacherController opener;
    private Teacher teacher;

    private final Map<String, Integer> genderMap = new HashMap<>();

    {
        genderMap.put("男", 1);
        genderMap.put("女", 2);
    }

    @FXML
    public void initialize(){
        genderCombo.setItems(FXCollections.observableArrayList("男", "女"));
    }

    public void init(Teacher t, TeacherController teacherController) {
        this.teacher = t;
        this.opener = teacherController;
        setDataView(t);
    }

    private void setDataView(Teacher t) {
        if(t == null){
            return;
        }
        teacherNameField.setText(t.getPerson().getPersonName());
        teacherNumField.setText(t.getPerson().getPersonNum());
        deptField.setText(t.getPerson().getDept());
        titleField.setText(t.getTitle());
        degreeField.setText(t.getDegree());
        cardField.setText(t.getPerson().getCard());
        emailField.setText(t.getPerson().getEmail());
        phoneField.setText(t.getPerson().getPhone());
        addressField.setText(t.getPerson().getAddress());
        LocalDate birth = CommonMethod.getLocalDateFromString(t.getPerson().getBirthday(),CommonMethod.DATE_FORMAT);
        birthPicker.setValue(birth);
        genderCombo.getSelectionModel().select(t.getPerson().getGenderName());
    }

    public void onSave(){
        if( teacherNumField.getText().equals("")) {
            MessageDialog.showDialog("工号为空，不能修改");
            return;
        }
        //判断邮箱是否合法，不合法就拒绝提交并弹出提示
        if(!CommonMethod.isValidEmail(emailField.getText()) && (emailField.getText() != null && !emailField.getText().isEmpty())){
            MessageDialog.showDialog("邮箱格式不合法!");
            return;
        }
        Teacher t = new Teacher();
        Person p = new Person();
        t.setPerson(p);
        p.setPersonNum(teacherNumField.getText());
        p.setPersonName(teacherNameField.getText());
        p.setDept(deptField.getText());
        t.setTitle(titleField.getText());
        t.setDegree(degreeField.getText());
        p.setCard(cardField.getText());
        if(genderCombo.getSelectionModel() != null && genderCombo.getSelectionModel().getSelectedItem() != null)
            p.setGender(genderMap.get(genderCombo.getSelectionModel().getSelectedItem()) == null? null : genderMap.get(genderCombo.getSelectionModel().getSelectedItem()).toString());
        p.setBirthday(birthPicker.getValue() == null ? null : CommonMethod.getDateString(birthPicker.getValue(), CommonMethod.DATE_FORMAT));
        p.setEmail(emailField.getText());
        p.setPhone(phoneField.getText());
        p.setAddress(addressField.getText());
        DataRequest req = new DataRequest();
        req.add("teacherId", teacher == null ? null : teacher.getTeacherId());
        req.add("form", t);
        DataResponse res = HttpRequestUtil.request("/api/teacher/teacherEditSave",req);
        if(res.getCode() == 0) {
            MessageDialog.showDialog("提交成功！");
            ((Stage)teacherNameField.getScene().getWindow()).close();
            opener.hasSaved();
        }
        else {
            MessageDialog.showDialog(res.getMsg());
        }
    }
}
