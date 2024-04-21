package com.teach.javafx.controller.courseSelection;

import io.github.palexdev.materialfx.controls.MFXButton;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;
import org.fatmansoft.teach.models.Course;

public class CourseActionValueFactory implements Callback<TableColumn.CellDataFeatures<Course, MFXButton>, ObservableValue<MFXButton>> {
    @Override
    public ObservableValue<MFXButton> call(TableColumn.CellDataFeatures<Course, MFXButton> param) {
        Course c = param.getValue();
        MFXButton button = c.getAction();
        return new ReadOnlyObjectWrapper<>(button);
    }
}
