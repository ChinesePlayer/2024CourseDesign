package com.teach.javafx.controller.shortcuts;

import com.teach.javafx.controller.base.MessageDialog;
import com.teach.javafx.customWidget.DataCarryingButton;
import com.teach.javafx.managers.SettingManager;
import com.teach.javafx.managers.ShortcutManager;
import com.teach.javafx.managers.ShortcutsDisplayer;
import com.teach.javafx.models.Shortcut;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.input.*;
import javafx.scene.layout.FlowPane;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

public class ShortcutsEditorController {
    @FXML
    public FlowPane displayedPane;
    @FXML
    public FlowPane notDisplayedPane;

    private List<Shortcut> displayed = new ArrayList<>();
    private List<Shortcut> notDisplayed = new ArrayList<>();

    //拖动时传输的数据格式
    //注意此处必须是static变量，因为javafx不允许出现相同的DataFormat, 第二次打开页面时会直接报异常
    private static final DataFormat shortcutFormat = new DataFormat("shortcut");
    private ShortcutsDisplayer opener;

    public void init(ShortcutsDisplayer o){
        this.opener = o;
    }

    @FXML
    public void initialize(){
        //从ShortcutManager处获得数据
        notDisplayed = ShortcutManager.getInstance().getNotDisplayedShortcuts();
        displayed = ShortcutManager.getInstance().getShortcutsList();
        //根据数据构建页面
        List<DataCarryingButton<Shortcut>> displayedButtons = new ArrayList<>();
        List<DataCarryingButton<Shortcut>> notDisplayedButtons = new ArrayList<>();
        for(Shortcut s : displayed){
            DataCarryingButton<Shortcut> btn = getShortcutDataCarryingButton(s);
            displayedButtons.add(btn);
        }

        for(Shortcut s : notDisplayed){
            DataCarryingButton<Shortcut> btn = getShortcutDataCarryingButton(s);
            notDisplayedButtons.add(btn);
        }

        //添加初始数据
        displayedPane.getChildren().addAll(displayedButtons);
        notDisplayedPane.getChildren().addAll(notDisplayedButtons);

        //为FlowPane添加拖动回调
        //拖动的东西进入displayedPane时的回调
        displayedPane.setOnDragOver(dragEvent -> {
            //注意，被拖动的对象是一个按钮，这个按钮的父节点才是FlowPane，所以此处要获取它的Parent
            if(((Node)dragEvent.getGestureSource()).getParent() != displayedPane && dragEvent.getDragboard().hasContent(shortcutFormat)){
                dragEvent.acceptTransferModes(TransferMode.MOVE);
            }
        });
        //用户将拖动的东西放入displayedPane的回调
        displayedPane.setOnDragDropped(dragEvent -> {
            Dragboard db = dragEvent.getDragboard();
            boolean isSuccess = false;
            if(db.hasContent(shortcutFormat)){
                DataCarryingButton target = (DataCarryingButton) dragEvent.getGestureSource();
                if(target.getParent() != displayedPane){
                    notDisplayedPane.getChildren().remove(target);
                }
                DataCarryingButton<Shortcut> btn = getShortcutDataCarryingButton((Shortcut) db.getContent(shortcutFormat));
                displayedPane.getChildren().add(btn);
                isSuccess = true;
            }
            dragEvent.setDropCompleted(isSuccess);
            dragEvent.consume();
        });

        notDisplayedPane.setOnDragOver(dragEvent -> {
            //注意，被拖动的对象是一个按钮，这个按钮的父节点才是FlowPane，所以此处要获取它的Parent
            if(((Node)dragEvent.getGestureSource()).getParent() != notDisplayedPane && dragEvent.getDragboard().hasContent(shortcutFormat)){
                dragEvent.acceptTransferModes(TransferMode.MOVE);
            }
        });
        //用户将拖动的东西放入displayedPane的回调
        notDisplayedPane.setOnDragDropped(dragEvent -> {
            Dragboard db = dragEvent.getDragboard();
            boolean isSuccess = false;
            if(db.hasContent(shortcutFormat)){
                DataCarryingButton target = (DataCarryingButton) dragEvent.getGestureSource();
                if(target.getParent() != notDisplayedPane){
                    displayedPane.getChildren().remove(target);
                }
                DataCarryingButton<Shortcut> btn = getShortcutDataCarryingButton((Shortcut) db.getContent(shortcutFormat));
                notDisplayedPane.getChildren().add(btn);
                isSuccess = true;
            }
            dragEvent.setDropCompleted(isSuccess);
            dragEvent.consume();
        });

    }

    private DataCarryingButton<Shortcut> getShortcutDataCarryingButton(Shortcut s) {
        DataCarryingButton<Shortcut> btn = new DataCarryingButton<>(s.getName(), s);
        //添加拖动回调
        btn.setOnDragDetected(mouseEvent -> {
            Dragboard db = btn.startDragAndDrop(TransferMode.MOVE);
            ClipboardContent content = new ClipboardContent();
            content.put(shortcutFormat, btn.getData());
            db.setContent(content);
            mouseEvent.consume();
        });
        return btn;
    }

    public void onSave(){
        List<Shortcut> newShortcuts = new ArrayList<>();
        for(Node n : displayedPane.getChildren()){
            DataCarryingButton<Shortcut> btn = (DataCarryingButton<Shortcut>) n;
            newShortcuts.add(btn.getData());
        }
        //通过ShortcutManager和SettingManager保存修改
        ShortcutManager.getInstance().saveChanges(newShortcuts);
        SettingManager.getInstance().save();

        opener.onEdited(newShortcuts);
        //提示用户保存成功
        MessageDialog.showDialog("修改成功! ");
        //关闭自己
        ((Stage)displayedPane.getScene().getWindow()).close();
    }
}
