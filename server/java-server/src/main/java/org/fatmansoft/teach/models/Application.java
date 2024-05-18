package org.fatmansoft.teach.models;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;


//假条实体类
@Entity
@Getter
@Setter
@Table(name = "application")
public class Application {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer applicationId;

    @ManyToOne
    @JoinColumn(name = "student_id")
    private Student student;

    @ManyToOne
    @JoinColumn(name = "application_type_id")
    private ApplicationType applicationType;

    //申请理由
    private String reason;

    //离校时间
    private LocalDate leaveDate;
    //返校时间
    private LocalDate returnDate;

    //假条状态：0: 审批中  1: 已通过  2: 未通过  3: 已销假
    private Integer status;
}
