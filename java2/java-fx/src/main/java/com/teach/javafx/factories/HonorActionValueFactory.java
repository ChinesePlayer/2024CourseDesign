package com.teach.javafx.factories;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.geometry.Pos;
import javafx.scene.control.TableColumn;
import javafx.scene.layout.HBox;
import javafx.util.Callback;
import org.fatmansoft.teach.models.Course;
import org.fatmansoft.teach.models.Honor;

public class HonorActionValueFactory implements Callback<TableColumn.CellDataFeatures<Honor, HBox>, ObservableValue<HBox>> {
    @Override
    public ObservableValue<HBox> call(TableColumn.CellDataFeatures<Honor, HBox> param) {
        Honor h = param.getValue();
        if(h == null){
            return new ReadOnlyObjectWrapper<>(new HBox());
        }
        HBox res = new HBox();
        res.setAlignment(Pos.CENTER);
        res.setSpacing(5);
        res.getChildren().addAll(FXCollections.observableArrayList(h.getActions()));
        return new ReadOnlyObjectWrapper<>(res);
    }
}
