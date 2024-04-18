package org.fatmansoft.teach.models;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.Size;

@Getter
@Setter
@Entity
@Table(name = "honor_type")
public class HonorType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer honorTypeId;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private EHonorType type;
}
