package com.teach.javafx.factories;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.layout.HBox;
import javafx.util.Callback;
import org.fatmansoft.teach.models.Activity;

import java.util.List;

public class ActivityActionsValueFactory implements Callback<TableColumn.CellDataFeatures<Activity,HBox>, ObservableValue<HBox>> {

    @Override
    public ObservableValue<HBox> call(TableColumn.CellDataFeatures<Activity, HBox> param) {
        Activity a = param.getValue();

        List<Button> buttons = a.getActions();
        HBox buttonBox = new HBox();
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setSpacing(5);
        if(a.getActions() == null){
            return new ReadOnlyObjectWrapper<>(buttonBox);
        }
        buttonBox.getChildren().addAll(buttons);
        return new ReadOnlyObjectWrapper<>(buttonBox);
    }
}