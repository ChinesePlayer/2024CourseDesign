package org.fatmansoft.teach.models;

import java.net.Inet4Address;
import java.util.Map;
import java.util.Objects;

public class Teacher {
    private Integer teacherId;
    private Person person;
    private String name;
    private String title;
    private String degree;

    public Teacher(){

    }

    public Teacher(Map m){
        this.teacherId = Integer.parseInt((String) m.get("teacherId"));
        Map personMap = (Map) m.get("person");
        this.name = (String) personMap.get("name");
        this.title = (String) m.get("title");
        this.degree = (String) m.get("degree");
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(Integer teacherId) {
        this.teacherId = teacherId;
    }

    public String getDegree() {
        return degree;
    }

    public void setDegree(String degree) {
        this.degree = degree;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString(){
        if(teacherId == null || teacherId == -1){
            return "无";
        }
        return teacherId + " - " + name;
    }

    @Override
    public boolean equals(Object o){
        if(!(o instanceof Teacher)){
            return false;
        }
        if(o == this){
            return true;
        }
        if(Objects.equals(((Teacher) o).getTeacherId(), this.teacherId)){
            return true;
        }
        return false;
    }
}
