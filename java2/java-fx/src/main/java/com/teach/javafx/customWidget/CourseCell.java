package com.teach.javafx.customWidget;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import org.fatmansoft.teach.models.Course;


public class CourseCell extends StackPane {
    //课程名字
    private String courseName = "";
    //老师名字
    private String teacherName = "";
    //上课地点
    private String loc = "";
    //默认Label组件，用于显示文本
    private Label textLabel;
    //背景默认颜色为白色
    private Color backgroundColor = Color.WHITE;
    //默认边框样式
    private BorderStroke borderStroke = new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT);
    private Border border = new Border(borderStroke);
    private double height = 80.0;
    private double width = 80.0;

    public CourseCell(){
        super();
        this.textLabel = new Label();
        textLabel.getStylesheets().add("-fx-font-size:1px;");
        Tooltip tooltip = new Tooltip(textLabel.getText());
        tooltip.setShowDelay(Duration.millis(5));
        Tooltip.install(textLabel, tooltip);
        textLabel.setWrapText(true);
        getChildren().add(textLabel);
        BackgroundFill bFill = new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY);
        Background background = new Background(bFill);
        setBackground(background);
        setBorder(border);
        setPrefWidth(width);
        setPrefHeight(height);
    }

    public CourseCell(Label label){
        super();
        this.textLabel = label;
        textLabel.getStylesheets().add("-fx-font-size:1px;");
        Tooltip tooltip = new Tooltip(textLabel.getText());
        tooltip.setShowDelay(Duration.millis(5));
        Tooltip.install(textLabel, tooltip);
        textLabel.setWrapText(true);
        getChildren().add(textLabel);
        BackgroundFill bFill = new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY);
        Background background = new Background(bFill);
        setBackground(background);
        setBorder(border);
        setPrefWidth(width);
        setPrefHeight(height);
    }

    public CourseCell(Course course, Color color){
        super();
        this.backgroundColor = color;
        this.courseName = course.getName();
        this.teacherName = course.getTeacher().getPersonName();
        this.loc = course.getLocation().getValue();
        String labelStr = "";
        if(courseName == null || courseName.isEmpty()){
            labelStr += "无法获取课程名 -";
        }
        else {
            labelStr += courseName + " - ";
        }
        if(loc == null || loc.isEmpty()){
            labelStr += "暂未公布 - ";
        }
        else{
            labelStr += loc + " - ";
        }
        if(teacherName == null || teacherName.isEmpty()){
            labelStr += "暂未公布";
        }
        else{
            labelStr += teacherName;
        }
        this.textLabel = new Label(labelStr);
        textLabel.getStylesheets().add("-fx-font-size:1px;");
        Tooltip tooltip = new Tooltip(textLabel.getText());
        tooltip.setShowDelay(Duration.millis(5));
        Tooltip.install(textLabel, tooltip);
        textLabel.setWrapText(true);
        getChildren().add(textLabel);
        setAlignment(Pos.CENTER);
        BackgroundFill bFill = new BackgroundFill(color, CornerRadii.EMPTY, Insets.EMPTY);
        Background background = new Background(bFill);
        setBackground(background);
        setBorder(border);
        setPrefWidth(width);
        setPrefHeight(height);
    }

    public CourseCell(Course course){

    }

    public String getTeacherName() {
        return teacherName;
    }

    public void setTeacherName(String teacherName) {
        this.teacherName = teacherName;
    }

    public String getLoc() {
        return loc;
    }

    public void setLoc(String loc) {
        this.loc = loc;
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
    public Label getTextLabel(){
        return this.textLabel;
    }

    public void setTextLabel(Label label){
        this.textLabel = label;
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
                ((CourseCell) that).getLoc().equals(this.loc)){
            return true;
        }
        return false;
    }
}
