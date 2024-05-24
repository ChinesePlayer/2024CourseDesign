package com.teach.javafx.factories;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;
import org.fatmansoft.teach.models.Activity;
import org.fatmansoft.teach.models.Application;
import org.fatmansoft.teach.util.CommonMethod;

public class ApplicationValueFactory implements Callback<TableColumn.CellDataFeatures<Application, String>, ObservableValue<String>> {
    @Override
    public ObservableValue<String> call(TableColumn.CellDataFeatures<Application, String> param) {
        Application a = param.getValue();
        String id = param.getTableColumn().getId();
        return switch (id) {
            case "studentName" -> CommonMethod.getSimpleStringProperty(a.getStudentName());
            case "applicationType" ->
                    CommonMethod.getSimpleStringProperty(Application.getDisplayString(a.getApplicationType()));
            case "leaveDate" -> {
                String date = a.getLeaveDate() == null ? "----" : CommonMethod.getDateString(a.getLeaveDate(), CommonMethod.DISPLAY_DATE_FORMAT);
                yield CommonMethod.getSimpleStringProperty(date);
            }
            case "returnDate" -> {
                String date = a.getReturnDate() == null ? "----" : CommonMethod.getDateString(a.getReturnDate(), CommonMethod.DISPLAY_DATE_FORMAT);
                yield CommonMethod.getSimpleStringProperty(date);
            }
            case "status" -> CommonMethod.getSimpleStringProperty(Application.getStatusName(a.getStatus()));
            default -> CommonMethod.getSimpleStringProperty("----");
        };
    }
}
