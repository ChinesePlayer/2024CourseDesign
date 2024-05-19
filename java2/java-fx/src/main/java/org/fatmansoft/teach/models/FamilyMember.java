package org.fatmansoft.teach.models;

import org.fatmansoft.teach.util.CommonMethod;

import java.time.LocalDate;
import java.util.Map;

public class FamilyMember {
    private Integer memberId;
    private Integer studentId;
    private String relation;
    private String name;
    private String gender;
    private LocalDate birthday;
    private String unit;

    public FamilyMember(){

    }

    public FamilyMember(Map m){
        this.memberId = CommonMethod.getInteger(m,"memberId");
        this.studentId = CommonMethod.getInteger(m,"studentId");
        this.relation = CommonMethod.getString(m,"relation");
        this.name = CommonMethod.getString(m,"name");
        this.gender = CommonMethod.getString(m,"gender");
        this.birthday = CommonMethod.getLocalDateFromString(CommonMethod.getString(m,"birthday"),CommonMethod.DATE_FORMAT);
        this.unit = CommonMethod.getString(m,"unit");
    }

    public Integer getMemberId() {
        return memberId;
    }

    public void setMemberId(Integer memberId) {
        this.memberId = memberId;
    }

    public Integer getStudentId() {
        return studentId;
    }

    public void setStudentId(Integer studentId) {
        this.studentId = studentId;
    }

    public String getRelation() {
        return relation;
    }

    public void setRelation(String relation) {
        this.relation = relation;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public LocalDate getBirthday() {
        return birthday;
    }

    public void setBirthday(LocalDate birthday) {
        this.birthday = birthday;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }
}
