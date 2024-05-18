package com.teach.javafx.controller.base;

import com.teach.javafx.controller.StudentController;
import com.teach.javafx.models.Student;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;

import java.util.Map;

public class StudentEditorController {
    @FXML
    public MFXTextField studentIdField;
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

    public void init(Student s, StudentController studentController) {
        this.student = s;
        this.opener = studentController;
    }
}
