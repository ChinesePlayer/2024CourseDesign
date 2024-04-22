package org.fatmansoft.teach.models;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Course 课程表实体类  保存课程的的基本信息信息，
 * Integer courseId 人员表 course 主键 course_id
 * String num 课程编号
 * String name 课程名称
 * Integer credit 学分
 * Course preCourse 前序课程 pre_course_id 关联前序课程的主键 course_id
 */
@Getter
@Setter
@Entity
@Table(	name = "course",
        uniqueConstraints = {
        })
public class Course implements Serializable {
    @Setter
    @Getter
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer courseId;
    @Setter
    @Getter
    @NotBlank
    @Size(max = 20)
    private String num;

    @Setter
    @Getter
    @Size(max = 50)
    private String name;
    @Setter
    @Getter
    private Integer credit;

    //上课星期
    @Size(min = 1, max = 7)
    private Integer day;

    //上课节次
    @Size(min = 1, max = 5)
    private Integer session;

    //上课地点
    @Column(length = 30)
    private String location;

    @Getter
    @Setter
    @ManyToOne
    @JoinColumn(name="pre_course_id")
    private Course preCourse;
    @Setter
    @Getter
    @Size(max = 12)
    private String coursePath;

    @Setter
    @Getter
    @ManyToMany
    @JoinTable(name = "course_student",
    joinColumns = @JoinColumn(name = "course_id"),
    inverseJoinColumns = @JoinColumn(name = "student_id"))
    private List<Student> students = new ArrayList<>();

    @ManyToMany
    @JoinTable(name = "course_turn",
    joinColumns = @JoinColumn(name = "course_id"),
    inverseJoinColumns = @JoinColumn(name = "turn_id"))
    private List<CourseSelectionTurn> turns;

    @OneToMany(mappedBy = "course")
    private List<CourseTime> courseTimes;

    @ManyToOne
    @JoinColumn(name = "teacher_id")
    private Teacher teacher;

    @Override
    public boolean equals(Object o){
        if(o == null){
            return false;
        }
        if(o.getClass() != this.getClass()){
            return false;
        }
        if(o == this){
            return true;
        }
        Course that = (Course)o;
        if(Objects.equals(that.getCourseId(), this.getCourseId())){
            return true;
        }
        return false;
    }

}
