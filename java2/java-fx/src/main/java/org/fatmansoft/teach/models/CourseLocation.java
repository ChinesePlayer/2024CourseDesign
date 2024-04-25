package org.fatmansoft.teach.models;

import java.util.Map;

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
}
