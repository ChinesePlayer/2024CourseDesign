package com.teach.javafx.factories;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.layout.HBox;
import javafx.util.Callback;
import org.fatmansoft.teach.models.Attendance;

import java.util.List;

public class AttendanceActionFactory implements Callback<TableColumn.CellDataFeatures<Attendance,HBox>, ObservableValue<HBox>> {

    @Override
    public ObservableValue<HBox> call(TableColumn.CellDataFeatures<Attendance, HBox> param) {
        Attendance a = param.getValue();
        List<Button> buttons = a.getActions();
        HBox buttonBox = new HBox();
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setSpacing(5);
        buttonBox.getChildren().addAll(buttons);
        return new ReadOnlyObjectWrapper<>(buttonBox);
    }
}
