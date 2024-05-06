package com.teach.javafx.managers;

import com.teach.javafx.MainApplication;
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
    private ShortcutManager(){

    }

    public static void init(){
        if(instance == null){
            instance = new ShortcutManager();
        }
        //重置shortcuts
        instance.shortcuts = new HashMap<>();
        //加载基本的快捷页面
        instance.shortcuts.put("进入选课", "courseSelection/course-selection-menu.fxml");
        instance.shortcuts.put("成绩查询", "studentScore/course-score.fxml");
        instance.shortcuts.put("系统设置", "setting/system-setting.fxml");
    }

    //用于外部调用来将调用者注册到快捷操作中
    public void registerAction(Shortcutable sca){
        if(instance == null){
            init();
        }
        shortcuts.put(sca.getName(), sca.getFXMLPath());
    }

    //根据可
    public List<Button> getShortcutActions(){
        List<Button> actions = new ArrayList<>();
        shortcuts.forEach((name, fxmlPath) -> {
            Button action = new Button(name);
            action.setOnAction(event -> {
                FXMLLoader loader = new FXMLLoader(MainApplication.class.getResource(fxmlPath));
                try {
                    Scene scene = new Scene(loader.load(), 800, 500);
                    Stage stage = new Stage();
                    stage.setScene(scene);
                    stage.setTitle(name);
                    stage.initOwner(MainApplication.getMainStage());
                    stage.initModality(Modality.WINDOW_MODAL);
                    shortcutsStages.add(stage);
                    stage.setOnCloseRequest(windowEvent -> shortcutsStages.set(shortcutsStages.size()-1, null));
                    stage.show();
                }
                catch (IOException e){
                    e.printStackTrace();
                }
            });
            actions.add(action);
        });
        return actions;
    }

    public static ShortcutManager getInstance(){
        init();
        return instance;
    }
}
