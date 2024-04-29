package org.fatmansoft.teach.models;

import java.util.Map;

public class Score {
    private Integer scoreId;
    private Integer studentId;
    private Integer courseId;
    private String studentNum;
    private String studentName;
    private String courseNum;
    private String courseName;
    private Double credit;
    private Double mark;
    private Integer rank;
    private Integer status;
    private Double gpa;

    public Score(){

    }

    public Score(Map m){
        this.scoreId = Integer.parseInt((String) m.get("scoreId"));
        this.studentId = Integer.parseInt((String) m.get("studentId"));
        this.courseId = Integer.parseInt((String) m.get("courseId"));
        this.credit = Double.parseDouble((String) m.get("credit"));
        if(m.get("mark") != null){
            this.mark = Double.parseDouble((String) m.get("mark"));
        }
        else {
            this.mark = null;
        }
        this.status = Integer.parseInt((String) m.get("status"));
        this.courseName = (String) m.get("courseName");
        this.courseNum = (String) m.get("courseNum");
        this.studentName = (String) m.get("studentName");
        this.studentNum = (String) m.get("studentNum");
        if(m.get("rank") != null){
            this.rank = Integer.parseInt((String) m.get("rank"));
        }
        else {
            this.rank = null;
        }
        this.gpa = (Double) m.get("gpa");
    }

    public Integer getScoreId() {
        return scoreId;
    }

    public void setScoreId(Integer scoreId) {
        this.scoreId = scoreId;
    }

    public Integer getStudentId() {
        return studentId;
    }

    public void setStudentId(Integer studentId) {
        this.studentId = studentId;
    }

    public Integer getCourseId() {
        return courseId;
    }

    public void setCourseId(Integer courseId) {
        this.courseId = courseId;
    }

    public String getStudentNum() {
        return studentNum;
    }

    public void setStudentNum(String studentNum) {
        this.studentNum = studentNum;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getCourseNum() {
        return courseNum;
    }

    public void setCourseNum(String courseNum) {
        this.courseNum = courseNum;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public Double getCredit() {
        return credit;
    }

    public void setCredit(Double credit) {
        this.credit = credit;
    }

    public Double getMark() {
        return mark;
    }

    public void setMark(Double mark) {
        this.mark = mark;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getRank() {
        return rank;
    }

    public void setRank(Integer rank) {
        this.rank = rank;
    }

    public Double getGpa() {
        return gpa;
    }

    public void setGpa(Double gpa) {
        this.gpa = gpa;
    }
}