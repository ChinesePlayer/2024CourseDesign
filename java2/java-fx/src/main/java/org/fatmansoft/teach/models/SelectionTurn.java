package org.fatmansoft.teach.models;

import java.time.LocalDateTime;
import java.util.Map;

//选课轮次类
public class SelectionTurn {
    private LocalDateTime start;
    private LocalDateTime end;
    private String name;
    private Integer id;
    private boolean isValid;

    public SelectionTurn(){

    }

    //从Map构建类
    public SelectionTurn(Map m){
        this.start = LocalDateTime.parse((String) m.get("start"));
        this.end = LocalDateTime.parse((String) m.get("end"));
        //id的字符串形式
        String stringId = String.valueOf(m.get("id"));
        //小数点的位置
        Integer pointPos = stringId.indexOf(".");
        Integer id = Integer.parseInt(stringId.substring(0,pointPos));
        this.id = id;
        this.isValid = (Boolean) m.get("isValid");
        this.name = (String) m.get("name");
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public boolean isValid() {
        return isValid;
    }

    public void setValid(boolean valid) {
        isValid = valid;
    }
}
