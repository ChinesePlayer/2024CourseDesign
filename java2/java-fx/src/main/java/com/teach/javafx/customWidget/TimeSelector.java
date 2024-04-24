package com.teach.javafx.customWidget;

import io.github.palexdev.materialfx.controls.MFXSpinner;
import javafx.scene.layout.VBox;

import java.util.HashMap;
import java.util.Map;

//用于管理员编辑课程时选择课程的上课时间
public class TimeSelector extends VBox {
    private MFXSpinner<Integer> day;
    private MFXSpinner<Integer> section;

    public TimeSelector(){

    }

    public TimeSelector(int day, int section){
        this.day.setValue(day);
        this.section.setValue(section);
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
}
