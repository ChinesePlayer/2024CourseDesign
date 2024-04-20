package org.fatmansoft.teach.models;

import java.util.Map;

public class Course {
    private String name;
    private Integer courseId;
    private Course preCourse;
    private String preCourseName;
    private String num;
    private String credit;
    private String coursePath;

    public Course(){

    }

    public Course(String name, Integer courseId, String num, String credit){
        this.name =name;
        this.courseId = courseId;
        this.num = num;
        this.credit = credit;
    }

    //从Map创建Course
    //这个Map中必须有的属性: name, courseId, num, credit
    public static Course fromMap(Map mapCourse){
        String name =(String) mapCourse.get("name");
        Integer courseId = null;
        String num = (String) mapCourse.get("num");
        String credit = "";
        if(mapCourse.get("courseId") instanceof Integer){
            courseId =(Integer) mapCourse.get("courseId");
        }
        else if(mapCourse.get("courseId") instanceof String){
            courseId = Integer.parseInt((String) mapCourse.get("courseId"));
        }

        if(mapCourse.get("credit") instanceof String){
            credit = (String) mapCourse.get("credit");
        }
        else if(mapCourse.get("credit") instanceof Integer){
            credit = String.valueOf((Integer) mapCourse.get("credit"));
        }
        return new Course(name, courseId, num, credit);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getCourseId() {
        return courseId;
    }

    public void setCourseId(Integer courseId) {
        this.courseId = courseId;
    }

    public Course getPreCourse() {
        return preCourse;
    }

    public void setPreCourse(Course preCourse) {
        this.preCourse = preCourse;
    }

    public String getNum() {
        return num;
    }

    public void setNum(String num) {
        this.num = num;
    }

    public String getCredit() {
        return credit;
    }

    public void setCredit(String credit) {
        this.credit = credit;
    }

    public String getCoursePath() {
        return coursePath;
    }

    public void setCoursePath(String coursePath) {
        this.coursePath = coursePath;
    }

    public String getPreCourseName() {
        return preCourseName;
    }

    public void setPreCourseName(String preCourseName) {
        this.preCourseName = preCourseName;
    }
}
