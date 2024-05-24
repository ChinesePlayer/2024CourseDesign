package com.teach.javafx.factories;

import com.teach.javafx.models.Student;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;
import org.fatmansoft.teach.models.FamilyMember;
import org.fatmansoft.teach.util.CommonMethod;

import java.time.LocalDate;

public class FamilyMemberValueFactory implements Callback<TableColumn.CellDataFeatures<FamilyMember, String>, ObservableValue<String>> {
    @Override
    public ObservableValue<String> call(TableColumn.CellDataFeatures<FamilyMember, String> param) {
        FamilyMember fm = param.getValue();
        String id = param.getTableColumn().getId();
        if(fm == null){
            return new SimpleStringProperty("----");
        }
        return switch (id) {
            case "memberNum" ->
                    new SimpleStringProperty(fm.getMemberId() == null ? "----" : fm.getMemberId().toString());
            case "memberName" -> new SimpleStringProperty(fm.getName() == null ? "----" : fm.getName());
            case "relation" -> new SimpleStringProperty(fm.getRelation() == null ? "----" : fm.getRelation());
            case "gender" -> new SimpleStringProperty(fm.getGender() == null ? "----" : fm.getGender());
            case "birth" -> {
                String birth = fm.getBirthday() == null ? "----" : CommonMethod.getDateString(fm.getBirthday(), CommonMethod.DATE_FORMAT);
                yield new SimpleStringProperty(birth);
            }
            case "unit" -> new SimpleStringProperty(fm.getUnit() == null ? "----" : fm.getUnit());
            default -> new SimpleStringProperty("----");
        };
    }
}
