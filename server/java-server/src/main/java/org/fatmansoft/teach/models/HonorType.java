package org.fatmansoft.teach.models;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Entity
@Table(name = "honor_type", uniqueConstraints = {})
public class HonorType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer honorTypeId;

    @NotBlank
    @Size(max = 50)
    private String honorName;
}
