package org.fatmansoft.teach.models;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "honor")
public class Honor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer honorId;

    //荣誉类型
    @ManyToOne
    @JoinColumn(name = "honor_type_id")
    private HonorType honorType;

    //荣誉内容
    @Column(length = 20)
    private String honorContent;

    @ManyToOne
    @JoinColumn(name = "student_id")
    private Student student;
}
