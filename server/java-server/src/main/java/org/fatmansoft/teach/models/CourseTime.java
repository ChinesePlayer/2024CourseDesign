package org.fatmansoft.teach.models;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.Size;

@Getter
@Setter
@Entity
@Table(name = "course_time")
//课程的上课时间，包括了星期和节次
public class CourseTime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer courseTimeId;

    @ManyToOne
    @JoinColumn(name = "course_id")
    private Course course;

    //上课星期
    @Size(min = 1, max = 7)
    private Integer day;

    //上课节次
    @Size(min = 1, max = 5)
    private Integer section;
}
