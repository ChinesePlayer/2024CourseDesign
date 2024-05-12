package org.fatmansoft.teach.models;

import org.fatmansoft.teach.util.CommonMethod;

import java.util.Map;

public class Answer {
    private Integer answerId;
    private Integer homeworkId;
    private String content;

    public Answer(){

    }

    //通过网络请求来构建对象
    public Answer(Map m){
        this.answerId = CommonMethod.getInteger(m, "answerId");
        this.homeworkId = CommonMethod.getInteger(m, "homeworkId");
        this.content = CommonMethod.getString(m, "content");
    }

    public Integer getAnswerId() {
        return answerId;
    }

    public void setAnswerId(Integer answerId) {
        this.answerId = answerId;
    }

    public Integer getHomeworkId() {
        return homeworkId;
    }

    public void setHomeworkId(Integer homeworkId) {
        this.homeworkId = homeworkId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
