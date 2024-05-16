package com.teach.javafx.factories;

import io.github.palexdev.materialfx.controls.MFXButton;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.layout.HBox;
import javafx.util.Callback;
import org.fatmansoft.teach.models.Course;

public class CourseAdminActionValueFactory implements Callback<TableColumn.CellDataFeatures<Course, HBox>, ObservableValue<HBox>> {
    @Override
    public ObservableValue<HBox> call(TableColumn.CellDataFeatures<Course, HBox> param) {
        Course c = param.getValue();
        HBox hBox = new HBox();
        hBox.setAlignment(Pos.CENTER);
        hBox.getChildren().addAll(FXCollections.observableArrayList(c.getAction()));
        return new ReadOnlyObjectWrapper<>(hBox);
    }
}
