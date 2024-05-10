package org.fatmansoft.teach.models;

import javafx.scene.control.Button;
import org.fatmansoft.teach.util.CommonMethod;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Homework {
    private Integer courseId;
    private Integer homeworkId;
    private String title;
    private String content;
    private LocalDateTime start;
    private LocalDateTime end;

    private List<Button> actions;

    public Homework(){

    }

    public Homework(Map m){
        this.courseId = CommonMethod.getInteger(m, "courseId");
        this.homeworkId = CommonMethod.getInteger(m, "homeworkId");
        this.title = CommonMethod.getString(m, "title");
        this.content = CommonMethod.getString(m, "content");
        this.start = LocalDateTime.parse(CommonMethod.getString(m, "start"));
        this.end = LocalDateTime.parse(CommonMethod.getString(m, "end"));
    }

    public Map toMap(){
        Map res = new HashMap<>();
        res.put("title", this.title);
        res.put("start", CommonMethod.getDateString(this.start, null));
        res.put("end", CommonMethod.getDateString(this.end, null));
        return res;
    }

    public List toList(){
        List res = new ArrayList<>();
        res.add(this.title);
        res.add(CommonMethod.getDateString(this.start, null));
        res.add(CommonMethod.getDateString(this.end, null));
        return res;
    }

    public Integer getCourseId() {
        return courseId;
    }

    public void setCourseId(Integer courseId) {
        this.courseId = courseId;
    }

    public Integer getHomeworkId() {
        return homeworkId;
    }

    public void setHomeworkId(Integer homeworkId) {
        this.homeworkId = homeworkId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public List<Button> getActions() {
        return actions;
    }

    public void setActions(List<Button> actions) {
        this.actions = actions;
    }
    public LocalDateTime getStart() {
        return start;
    }

    public void setStart(LocalDateTime start) {
        this.start = start;
    }

    public LocalDateTime getEnd() {
        return end;
    }

    public void setEnd(LocalDateTime end) {
        this.end = end;
    }
}