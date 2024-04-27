package org.fatmansoft.teach.models;

import com.teach.javafx.request.HttpRequestUtil;
import io.github.palexdev.materialfx.controls.MFXButton;
import org.fatmansoft.teach.payload.request.DataRequest;
import org.fatmansoft.teach.payload.response.DataResponse;

import java.util.*;

public class Course {
    private String name;
    private Integer courseId;
    private Course preCourse;
    private String num;
    private double credit;
    private String coursePath;
    private Teacher teacher;
    private CourseLocation location;
    private List<CourseTime> courseTimes = new ArrayList<>();
    //对课程可执行的操作
    private MFXButton action;
    //是否已选该课程
    private Boolean isChosen;

    public Course(){

    }

    public Course(String name, Integer courseId, String num, double credit){
        this.name =name;
        this.courseId = courseId;
        this.num = num;
        this.credit = credit;
    }

    //从Map构建课程对象的代码
    public Course(Map m){
        String str = String.valueOf(m.get("courseId"));
        int pos = str.indexOf(".");
        Integer id = Integer.parseInt(String.valueOf(m.get("courseId")));

        this.name = (String)m.get("name");
        this.num = (String)m.get("num");
        this.courseId = id;
        this.credit = (double)m.get("credit");

        Map teacherMap = (Map)m.get("teacher");
        if(teacherMap != null){
            Teacher t = new Teacher();
            t.setName((String) teacherMap.get("name"));
            t.setTeacherId(Integer.parseInt((String) teacherMap.get("id")));
            this.teacher = t;
        }
        else {
            this.teacher = null;
        }

        this.isChosen = (Boolean) m.get("isChosen");
        this.coursePath = (String) m.get("coursePath");

        Map locationMap = (Map) m.get("location");
        if(locationMap != null){
            this.location = new CourseLocation(locationMap);
        }
        else {
            this.location = null;
        }

        this.courseTimes = new ArrayList<>();
        List<Map> timeMaps = (ArrayList<Map>) m.get("times");
        if(timeMaps != null){
            for(Map tm : timeMaps){
                this.courseTimes.add(new CourseTime(tm));
            }
        }
        if(m.get("preCourseId") != null){
            Integer preCourseId = Integer.parseInt((String) m.get("preCourseId"));
            DataRequest req = new DataRequest();
            req.add("courseId", preCourseId);
            DataResponse res = HttpRequestUtil.request("/api/course/getCourse",req);
            if(res != null && res.getCode() == 0){
                Map preMap = (Map) res.getData();
                preCourse = new Course(preMap);
            }
            else{
                System.out.println("在构建课程对象时请求前序课程失败");
            }
        }
    }

    //复制构造函数
    public Course(Course c){
        this.name = c.getName();
        this.num = c.getNum();
        this.coursePath = c.getCoursePath();
        this.courseId = c.getCourseId();
        this.isChosen = c.getChosen();
        this.credit = c.getCredit();
        this.action = c.getAction();
        this.courseTimes = c.getCourseTimes();
        this.preCourse = c.getPreCourse();
        this.teacher = c.getTeacher();
        this.location = c.getLocation();
    }

    @Override
    public String toString(){
        if(Objects.equals(courseId, -1)){
            return "无";
        }
        return num + " - " + name;
    }

    @Override
    public boolean equals(Object o){
        if(!(o instanceof Course)){
            return false;
        }
        if(o == this){
            return true;
        }
        if(Objects.equals(((Course) o).getCourseId(), this.courseId)){
            return true;
        }
        return false;
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

    public double getCredit() {
        return credit;
    }

    public void setCredit(double credit) {
        this.credit = credit;
    }

    public String getCoursePath() {
        return coursePath;
    }

    public void setCoursePath(String coursePath) {
        this.coursePath = coursePath;
    }

    public MFXButton getAction() {
        return action;
    }

    public void setAction(MFXButton action) {
        this.action = action;
    }

    public Boolean getChosen() {
        return isChosen;
    }

    public void setChosen(Boolean chosen) {
        isChosen = chosen;
    }

    public List<CourseTime> getCourseTimes() {
        return courseTimes;
    }

    public void setCourseTimes(List<CourseTime> courseTimes) {
        this.courseTimes = courseTimes;
    }

    public Teacher getTeacher() {
        return teacher;
    }

    public void setTeacher(Teacher teacher) {
        this.teacher = teacher;
    }

    public CourseLocation getLocation() {
        return location;
    }

    public void setLocation(CourseLocation location) {
        this.location = location;
    }

    //判断两个课程是否冲突
    public static boolean isConflict(Course c1, Course c2){
        if((c1.getCourseTimes() == null || c1.getCourseTimes().isEmpty()) || (c2.getCourseTimes() == null || c2.getCourseTimes().isEmpty())){
            return false;
        }
        for(CourseTime ct1 : c1.getCourseTimes()){
            if(c2.getCourseTimes().contains(ct1)){
                return true;
            }
        }
        return false;
    }
}
