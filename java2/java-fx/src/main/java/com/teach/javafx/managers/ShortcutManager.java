package com.teach.javafx.managers;

import com.teach.javafx.MainApplication;
import com.teach.javafx.models.Shortcut;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//用来管理学生仪表盘的快捷方式, 单例
public class ShortcutManager {
    private static ShortcutManager instance;
    private Map<String, String> shortcuts = new HashMap<>();
    //通过快捷方式打开的所有窗口
    private List<Stage> shortcutsStages = new ArrayList<>();
    //当前已显示的快捷方式
    private List<Shortcut> shortcutsList = new ArrayList<>();
    //所有的快捷方式
    private List<Shortcut> allShortcuts = new ArrayList<>();
    private ShortcutManager(){

    }

    public static void init(){
        if(instance == null){
            instance = new ShortcutManager();
        }
        //重置shortcuts
        instance.shortcuts = new HashMap<>();
        //加载设置页面中指定的快捷方式
        instance.shortcutsList = SettingManager.getInstance().getShortcutsList();
        instance.allShortcuts = SettingManager.getInstance().getAllShortcutsList();
    }

    //工具方法，判断两个快捷方式是否相同
    private boolean isEqual(Shortcut s1, Shortcut s2){
        return s1.equals(s2);
    }

    //用于外部调用来将调用者注册到快捷操作中
    public void registerAction(Shortcutable sca){
        if(instance == null){
            init();
        }
        shortcuts.put(sca.getName(), sca.getFXMLPath());
    }


    public List<Button> getShortcutActions(){
        List<Button> actions = new ArrayList<>();
        for(Shortcut s : shortcutsList){
            Button action = new Button(s.getName());
            action.setOnAction(event -> {
                FXMLLoader loader = new FXMLLoader(MainApplication.class.getResource(s.getFxml()));
                try {
                    Stage stage = WindowsManager.getInstance().openNewWindow(
                            loader,800,500,s.getName(),
                            MainApplication.getMainStage(),Modality.WINDOW_MODAL,
                            null
                    );
                    shortcutsStages.add(stage);
                    stage.setOnCloseRequest(windowEvent -> shortcutsStages.set(shortcutsStages.size()-1, null));
                    stage.show();
                }
                catch (IOException e){
                    e.printStackTrace();
                }
            });
            actions.add(action);
        }
//        shortcuts.forEach((name, fxmlPath) -> {
//            Button action = new Button(name);
//            action.setOnAction(event -> {
//                FXMLLoader loader = new FXMLLoader(MainApplication.class.getResource(fxmlPath));
//                try {
//                    Scene scene = new Scene(loader.load(), 800, 500);
//                    Stage stage = new Stage();
//                    stage.setScene(scene);
//                    stage.setTitle(name);
//                    stage.initOwner(MainApplication.getMainStage());
//                    stage.initModality(Modality.WINDOW_MODAL);
//                    shortcutsStages.add(stage);
//                    stage.setOnCloseRequest(windowEvent -> shortcutsStages.set(shortcutsStages.size()-1, null));
//                    stage.show();
//                }
//                catch (IOException e){
//                    e.printStackTrace();
//                }
//            });
//            actions.add(action);
//        });
        return actions;
    }

    //将传入的按钮绑定至
    public void convertToActions(){

    }

    public List<Shortcut> getShortcutsList(){
        return shortcutsList;
    }

    public List<Shortcut> getAllShortcuts(){
        return allShortcuts;
    }

    public List<Shortcut> getNotDisplayedShortcuts(){
        List<Shortcut> undisplayed = new ArrayList<>();
        for(Shortcut s : allShortcuts){
            if(!shortcutsList.contains(s)){
                undisplayed.add(s);
            }
        }
        return undisplayed;
    }

    //外界传入修改，该方法负责和SettingManager交互从而保存修改
    public void saveChanges(List<Shortcut> display){
        SettingManager.getInstance().setDisplayedShortcuts(display);
        shortcutsList = display;
    }

    public static ShortcutManager getInstance(){
        init();
        return instance;
    }

}
