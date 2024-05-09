package com.teach.javafx.controller.courseSelection;

import io.github.palexdev.materialfx.controls.MFXButton;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.layout.HBox;
import javafx.util.Callback;
import org.fatmansoft.teach.models.Course;

public class CourseActionValueFactory implements Callback<TableColumn.CellDataFeatures<Course, Button>, ObservableValue<Button>> {
    @Override
    public ObservableValue<Button> call(TableColumn.CellDataFeatures<Course, Button> param) {
        Course c = param.getValue();
        HBox buttonList = new HBox();
        buttonList.getChildren().addAll(FXCollections.observableArrayList(c.getAction()));
        return new ReadOnlyObjectWrapper<>(HBox);
    }
}
