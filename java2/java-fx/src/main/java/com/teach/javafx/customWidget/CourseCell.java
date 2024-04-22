package com.teach.javafx.customWidget;

import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import org.fatmansoft.teach.models.Course;


public class CourseCell extends Pane {
    //课程名字
    private String courseName = "";
    //老师名字
    private String teacherName = "";
    //上课地点
    private String location = "";
    //默认Label组件，用于显示文本
    private Label textLabel = new Label(courseName + "-" + location + "-" + teacherName);
    //背景默认颜色为白色
    private Color backgroundColor = Color.WHITE;
    //默认边框样式
    private BorderStroke borderStroke = new BorderStroke(backgroundColor, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT);
    private Border border = new Border(borderStroke);
    public CourseCell(){
        setPrefHeight(50.0);
        setPrefWidth(50.0);
    }

    public CourseCell(Color backgroundColor){
        this.backgroundColor = backgroundColor;
        setPrefHeight(50.0);
        setPrefWidth(50.0);
    }

    public CourseCell(Color backgroundColor, Double preHeight, Double preWidth){
        this.backgroundColor = backgroundColor;
        setPrefHeight(preHeight);
        setPrefWidth(preWidth);
    }

    public CourseCell(Color backgroundColor, String courseName, String teacherName, String location){
        this.backgroundColor = backgroundColor;
        this.courseName = courseName;
        this.teacherName = teacherName;
        this.location = location;
        setPrefHeight(50.0);
        setPrefWidth(50.0);
    }

    public CourseCell(Course course, Color color){
        this.backgroundColor = color;
        this.courseName = course.getName();
        this.teacherName = course.getTeacher();
    }

    public CourseCell(Course course){

    }

    public String getTeacherName() {
        return teacherName;
    }

    public void setTeacherName(String teacherName) {
        this.teacherName = teacherName;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public void setBorderStroke(BorderStroke borderStroke){
        this.borderStroke = borderStroke;
    }

    public BorderStroke getBorderStroke(){
        return this.borderStroke;
    }

    @Override
    public void setPrefSize(double width, double height){
        super.setPrefSize(width, height);
        this.textLabel.setPrefSize(width, height);
    }

    @Override
    public boolean equals(Object that){
        if(that.getClass() != this.getClass()){
            return false;
        }
        if(that == this){
            return true;
        }
        if(((CourseCell) that).getCourseName().equals(this.courseName) &&
                ((CourseCell) that).getTeacherName().equals(this.teacherName) &&
                ((CourseCell) that).getLocation().equals(this.location)){
            return true;
        }
        return false;
    }
}
