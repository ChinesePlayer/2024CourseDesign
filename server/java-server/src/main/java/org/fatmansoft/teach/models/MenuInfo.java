package org.fatmansoft.teach.models;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.Size;
/**
 * MenuInfo 菜单表实体类  保存菜单的的基本信息信息， 数据库表名 menu
 * Integer id 菜单表  menu 主键 id
 * Integer pid  父节点ID  所属于的父菜单的id
 * String name 菜单名  记录FXML的文件名，用于导航加载FX面板
 * String name 菜单标题 菜单显示的标题
 */
@Entity
@Table(	name = "menu",
        uniqueConstraints = {
        })
@Getter
@Setter
public class MenuInfo {
    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String userTypeIds;
    private Integer pid;

    @Size(max = 100)
    private String name;


    @Size(max = 100)
    private String title;

    @OneToOne
    @JoinColumn(name = "svg_id")
    private SvgImage svgImage;
}