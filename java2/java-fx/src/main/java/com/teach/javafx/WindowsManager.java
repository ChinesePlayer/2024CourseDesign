package com.teach.javafx;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.stage.WindowEvent;

import java.util.List;


//一个窗口管理器，单例
public class WindowsManager {
    private static WindowsManager instance;
    private ObservableList<Window> windows;

    private WindowsManager(){
        //获取所有窗口
        this.windows =  Stage.getWindows();
    }

    //初始化窗口管理器
    private static void initWindowsManager(){
        if(instance == null){
            instance = new WindowsManager();
        }
        //获取所有窗口
        instance.windows = Stage.getWindows();
        //添加监听器，为每个新增的窗口设置样式
        instance.windows.addListener(new ListChangeListener<Window>() {
            @Override
            public void onChanged(Change<? extends Window> change) {
                while(change.next()){
                    if(change.wasAdded()){
                        List<Window> addedWindows = (List<Window>) change.getAddedSubList();
                        for(Window w : addedWindows){
                            Stage stage = (Stage) w;
                            stage.getScene().getStylesheets().clear();
                            stage.getScene().getStylesheets().add(ThemeManager.getInstance().getCurrentTheme());
                        }
                    }
                }
            }
        });
    }

    public static WindowsManager getInstance(){
        return instance;
    }

    public static void init(){
        if(instance != null){
            return;
        }
        initWindowsManager();
        System.out.println(instance.windows.size());
    }
}
