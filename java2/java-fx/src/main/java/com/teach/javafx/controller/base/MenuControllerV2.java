package com.teach.javafx.controller.base;

import com.teach.javafx.MainApplication;
import com.teach.javafx.request.HttpRequestUtil;
import com.teach.javafx.request.MyTreeNode;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXCheckbox;
import io.github.palexdev.materialfx.controls.MFXTextField;
import io.github.palexdev.materialfx.enums.FloatMode;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.control.skin.TreeViewSkin;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import org.fatmansoft.teach.payload.request.DataRequest;

import javax.swing.plaf.IconUIResource;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

public class MenuControllerV2 {
    //菜单树
    @FXML
    private TreeView<MyTreeNode> menuTreeView;
    //用于编辑节点的视图
    @FXML
    private VBox editView;
    //处于编辑状态时的内容
    @FXML
    private GridPane editStatusView;

    //当前选中的树节点
    private TreeItem<MyTreeNode> currentItem;

    //未选择任何页面时编辑视图显示的内容
    private Label tipText1;
    private Label tipText2;
    private MFXButton addButton;

    //编辑视图进入编辑状态时的一系列组件
    @FXML
    private Label pid;
    @FXML
    private MFXTextField valueField;
    @FXML
    private MFXTextField titleField;
    @FXML
    private MFXButton saveButton;
    @FXML
    private Label bigTitle;
    private Label authority;
    private MFXCheckbox admin;
    private MFXCheckbox student;
    private MFXCheckbox teacher;

    //根节点(只能有一个)
    private TreeItem<MyTreeNode> root;
    //子节点
    private TreeItem<MyTreeNode> item;

    @FXML
    public void initialize(){
        MyTreeNode node = new MyTreeNode(null, "", "", 0);
        root = new TreeItem<>(node);
        //设置根节点
        menuTreeView.setRoot(root);
        //不显示根节点
        menuTreeView.setShowRoot(false);
        updateTreeView();
        //将编辑视图的背景设为白色
        editView.setStyle("-fx-background-color: #ffffff");
        clearEditView();
        initIdleStatusContent();
        initEditStatusContent();
        //将当前编辑视图设为待命状态
        setIdleStatus();
        //为当前菜单树视图添加点击回调
        menuTreeView.addEventFilter(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>(){
            @Override
            public void handle(MouseEvent event){
                //当检测到点击事件时，将被点击的节点传入到当前节点，以便于后续编辑
                TreeCell<MyTreeNode> node = null;
                //若当前电机的对象是TreeCell类型，则获取该TreeCell
                if(event.getPickResult().getIntersectedNode() instanceof TreeCell<?>){
                    node = (TreeCell<MyTreeNode>) event.getPickResult().getIntersectedNode();
                }
                currentItem = menuTreeView.getSelectionModel().getSelectedItem();
                //此处的机制：要让用户点击空白处时取消选中已经选中的项，并且将当前选中的currentItem设为空，并将编辑试图设为待命状态
                //首先要保证node不为空,否则后续调用node相关的方法时会报空指针异常，然后判断node中的文本是否存在，若为空，则可判断
                //用户点击了空白处
                //TODO: 在整个页面某处添加一个新增菜单的功能以当没有空白处时(选项太多空白处被挤没了),用户可以通过点击按钮来实现新增菜单
                if(node != null && (node.getText() == null || node.getText().isEmpty())){
                    currentItem = null;
                    setIdleStatus();
                    menuTreeView.getSelectionModel().clearSelection();
                    return;
                }
                //currentItem不为空时，说明用户点击了某个节点，这时候将编辑视图设为编辑状态
                if(currentItem != null)
                {
                    setEditStatus();
                }
                else {
                    setIdleStatus();
                }
            }
        });
    }

    private void initIdleStatusContent(){
        tipText1 = new Label("请选择左侧的菜单");
        tipText2 = new Label("或");
        addButton = new MFXButton("添加新菜单");
    }

    private void initEditStatusContent(){
        admin = new MFXCheckbox();
        student = new MFXCheckbox();
        teacher = new MFXCheckbox();

        authority = new Label("访问权限");

    }

    private void setIdleStatus(){
        clearEditView();

        editView.getChildren().addAll(tipText1, tipText2, addButton);
        editView.setAlignment(Pos.CENTER);
    }

    //当某个节点被选中时，根据被选节点的数据加载编辑页面
    private void setEditStatus(){
        //TODO: 在fxml布局文件中添加复选框
        clearEditView();

        if(currentItem.isLeaf()){
            pid.setText(""+currentItem.getParent().getValue().getLabel());
            valueField.setText(currentItem.getValue().getValue());
            titleField.setText(currentItem.getValue().getTitle());
        }
        else {
            pid.setText(currentItem.getValue().getLabel());
            valueField.setText("");
            titleField.setText("");
        }
        if(currentItem.isLeaf()){
            bigTitle.setText("编辑 " + currentItem.getValue().getTitle() + " : ");
            System.out.println("是叶子节点");
        }
        else{
            bigTitle.setText("为 " + currentItem.getValue().getTitle() + " 添加新页面: ");
        }

        editView.getChildren().add(editStatusView);
        editView.setAlignment(Pos.TOP_LEFT);
    }

    //清空右侧编辑视图的内容
    private void clearEditView(){
        editView.getChildren().clear();
    }

    private TreeItem<MyTreeNode> getChildren(MyTreeNode node){
        TreeItem<MyTreeNode> treeItem = new TreeItem<>(node);
        List<MyTreeNode> children = node.getChildren();
        //递归退出条件: 当前子节点为空或者子节点对象为null
        if(children == null || children.isEmpty()){
            return treeItem;
        }
        for(MyTreeNode i : children){
            //递归调用以获取所有子节点
            treeItem.getChildren().add(getChildren(i));
        }
        return treeItem;
    }

    public void updateTreeView(){
        DataRequest req = new DataRequest();
        List<MyTreeNode> treeNodes = HttpRequestUtil.requestTreeNodeList("/api/base/getMenuTreeNodeList", req);
        if(treeNodes == null || treeNodes.isEmpty()){
            return;
        }
        for(MyTreeNode i : treeNodes){
            //加载所有节点并添加到根节点
            root.getChildren().add(getChildren(i));
        }
    }
}
