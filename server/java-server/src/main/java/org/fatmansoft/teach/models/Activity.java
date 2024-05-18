package org.fatmansoft.teach.models;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

//学生活动实体类
@Entity
@Getter
@Setter
@Table(name = "activity")
public class Activity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer activityId;

    //参与人员
    @ManyToMany
    @JoinTable(name = "activity_student",
    joinColumns = @JoinColumn(name = "activity_id"),
    inverseJoinColumns = @JoinColumn(name = "student_id"))
    private List<Student> students=new ArrayList<>();

    private String activityName;

    //负责人
    @ManyToOne
    @JoinColumn(name = "director_id")
    private Person director;

    //活动的审批状态:0: 审批中  1: 已通过  2: 未通过
    private Integer status;

    //活动可参与的总名额
    private Integer maxJoiner;

    //结束时间
    private LocalDate start;
    //开始时间
    private LocalDate end;

    //判断该活动的进行状态0: 未开始  1:进行中 2:已结束
    public Integer getProgressStatus(){
        LocalDate now = LocalDate.now();
        if(now.isBefore(start)){
            return 0;
        }
        else if(!now.isBefore(start) && !now.isAfter(end)){
            return 1;
        }
        else if(now.isAfter(end)){
            return 2;
        }
        else{
            //状态未知，返回一个未开始状态
            return 0;
        }
    }
}
