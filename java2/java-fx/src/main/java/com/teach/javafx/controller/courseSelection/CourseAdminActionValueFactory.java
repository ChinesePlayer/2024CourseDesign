package com.teach.javafx.controller.courseSelection;

import io.github.palexdev.materialfx.controls.MFXButton;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;
import org.fatmansoft.teach.models.Course;

public class CourseAdminActionValueFactory implements Callback<TableColumn.CellDataFeatures<Course, Button>, ObservableValue<Button>> {
    @Override
    public ObservableValue<Button> call(TableColumn.CellDataFeatures<Course, Button> param) {
        Course c = param.getValue();
        Button button = c.getAction();
        return new ReadOnlyObjectWrapper<>(button);
    }
}
