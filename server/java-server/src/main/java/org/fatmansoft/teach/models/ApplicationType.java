package org.fatmansoft.teach.models;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
@Table(name = "application_type")
public class ApplicationType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer applicationTypeId;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private EApplicationType typeName;
}
