package org.fatmansoft.teach.models;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Attendance 考勤实体类  保存成绩的的基本信息，
 * Integer Id 考勤表 attendance 主键 Id
 * Student student 关联学生 student_id 关联学生的主键 student_id
 * Course course 关联课程 course_id 关联课程的主键 course_id
 * LocalDataTime date 考勤日期
 * String attendance  出勤情况
 */
@Entity
@Getter
@Setter
@Table(	name = "attendance",
        uniqueConstraints = {
        })
public class Attendance implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer Id;

    @ManyToOne
    @JoinColumn(name = "course_id")
    private Course course;
    //课程名，课序号
    @ManyToOne
    @JoinColumn(name = "student_id")
    private Student student;

    private LocalDateTime date;//日期

    private String status;//出勤情况:“出勤”/"缺勤“

}