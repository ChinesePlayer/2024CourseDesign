package com.teach.javafx.customWidget;

import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXSpinner;
import io.github.palexdev.materialfx.controls.models.spinner.IntegerSpinnerModel;
import io.github.palexdev.materialfx.controls.models.spinner.SpinnerModel;
import javafx.geometry.Pos;
import javafx.scene.layout.VBox;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

//用于管理员编辑课程时选择课程的上课时间
public class TimeSelector extends VBox {
    private Integer courseTimeId;
    private MFXSpinner<Integer> day = new MFXSpinner<>();
    private MFXSpinner<Integer> section = new MFXSpinner<>();

    //用于外部对该对象执行一些操作
    private MFXButton action = null;

    public TimeSelector(){
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

        getChildren().addAll(this.day, this.section);
        setAlignment(Pos.TOP_CENTER);
    }

    public TimeSelector(MFXButton action){
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
        getChildren().addAll(this.action, this.day, this.section);
        setAlignment(Pos.TOP_CENTER);
    }

    public TimeSelector(int day, int section, MFXButton action){
        setStyle("-fx-padding: 20px");
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

        getChildren().addAll(this.action, this.day, this.section);
        setAlignment(Pos.TOP_CENTER);
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
}
