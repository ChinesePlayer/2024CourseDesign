package com.teach.javafx.factories;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;
import org.fatmansoft.teach.models.FamilyMember;
import org.fatmansoft.teach.models.Honor;
import org.fatmansoft.teach.util.CommonMethod;

public class HonorValueFactory implements Callback<TableColumn.CellDataFeatures<Honor, String>, ObservableValue<String>> {
    @Override
    public ObservableValue<String> call(TableColumn.CellDataFeatures<Honor, String> param) {
        Honor h = param.getValue();
        String id = param.getTableColumn().getId();
        if(h == null){
            return CommonMethod.getSimpleStringProperty("----");
        }
        return switch (id) {
            case "studentName" ->
                    new SimpleStringProperty(h.getStudentName() == null ? "----" : h.getStudentName());
            case "studentNum" -> new SimpleStringProperty(h.getStudentNum() == null ? "----" : h.getStudentNum());
            case "honorType" -> new SimpleStringProperty(h.getHonorType() == null ? "----" : Honor.getTypeName(h.getHonorType()));
            case "honorContent" -> new SimpleStringProperty(h.getHonorContent() == null ? "----" : h.getHonorContent());
            default -> new SimpleStringProperty("----");
        };
    }
}
