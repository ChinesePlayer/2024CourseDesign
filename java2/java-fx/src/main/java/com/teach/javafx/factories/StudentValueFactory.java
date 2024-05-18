package com.teach.javafx.factories;

import com.teach.javafx.models.Student;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;
import org.fatmansoft.teach.models.Score;
import org.fatmansoft.teach.util.CommonMethod;

import java.time.LocalDate;

public class StudentValueFactory implements Callback<TableColumn.CellDataFeatures<Student, String>, ObservableValue<String>> {
    @Override
    public ObservableValue<String> call(TableColumn.CellDataFeatures<Student, String> param) {
        Student s = param.getValue();
        String id = param.getTableColumn().getId();
        if(s == null){
            return new SimpleStringProperty("----");
        }
        switch (id){
            case "numColumn":{
                return new SimpleStringProperty(s.getStudentNum() == null ? "----" : s.getStudentNum());
            }
            case "nameColumn":{
                return new SimpleStringProperty(s.getStudentName() == null ? "----" : s.getStudentName());
            }
            case "deptColumn":{
                return new SimpleStringProperty(s.getDept() == null ? "----" : s.getDept());
            }
            case "majorColumn":{
                return new SimpleStringProperty(s.getMajor() == null ? "----" : s.getMajor());
            }
            case "classNameColumn":{
                return new SimpleStringProperty(s.getClassName() == null ? "----" : s.getClassName());
            }
            case "cardColumn":{
                return new SimpleStringProperty(s.getCard() == null ? "----" : s.getCard());
            }
            case "genderColumn":{
                return new SimpleStringProperty(s.getGenderName() == null ? "----" : s.getGenderName());
            }
            case "birthdayColumn":{
                LocalDate birth = s.getBirthday();
                String str = birth == null? "----" : CommonMethod.getDateString(birth,CommonMethod.DATE_FORMAT);
                return new SimpleStringProperty(str);
            }
            case "emailColumn":{
                return new SimpleStringProperty(s.getEmail() == null ? "----" : s.getEmail());
            }
            case "phoneColumn":{
                return new SimpleStringProperty(s.getPhone() == null ? "----" : s.getPhone());
            }
            case "addressColumn":{
                return new SimpleStringProperty(s.getAddress() == null ? "----" : s.getAddress());
            }
            default:{
                return new SimpleStringProperty("----");
            }
        }
    }
}
