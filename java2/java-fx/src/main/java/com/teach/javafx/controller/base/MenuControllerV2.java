package com.teach.javafx.controller.base;

import com.teach.javafx.request.HttpRequestUtil;
import com.teach.javafx.request.MyTreeNode;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXCheckbox;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import org.fatmansoft.teach.payload.request.DataRequest;
import org.fatmansoft.teach.payload.response.DataResponse;

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

    //当前选中的节点
    private TreeItem<MyTreeNode> currentItem;

    //未选择任何页面时编辑视图显示的内容
    private Label tipText1;
    private Label tipText2;
    private MFXButton addButton;

    //编辑视图进入编辑状态时的一系列组件
    @FXML
    private Label pidLabel;
    @FXML
    private MFXTextField valueField;
    @FXML
    private MFXTextField titleField;
    @FXML
    private MFXButton saveButton;
    @FXML
    private Label bigTitle;
    @FXML
    private MFXCheckbox admin;
    @FXML
    private MFXCheckbox student;
    @FXML
    private MFXCheckbox teacher;

    //上下文菜单的删除按钮
    private MenuItem deleteContextMenu;

    //根节点(只能有一个)
    private TreeItem<MyTreeNode> root;

    //点击保存时要提交的节点数据
    private MyTreeNode editNode;

    //编辑类型
    //0: 添加了根节点(即菜单)    1: 添加了叶子节点(即新页面)
    //2: 编辑了某个页面
    private Integer editType = 0;

    @FXML
    public void initialize(){
        MyTreeNode node = new MyTreeNode(null, "", "", 0,null);
        editNode = new MyTreeNode(null, "", "", 0,null);
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
                //若当前点击的对象是TreeCell类型，则获取该TreeCell
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
        ContextMenu contextMenu = new ContextMenu();
        deleteContextMenu = new MenuItem("删除");
        deleteContextMenu.setOnAction(this::onDeleteButtonPressed);
        contextMenu.getItems().add(deleteContextMenu);
        menuTreeView.setContextMenu(contextMenu);
    }

    private void onDeleteButtonPressed(ActionEvent event){
        if(currentItem == null || currentItem.getValue() == null){
            MessageDialog.showDialog("删除失败: 未选择任何菜单");
            return;
        }
        if(currentItem.getParent() == null){
            MessageDialog.showDialog("删除失败: 不能删除根节点");
            return;
        }
        if(!currentItem.getChildren().isEmpty()){
            int confirm = MessageDialog.choiceDialog("此操作会将 " + currentItem.getValue().getLabel() + " 中的所有子页面删除，你确定吗");
            //若用户没有选择确定，则终止删除操作
            if(confirm != MessageDialog.CHOICE_YES){
                return;
            }
        }
        TreeItem<MyTreeNode> parent = currentItem.getParent();
        DataRequest req = new DataRequest();
        req.add("id", currentItem.getValue().getId());
        DataResponse res = HttpRequestUtil.request("/api/base/menuDelete", req);
        assert res != null;
        if(res.getCode() == 0){
            //从UI中删除目标节点
            parent.getChildren().remove(currentItem);
            currentItem = new TreeItem<>();
            MessageDialog.showDialog("删除成功!");
        }
        else {
            MessageDialog.showDialog(res.getMsg());
        }
    }

    private String getPidFromPidLabel(){
        if(pidLabel.getText().isEmpty() || pidLabel.getText() == null){
            return null;
        }
        String[] pidSplit = pidLabel.getText().split("-");
        return pidSplit[0];
    }

    //添加按钮按下时触发的事件
    private void onAddButtonPressed(ActionEvent event){
        editNode = new MyTreeNode();
        editType = 0;
        setAddStatus();
    }

    private void initIdleStatusContent(){
        tipText1 = new Label("请选择左侧的菜单");
        tipText2 = new Label("或");
        addButton = new MFXButton("添加新菜单");
        addButton.setOnAction(this::onAddButtonPressed);
        addButton.setStyle("-fx-background-color: #930e14;\n" +
                "    -fx-font-family: \"Open Sans SemiBold\";\n" +
                "    -fx-font-size: 17px;\n" +
                "    -fx-text-fill: #ffffff;");
    }

    private void initEditStatusContent(){

    }

    //判断当前选中的菜单是否为空
    private boolean isMenuEmpty(){
        ObservableList<TreeItem<MyTreeNode>> treeItems = currentItem.getChildren();
        return treeItems.isEmpty();
    }

    //判断当前选择的菜单中是否有空对象
    private boolean hasEmptyItem(){
        ObservableList<TreeItem<MyTreeNode>> treeItems = currentItem.getChildren();
        for(TreeItem<MyTreeNode> i : treeItems){
            if(i.getValue() == null){
                return true;
            }
        }
        return false;
    }

    //删除所有空节点
    private void deleteEmptyItems(){
        ObservableList<TreeItem<MyTreeNode>> treeItems = currentItem.getChildren();
        for(int i = 0; i < treeItems.size(); i++){
            if(treeItems.get(i).getValue() == null){
                currentItem.getChildren().remove(i);
            }
        }
    }

    //根据选中的节点的访问权限设置复选框的选中状态
    private void setCheckBoxStatus(){
        if(currentItem == null || currentItem.getValue() == null){
            return;
        }
        String userTypeStr = currentItem.getValue().getUserTypeIds();
        String[] userTypes = userTypeStr.split(",");
        for(String i : userTypes){
            switch (i) {
                case "1" -> admin.setSelected(true);
                case "2" -> student.setSelected(true);
                case "3" -> teacher.setSelected(true);
            }
        }
    }

    //取消所有复选框的选中状态
    private void clearCheckBox(){
        admin.setSelected(false);
        student.setSelected(false);
        teacher.setSelected(false);
    }

    //当新增按钮按下时，将编辑视图设为准备添加新菜单的状态
    private void setAddStatus(){
        clearEditView();
        bigTitle.setText("添加新菜单: ");
        editType = 0;
        titleField.setText("");
        valueField.setText("");
        pidLabel.setText("无");
        clearCheckBox();

        editView.getChildren().add(editStatusView);
        editView.setAlignment(Pos.TOP_LEFT);
    }

    private void setIdleStatus(){
        clearEditView();

        editView.getChildren().addAll(tipText1, tipText2, addButton);
        editView.setAlignment(Pos.CENTER);
    }

    //当某个节点被选中时，根据被选节点的数据加载编辑页面
    private void setEditStatus(){
        clearEditView();

        editNode = new MyTreeNode();

        if(!currentItem.getValue().getIsMenu()){
            editType = 2;
            pidLabel.setText(currentItem.getParent().getValue().getLabel());
            valueField.setText(currentItem.getValue().getName());
            titleField.setText(currentItem.getValue().getTitle());
            bigTitle.setText("编辑 " + currentItem.getValue().getTitle() + " : ");
            //设置复选框的选中状态
            clearCheckBox();
            setCheckBoxStatus();
        }
        else {
            editType = 1;
            pidLabel.setText(currentItem.getValue().getLabel());
            valueField.setText("");
            titleField.setText("");
            bigTitle.setText("为 " + currentItem.getValue().getTitle() + " 添加新页面: ");
            clearCheckBox();
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
            i.setIsMenu(false);
            treeItem.getChildren().add(getChildren(i));
        }
        return treeItem;
    }

    public void updateTreeView(){
        DataRequest req = new DataRequest();
        List<MyTreeNode> treeNodes = HttpRequestUtil.requestTreeNodeList("/api/base/getMenuTreeNodeList", req);
        System.out.println(treeNodes);
        if(treeNodes == null || treeNodes.isEmpty()){
            return;
        }
        for(MyTreeNode i : treeNodes){
            //加载所有节点并添加到根节点
            i.setIsMenu(true);
            root.getChildren().add(getChildren(i));
        }
    }

    //获取某个菜单下叶节点的最大ID
    //若指定的父节点不存在则返回null
    //否则返回该父节点下最大的ID
    private Integer getMaxIdFromPid(Integer pid){
        ObservableList<TreeItem<MyTreeNode>> allMenu = menuTreeView.getRoot().getChildren();
        TreeItem<MyTreeNode> targetParent = null;
        for(TreeItem<MyTreeNode> i : allMenu){
            if(Objects.equals(i.getValue().getId(), pid)){
                targetParent = i;
                break;
            }
        }
        Integer maxId = 0;
        if(targetParent == null){
            return null;
        }
        if(hasEmptyItem()){
            return 0;
        }
        System.out.println(targetParent.getChildren().size());
        for(TreeItem<MyTreeNode> i : targetParent.getChildren()){
            if(i.getValue().getId() > maxId){
                maxId = i.getValue().getId();
            }
        }
        return maxId;
    }

    //获取所有菜单中最大的ID
    //若返回0，则说明不存在任何菜单
    private Integer getMaxMenuId(){
        ObservableList<TreeItem<MyTreeNode>> allMenu = menuTreeView.getRoot().getChildren();
        Integer maxId = 0;
        for(TreeItem<MyTreeNode> i : allMenu){
            if(i.getValue().getId() > maxId){
                maxId = i.getValue().getId();
            }
        }
        return maxId;
    }

    //根据当前节点的状态计算ID
    private Integer calcId(){
        if(editType == 0){
            return getMaxMenuId() + 1;
        }
        else if(editType == 1){
            Integer maxItemId = getMaxIdFromPid(currentItem.getValue().getId());
            if(maxItemId == null){
                throw new RuntimeException("选择的父节点不存在!");
            }
            if(maxItemId == 0){
                return Integer.parseInt(getPidFromPidLabel() + "1");
            }
            else{
                return (maxItemId + 1);
            }
        }
        //若不是以上两种情况，则说明当前是在编辑已有节点，直接返回当前节点本身的Id即可
        return currentItem.getValue().getId();
    }

    //保存按钮按下时，将新的数据传到后端
    public void onSaveButtonPressed(){
        editNode.setName(valueField.getText());
        editNode.setTitle(titleField.getText());
        if(editType == 0){
            editNode.setIsMenu(true);
            editNode.setPid(null);
        }
        else{
            editNode.setIsMenu(false);
            editNode.setPid(Integer.parseInt(Objects.requireNonNull(getPidFromPidLabel())));
        }
        editNode.setId(calcId());
        String str = null;
        //构建用户访问权限字符串
        //1: 允许管理员访问    2: 允许学生访问    3: 允许老师访问
        if(admin.isSelected()) {
            if(str == null)
                str ="1";
            else str +=",1";
        }
        if(student.isSelected()) {
            if(str == null)
                str ="2";
            else str +=",2";
        }
        if(teacher.isSelected()) {
            if(str == null)
                str ="3";
            else str +=",3";
        }
        editNode.setUserTypeIds(str);
        editNode.setLabel(editNode.getId() + "-" + editNode.getTitle());
        System.out.println("保存时value为: " + editNode.getName());
        System.out.println("保存时pid为: " + editNode.getPid());
        DataRequest req = new DataRequest();
        req.add("editType", editType);
        req.add("node", editNode);
        DataResponse res = HttpRequestUtil.request("/api/base/menuSave",req);
        assert res != null;
        //操作成功后，进行UI更新操作
        if(res.getCode() == 0){
            //展示一个对话框来提示用户保存成功
            MessageDialog.showDialog("保存成功!");
            //关于editType不同值的含义，详见editType定义处
            if(editType == 0){
                TreeItem<MyTreeNode> newNode = new TreeItem<>(editNode);
                root.getChildren().add(newNode);
            }
            else if(editType == 1){
                currentItem.getChildren().add(new TreeItem<>(editNode));
            }
            else if(editType == 2){
                //将当前编辑节点设为当前节点(此处是为了实现在数据更新后，UI能够立刻发生变化)
                currentItem.setValue(editNode);
            }
        }
        else {
            MessageDialog.showDialog(res.getMsg());
        }
        //注意在提交结束后将当前编辑节点设为空，此处直接新建一个对象
        editNode = new MyTreeNode();
    }
}
