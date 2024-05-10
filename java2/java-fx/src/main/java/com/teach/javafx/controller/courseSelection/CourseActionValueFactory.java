package com.teach.javafx.controller.courseSelection;

import io.github.palexdev.materialfx.controls.MFXButton;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.layout.HBox;
import javafx.util.Callback;
import org.fatmansoft.teach.models.Course;

public class CourseActionValueFactory implements Callback<TableColumn.CellDataFeatures<Course, HBox>, ObservableValue<HBox>> {
    @Override
    public ObservableValue<HBox> call(TableColumn.CellDataFeatures<Course, HBox> param) {
        if(param.getValue() == null){
            return new ReadOnlyObjectWrapper<>(new HBox());
        }
        Course c = param.getValue();
        HBox buttonList = new HBox();
        buttonList.setSpacing(5);
        buttonList.setAlignment(Pos.CENTER);
        buttonList.getChildren().addAll(FXCollections.observableArrayList(c.getAction()));
        return new ReadOnlyObjectWrapper<>(buttonList);
    }
}
