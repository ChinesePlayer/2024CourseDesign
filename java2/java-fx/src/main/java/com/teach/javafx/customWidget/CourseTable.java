package com.teach.javafx.customWidget;

import javafx.scene.layout.GridPane;
import org.fatmansoft.teach.models.Course;

import java.util.ArrayList;
import java.util.List;

public class CourseTable extends GridPane {
    List<List<CourseCell>> courseCell = new ArrayList<>();

    public CourseTable(){

    }

    //课程需要有上课星期，节次，以此来定位
    public CourseTable(List<Course> course){

    }

//    public void setGrid(List<>){
//
//    }
}
