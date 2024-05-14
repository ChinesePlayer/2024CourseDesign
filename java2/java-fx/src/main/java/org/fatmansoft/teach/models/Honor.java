package org.fatmansoft.teach.models;

import org.fatmansoft.teach.util.CommonMethod;

import java.util.Map;

public class Honor {
    private Integer honorId;

    //荣誉类型
    private String honorType;

    //荣誉内容
    private String honorContent;

    public Honor(){

    }

    public Honor(Map m){
        this.honorId = CommonMethod.getInteger(m, "honorId");
        this.honorType = CommonMethod.getString(m, "honorType");
        this.honorContent = CommonMethod.getString(m, "honorContent");
    }

    public Integer getHonorId() {
        return honorId;
    }

    public void setHonorId(Integer honorId) {
        this.honorId = honorId;
    }

    public String getHonorType() {
        return honorType;
    }

    public void setHonorType(String honorType) {
        this.honorType = honorType;
    }

    public String getHonorContent() {
        return honorContent;
    }

    public void setHonorContent(String honorContent) {
        this.honorContent = honorContent;
    }
}