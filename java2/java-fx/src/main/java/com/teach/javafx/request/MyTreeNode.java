package com.teach.javafx.request;

import org.fatmansoft.teach.util.CommonMethod;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * MyTreeNode  树节点类
 * Integer id 节点ID 菜单或数据字典的主键ID
 * String value; 节点值
 * String label  节点标题
 * Integer pid 父节点
 * List<MyTreeNode> childList 子节点对象列表
 */
public class MyTreeNode {
    private Integer id;
    private String name;
    private String label;
    private Integer pid;
    private Integer isLeaf;
    private String title;
    private String userTypeIds;
    private String parentTitle;
    private boolean isMenu;
    private SvgImage svg;
    private List<MyTreeNode> children;
    public MyTreeNode(){
        this.children= new ArrayList<MyTreeNode>();
    }
    public MyTreeNode(Integer id, String name, String label, Integer isLeaf, SvgImage svg){
        this.id  = id;
        this.name = name;
        this.label = label;
        this.isLeaf = isLeaf;
        this.children= new ArrayList<MyTreeNode>();
        this.svg = svg;
    }

    public MyTreeNode(Map map){
        this.id  = CommonMethod.getInteger(map,"id");
        this.name = CommonMethod.getString(map,"value");
        this.label = CommonMethod.getString(map,"label");
        this.title = CommonMethod.getString(map,"title");
        this.isLeaf = CommonMethod.getInteger(map,"isLeaf");
        this.pid = CommonMethod.getInteger(map,"pid");
        this.userTypeIds = CommonMethod.getString(map,"userTypeIds");
        this.parentTitle = CommonMethod.getString(map,"parentTitle");
        if(map.get("svgPath") != null){
            this.svg = new SvgImage((String) map.get("svgPath"));
        }
        else{
            this.svg = null;
        }
        this.children= new ArrayList<MyTreeNode>();
        List children = CommonMethod.getList(map,"children");
        if(children != null && children.size() > 0) {
            for(int i = 0; i < children.size();i++)
                this.children.add(new MyTreeNode((Map)children.get(i)));
        }
    }


    //将当前节点拷贝到另一个节点中
    public MyTreeNode(MyTreeNode newNode){
        this.isMenu = newNode.getIsMenu();
        this.name = newNode.getName();
        this.title = newNode.getTitle();
        this.pid = newNode.getPid();
        this.id = newNode.getId();
        this.label = newNode.getLabel();
        this.userTypeIds = newNode.getUserTypeIds();
        this.children = newNode.getChildren();
        this.isLeaf = newNode.getIsLeaf();
        this.parentTitle = newNode.getParentTitle();
    }

    public String toString(){
        return label;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public boolean getIsMenu(){return this.isMenu;}

    public void setIsMenu(boolean isMenu) {
        this.isMenu = isMenu;
    }

    public Integer getPid() {
        return pid;
    }

    public void setPid(Integer pid) {
        this.pid = pid;
    }

    public Integer getIsLeaf() {
        return isLeaf;
    }

    public void setIsLeaf(Integer isLeaf) {
        this.isLeaf = isLeaf;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUserTypeIds() {
        return userTypeIds;
    }

    public void setUserTypeIds(String userTypeIds) {
        this.userTypeIds = userTypeIds;
    }

    public String getParentTitle() {
        return parentTitle;
    }

    public void setParentTitle(String parentTitle) {
        this.parentTitle = parentTitle;
    }

    public List<MyTreeNode> getChildren() {
        return children;
    }

    public void setChildren(List<MyTreeNode> children) {
        this.children = children;
    }


    public SvgImage getSvg() {
        return svg;
    }

    public void setSvg(SvgImage svg) {
        this.svg = svg;
    }
}
