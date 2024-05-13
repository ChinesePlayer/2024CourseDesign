package org.fatmansoft.teach.models;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * Score 成绩表实体类  保存成绩的的基本信息信息，
 * Integer scoreId 人员表 score 主键 score_id
 * Student student 关联学生 student_id 关联学生的主键 student_id
 * Course course 关联课程 course_id 关联课程的主键 course_id
 * Integer mark 成绩
 * Integer ranking 排名
 */

//注：特定学生的特定课程的Score实体对象可以有个多个，但是只会有三种状态：1.多个不及格的成绩  2.零个或多个不及格的成绩和一个修读中的成绩  3.一个已及格的成绩
@Getter
@Setter
@Entity
@Table(	name = "score",
        uniqueConstraints = {
        })
public class Score {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer scoreId;

    @ManyToOne
    @JoinColumn(name = "student_id")
    private Student student;

    @ManyToOne
    @JoinColumn(name = "course_id")
    private Course course;

    private Integer mark;
    private Integer rank;

    //完成状态，0：修读中，1：已及格，2：不及格
    private Integer status;

    //成绩录入时间
    //由后端管理，前端无修改权限，仅能读取
    private LocalDateTime dateTime;

    public Double calcGpa(){
        if(mark != null){
            if(mark < 60){
                return null;
            }
            return mark/10.0-5.0;
        }
        return null;
    }
}