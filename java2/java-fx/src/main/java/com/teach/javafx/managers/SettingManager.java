package com.teach.javafx.managers;

import com.teach.javafx.MainApplication;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.*;
import java.net.URI;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

//设置管理类，用于维护设置文件
public class SettingManager {
    private static SettingManager instance;
    private static String SETTING_PATH = "system_setting.json";
    private static Map<String, Object> setting = new HashMap<>();
    //静态初始化块，用于加载instance和加载设置文件
    static {
        instance = new SettingManager();
        try{
            URI settingURI = MainApplication.class.getResource(SETTING_PATH).toURI();
            String settingPath = Paths.get(settingURI).toString();
            BufferedReader reader = new BufferedReader(new FileReader(settingPath));
            JSONTokener tokener = new JSONTokener(reader);
            JSONObject jsonObject = new JSONObject(tokener);
            setting = toMap(jsonObject);
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
    private SettingManager(){

    }

    public static SettingManager getInstance(){
        if(instance == null){
            instance = new SettingManager();
        }
        return instance;
    }

    private static Map<String, Object> toMap(JSONObject object) {
        Map<String, Object> map = new HashMap<>();

        // 遍历 JSONObject 的键值对
        for (String key : object.keySet()) {
            Object value = object.get(key);

            // 如果值是 JSONObject 类型，则递归调用该方法
            if (value instanceof JSONObject) {
                value = toMap((JSONObject) value);
            }

            // 将键值对添加到 Map 中
            map.put(key, value);
        }

        return map;
    }

    //获取当前选择的theme名称
    public String getTheme(){
        return (String) setting.get("theme");
    }

    public void setTheme(String theme){
        setting.put("theme", theme);
    }

    //获取主题名映射
    public Map<String, String> getThemeMapper(){
        Map<String, String> res = (Map<String, String>) setting.get("themeMapper");
        return res;
    }

    public void set(Map<String ,Object> m){
        setting.putAll(m);
    }

//    //将设置保存至文件
//    public void save(){
//        try{
//            FileWriter writer = new FileWriter()
//        }
//    }
}
