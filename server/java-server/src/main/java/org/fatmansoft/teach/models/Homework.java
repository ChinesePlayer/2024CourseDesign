package org.fatmansoft.teach.models;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Entity
@Setter
@Getter
@Table(name = "homework")
public class Homework {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer homeworkId;

    @ManyToOne
    @JoinColumn(name = "course_id")
    private Course course;

    @Size(max = 100)
    @JoinColumn(name = "title")
    private String title;

    @Size(max = 1000)
    @JoinColumn(name = "content")
    private String content;

    //作业开放时间
    private LocalDateTime start;
    //作业截止时间
    private LocalDateTime end;
}
