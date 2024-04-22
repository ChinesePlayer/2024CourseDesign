package org.fatmansoft.teach.models;

import com.teach.javafx.request.HttpRequestUtil;
import io.github.palexdev.materialfx.controls.MFXButton;
import org.fatmansoft.teach.payload.request.DataRequest;
import org.fatmansoft.teach.payload.response.DataResponse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Course {
    private String name;
    private Integer courseId;
    private Course preCourse;
    private String num;
    private String credit;
    private String coursePath;
    private String teacher;
    private List<CourseTime> courseTimes;
    //对课程可执行的操作
    private MFXButton action;
    //是否已选该课程
    private Boolean isChosen;

    public Course(){

    }

    public Course(String name, Integer courseId, String num, String credit){
        this.name =name;
        this.courseId = courseId;
        this.num = num;
        this.credit = credit;
    }

    //从Map构建课程对象的代码
    public Course(Map m){
        System.out.println("当前正在转化的Map数据: " + m);
        String str = String.valueOf(m.get("courseId"));
        int pos = str.indexOf(".");
        Integer id = Integer.parseInt(String.valueOf(m.get("courseId")));

        this.name = (String)m.get("name");
        this.num = (String)m.get("num");
        this.courseId = id;
        this.credit = String.valueOf(m.get("credit"));
        this.teacher = (String) m.get("teacher");
        this.isChosen = (Boolean) m.get("isChosen");
        this.coursePath = (String) m.get("coursePath");
        this.courseTimes = new ArrayList<>();
        List<Map> timeMaps = (ArrayList<Map>) m.get("times");
        System.out.println("从Map构建Course: " + m.get("times"));
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

    public String getTeacher() {
        return teacher;
    }

    public void setTeacher(String teacher) {
        this.teacher = teacher;
    }
}
