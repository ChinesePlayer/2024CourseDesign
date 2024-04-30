package org.fatmansoft.teach.models;

import java.util.Map;
import java.util.Objects;

public class CourseLocation {
    private String value;
    private Integer id;

    public CourseLocation(){

    }

    public CourseLocation(Map m){
        this.value = (String) m.get("value");
        this.id = Integer.parseInt((String) m.get("locationId"));
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Override
    public String toString(){
        if(isEmptyLocation()){
            return "无";
        }
        return value;
    }

    @Override
    public boolean equals(Object o){
        if(!(o instanceof CourseLocation)){
            return false;
        }
        if(o == this){
            return true;
        }
        if(Objects.equals(((CourseLocation) o).getId(), this.id)){
            return true;
        }
        return false;
    }

    //判断当前地点是否为空地点
    //id为null或小于等于0是则为空地点
    public boolean isEmptyLocation(){
        return id == null || id <= 0;
    }
}
