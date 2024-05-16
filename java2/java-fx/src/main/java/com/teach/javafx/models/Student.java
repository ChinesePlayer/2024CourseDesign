package com.teach.javafx.models;

import org.fatmansoft.teach.models.Person;
import org.fatmansoft.teach.util.CommonMethod;

import java.util.Map;
import java.util.Objects;

public class Student {
    private Integer studentId;
    private Integer personId;
    private String num;
    private String name;
    private String type;
    private String dept;
    private String card;
    private String gender;
    private String genderName;
    private String birthday;
    private String email;
    private String phone;
    private String address;
    private String introduce;
    private String major;
    private String className;
    public Student(){

    }
    public Student(String num, String name){
        this.num = num;
        this.name = name;
    }

    public Student(Map m){
        this.studentId = CommonMethod.getInteger(m,"studentId");
        this.personId = CommonMethod.getInteger(m,"personId");
        this.num = CommonMethod.getString(m, "studentNum");
        this.name = CommonMethod.getString(m,"studentName");
        this.className = CommonMethod.getString(m, "className");
        this.num = CommonMethod.getString(m, "num");
        this.name = CommonMethod.getString(m, "name");
    }

    @Override
    public String toString(){
        if(isEmptyStudent()){
            return "无";
        }
        else {
            return num + " - " + name;
        }
    }

    //根据学生ID来判断两个学生对象是否相等
    @Override
    public boolean equals(Object o){
        if(!(o instanceof Student)){
            return false;
        }
        if(o == this){
            return true;
        }
        return Objects.equals(((Student) o).studentId, this.studentId);
    }

    //判断当前学生是否为空学生
    //若id小于等于0或为null，则为空学生
    public boolean isEmptyStudent(){
        return studentId == null || studentId <= 0;
    }

    public Integer getStudentId() {
        return studentId;
    }

    public void setStudentId(Integer studentId) {
        this.studentId = studentId;
    }


    public String getMajor() {
        return major;
    }

    public void setMajor(String major) {
        this.major = major;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public Integer getPersonId() {
        return personId;
    }

    public void setPersonId(Integer personId) {
        this.personId = personId;
    }

    public String getNum() {
        return num;
    }

    public void setNum(String num) {
        this.num = num;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDept() {
        return dept;
    }

    public void setDept(String dept) {
        this.dept = dept;
    }

    public String getCard() {
        return card;
    }

    public void setCard(String card) {
        this.card = card;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getGenderName() {
        return genderName;
    }

    public void setGenderName(String genderName) {
        this.genderName = genderName;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getIntroduce() {
        return introduce;
    }

    public void setIntroduce(String introduce) {
        this.introduce = introduce;
    }

}
