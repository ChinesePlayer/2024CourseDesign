package org.fatmansoft.teach.models;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "course_selection_turn")
//选课轮次
public class CourseSelectionTurn {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer courseSelectionId;

    @ManyToMany(mappedBy = "turns")
    private List<Course> courses;

    //选课开始时间
    private LocalDateTime start;

    //选课截止时间
    private LocalDateTime end;

    //选课轮次的名称
    @Column(length = 50)
    private String name;
}
