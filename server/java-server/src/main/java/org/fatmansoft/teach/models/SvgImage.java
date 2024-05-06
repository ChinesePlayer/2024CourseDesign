package org.fatmansoft.teach.models;

import lombok.Getter;
import lombok.Setter;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;

import javax.persistence.*;

@Entity
@Getter
@Setter
@Table(name = "svg_image")
public class SvgImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer svgImageId;

    @Column(name = "svg_path")
    private String svgPath;

    @OneToOne
    @JoinColumn(name = "menu_id")
    private MenuInfo menu;
}
