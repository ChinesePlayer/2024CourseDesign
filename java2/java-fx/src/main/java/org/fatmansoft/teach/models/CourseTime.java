package org.fatmansoft.teach.models;

import java.util.Map;
import java.util.Objects;

public class CourseTime {
    private Integer courseTimeId;

    //上课星期
    private Integer day;

    //上课节次
    private Integer section;

    public CourseTime(){

    }

    //从Map创建一个CourseTime对象
    //m中必须包含: "id", "day", "section"键
    public CourseTime(Map m){
        this.courseTimeId = Integer.parseInt((String) m.get("id"));
        this.day = Integer.parseInt((String) m.get("day"));
        this.section = Integer.parseInt((String) m.get("section"));
    }

    //复制构造函数
    public CourseTime(CourseTime ct){
        this.courseTimeId = ct.getCourseTimeId();
        this.day = ct.getDay();
        this.section = ct.getSection();
    }

    public CourseTime(int day, int section){
        this.day = day;
        this.section = section;
    }

    //重载课程时间是否相等的方法
    //只要day和section相等，则相等
    @Override
    public boolean equals(Object that){
        if(!(that instanceof CourseTime)){
            return false;
        }
        if(that == this){
            return true;
        }
        return Objects.equals(((CourseTime) that).getDay(), this.day) && Objects.equals(((CourseTime) that).getSection(), this.section);
    }

    public Integer getCourseTimeId() {
        return courseTimeId;
    }

    public void setCourseTimeId(Integer courseTimeId) {
        this.courseTimeId = courseTimeId;
    }

    public Integer getDay() {
        return day;
    }

    public void setDay(Integer day) {
        this.day = day;
    }

    public Integer getSection() {
        return section;
    }

    public void setSection(Integer section) {
        this.section = section;
    }
}
