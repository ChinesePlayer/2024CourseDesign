package com.teach.javafx.customWidget;

import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXSpinner;
import io.github.palexdev.materialfx.controls.models.spinner.IntegerSpinnerModel;
import io.github.palexdev.materialfx.controls.models.spinner.SpinnerModel;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.fatmansoft.teach.models.Course;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import javafx.geometry.Insets;

//用于管理员编辑课程时选择课程的上课时间
public class TimeSelector extends VBox {
    private Integer courseTimeId;
    private MFXSpinner<Integer> day = new MFXSpinner<>();
    private MFXSpinner<Integer> section = new MFXSpinner<>();

    //用于外部对该对象执行一些操作
    private MFXButton action = null;

    public TimeSelector(){
        setStyle("-fx-padding: 20px");
        Label dayL = new Label("星    期    :");
        dayL.setPadding(new Insets(0, 0, 0, 30));
        Label sectionL = new Label("节    次    :");
        sectionL.setPadding(new Insets(0, 0, 0, 30));
        dayL.setStyle("-fx-font-size: 19px; -fx-font-family: Arial;");
        sectionL.setStyle("-fx-font-size: 19px; -fx-font-family: Arial;");
        HBox dayBox = new HBox();
        dayBox.setSpacing(30);
        HBox sectionBox = new HBox();
        sectionBox.setSpacing(30);
        //设置星期和节次的选择模型
        IntegerSpinnerModel integerSpinnerModel = new IntegerSpinnerModel();
        integerSpinnerModel.setIncrement(1);
        integerSpinnerModel.setMax(7);
        integerSpinnerModel .setMin(1);
        this.day.setSpinnerModel(integerSpinnerModel);

        integerSpinnerModel = new IntegerSpinnerModel();
        integerSpinnerModel.setIncrement(1);
        integerSpinnerModel.setMax(5);
        integerSpinnerModel.setMin(1);
        this.section.setSpinnerModel(integerSpinnerModel);

        dayBox.getChildren().addAll(dayL, this.day);
        sectionBox.getChildren().addAll(sectionL, this.section);
        dayBox.setAlignment(Pos.CENTER);
        sectionBox.setAlignment(Pos.CENTER);

        getChildren().addAll(dayBox, sectionBox);
        setAlignment(Pos.TOP_CENTER);
        setSpacing(40);
    }

    public TimeSelector(MFXButton action){
        setStyle("-fx-padding: 20px");
        Label dayL = new Label("星    期    :");
        dayL.setPadding(new Insets(0, 0, 0, 30));
        Label sectionL = new Label("节    次    :");
        sectionL.setPadding(new Insets(0, 0, 0, 30));
        dayL.setStyle("-fx-font-size: 19px; -fx-font-family: Arial;");
        sectionL.setStyle("-fx-font-size: 19px; -fx-font-family: Arial;");
        HBox dayBox = new HBox();
        dayBox.setSpacing(30);
        HBox sectionBox = new HBox();
        sectionBox.setSpacing(30);
        //设置星期和节次的选择模型
        IntegerSpinnerModel integerSpinnerModel = new IntegerSpinnerModel();
        integerSpinnerModel.setIncrement(1);
        integerSpinnerModel.setMax(7);
        integerSpinnerModel .setMin(1);
        this.day.setSpinnerModel(integerSpinnerModel);

        integerSpinnerModel = new IntegerSpinnerModel();
        integerSpinnerModel.setIncrement(1);
        integerSpinnerModel.setMax(5);
        integerSpinnerModel.setMin(1);
        this.section.setSpinnerModel(integerSpinnerModel);

        this.day.setValue(1);
        this.section.setValue(1);
        this.action = action;
        dayBox.getChildren().addAll(dayL, this.day);
        sectionBox.getChildren().addAll(sectionL, this.section);
        dayBox.setAlignment(Pos.CENTER);
        sectionBox.setAlignment(Pos.CENTER);

        getChildren().addAll( action, dayBox, sectionBox);
        setAlignment(Pos.TOP_CENTER);
        setSpacing(40);
    }

    public TimeSelector(int day, int section, MFXButton action){
        setStyle("-fx-padding: 20px");
        Label dayL = new Label("星    期    :");
        dayL.setPadding(new Insets(0, 0, 0, 30));
        /*VBox.setMargin(dayL, new Insets(10, 10, 200, 10));*/
        Label sectionL = new Label("节    次    :");
        sectionL.setPadding(new Insets(0, 0, 0, 30));
        dayL.setStyle("-fx-font-size: 19px; -fx-font-family: Arial;");
        sectionL.setStyle("-fx-font-size: 19px; -fx-font-family: Arial;");
        HBox dayBox = new HBox();
        dayBox.setSpacing(30);
        HBox sectionBox = new HBox();
        sectionBox.setSpacing(30);
        //设置星期和节次的选择模型
        IntegerSpinnerModel integerSpinnerModel = new IntegerSpinnerModel();
        integerSpinnerModel.setIncrement(1);
        integerSpinnerModel.setMax(7);
        integerSpinnerModel .setMin(1);
        this.day.setSpinnerModel(integerSpinnerModel);

        integerSpinnerModel = new IntegerSpinnerModel();
        integerSpinnerModel.setIncrement(1);
        integerSpinnerModel.setMax(5);
        integerSpinnerModel.setMin(1);
        this.section.setSpinnerModel(integerSpinnerModel);

        this.day.setValue(day);
        this.section.setValue(section);
        this.action = action;
        dayBox.getChildren().addAll(dayL, this.day);
        sectionBox.getChildren().addAll(sectionL, this.section);
        dayBox.setAlignment(Pos.CENTER);
        sectionBox.setAlignment(Pos.CENTER);

        getChildren().addAll(action, dayBox, sectionBox);
        setAlignment(Pos.TOP_CENTER);
        setSpacing(40);
    }

    public Map toMap(){
        Map m = new HashMap<>();
        m.put("day", this.day.getValue());
        m.put("section", this.section.getValue());
        return m;
    }

    public int getDay(){
        return this.day.getValue();
    }

    public void setDay(int day){
        this.day.setValue(day);
    }

    public int getSection(){
        return this.section.getValue();
    }

    public void setSection(int section){
        this.section.setValue(section);
    }

    public MFXButton getAction() {
        return action;
    }

    public void setAction(MFXButton action) {
        this.action = action;
    }

    public Integer getCourseTimeId() {
        return courseTimeId;
    }

    public void setCourseTimeId(Integer courseTimeId) {
        this.courseTimeId = courseTimeId;
    }

    @Override
    public boolean equals(Object o){
        if(!(o instanceof TimeSelector)){
            return false;
        }
        if(o == this){
            return true;
        }
        if(((TimeSelector) o).getDay() == this.getDay() && ((TimeSelector) o).getSection() == this.getSection()){
            return true;
        }
        return false;
    }
}
