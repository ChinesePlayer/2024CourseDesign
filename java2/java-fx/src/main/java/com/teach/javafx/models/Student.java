package com.teach.javafx.models;

import org.fatmansoft.teach.util.CommonMethod;

import java.time.LocalDate;
import java.util.Map;
import java.util.Objects;

public class Student {
    private Integer studentId;
    private Integer personId;
    private String studentNum;
    private String studentName;
    private String type;
    private String dept;
    private String card;
    private Integer gender;
    private String genderName;
    private LocalDate birthday;
    private String email;
    private String phone;
    private String address;
    private String introduce;
    private String major;
    private String className;
    public Student(){

    }
    public Student(String studentNum, String studentName){
        this.studentNum = studentNum;
        this.studentName = studentName;
    }

    public Student(Map m){
        this.studentId = CommonMethod.getInteger(m,"studentId");
        this.personId = CommonMethod.getInteger(m,"personId");
        this.studentNum = CommonMethod.getString(m, "studentNum");
        this.studentName = CommonMethod.getString(m,"studentName");
        this.className = CommonMethod.getString(m, "className");
        this.gender = CommonMethod.getInteger(m, "gender");
        this.genderName = CommonMethod.getString(m, "genderName");
        this.birthday = CommonMethod.getLocalDateFromString(CommonMethod.getString(m,"birthday"), CommonMethod.DATE_FORMAT);
        this.email = CommonMethod.getString(m, "email");
        this.phone = CommonMethod.getString(m, "phone");
        this.address = CommonMethod.getString(m, "address");
        this.introduce = CommonMethod.getString(m, "introduce");
        this.major = CommonMethod.getString(m, "major");
        this.type = CommonMethod.getString(m, "type");
        this.dept = CommonMethod.getString(m, "dept");
        this.card = CommonMethod.getString(m, "card");
    }

    @Override
    public String toString(){
        if(isEmptyStudent()){
            return "无";
        }
        else {
            return studentNum + " - " + studentName;
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


    public String getGenderName() {
        return genderName;
    }

    public void setGenderName(String genderName) {
        this.genderName = genderName;
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

    public Integer getGender() {
        return gender;
    }

    public void setGender(Integer gender) {
        this.gender = gender;
    }

    public LocalDate getBirthday() {
        return birthday;
    }

    public void setBirthday(LocalDate birthday) {
        this.birthday = birthday;
    }
}
