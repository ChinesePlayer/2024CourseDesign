package com.teach.javafx.managers;

import com.teach.javafx.MainApplication;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

//应用程序主题管理器，单例
public class ThemeManager {
    private static ThemeManager instance;
    private final String CSS_PATH = "css/";
    //储存主题文件的主题名称-文件名映射
    private Map<String, String> styleSheets = new HashMap();
    //储存已加载的主题
    private Map<String, String> loadedTheme = new HashMap<>();
    //主题名---主题ID映射
    private Map<String, String> nameMapper;

    //当前主题名称
    private String currentThemeId;

    private ThemeManager(){
        URL cssFolderUrl = MainApplication.class.getResource(CSS_PATH);
        if(cssFolderUrl != null){
            File cssFolder = new File(cssFolderUrl.getFile());
            cssFolder.listFiles((dir, name) -> {
                if(name.endsWith(".css")){
                    int pos = name.indexOf("-theme");
                    if(pos < 0){
                        //不是主题文件，退出
                        return false;
                    }
                    //获取主题名称
                    String themeName = name.substring(0, pos);
                    styleSheets.put(themeName, name);
                }
                return false;
            });
        }

        //加载默认主题
        String themeName = SettingManager.getInstance().getTheme();
        String themeFile;
        if(themeName == null){
            themeFile = styleSheets.get("default");
        }
        else{
            themeFile = styleSheets.get(themeName);
        }
        String theme = MainApplication.class.getResource(CSS_PATH + themeFile).toExternalForm();
        currentThemeId = themeName;
        loadedTheme.put(themeName, theme);

        //获取主题名---主题ID的映射
        nameMapper = SettingManager.getInstance().getThemeMapper();
    }

    //根据提供的主题名称加载并返回相应的主题
    public String getTheme(String themeName){
        String themeFile = styleSheets.get(themeName);
        if(themeFile == null){
            //提供的主题不存在，直接退出
            return null;
        }
        //先从已加载的主题文件中寻找，若已经加载则直接返回
        String theme = loadedTheme.get(themeName);
        if(theme != null){
            return theme;
        }
        //若未加载，则加载后返回，并将其储存在已加载的主题中
        theme = MainApplication.class.getResource(CSS_PATH + themeFile).toExternalForm();
        loadedTheme.put(themeName, theme);
        return theme;
    }

    private static void initThemeManager(){
        if(instance == null){
            instance = new ThemeManager();
        }
    }

    public static ThemeManager getInstance(){
        return instance;
    }

    public static void init(){
        if(instance != null){
            return;
        }
        initThemeManager();
    }

    public String getCurrentThemeId(){
        return currentThemeId;
    }

    public void changeTo(String themeId){
        List<Window> windows = WindowsManager.getInstance().getWindows();
        String theme = getTheme(themeId);
        if(theme == null){
            return;
        }
        for(Window w : windows){
            Stage s = (Stage) w;
            s.getScene().getStylesheets().clear();
            s.getScene().getStylesheets().add(theme);
        }
        currentThemeId = themeId;
    }

    //通过主题Id获取主题名
    public String getThemeName(String themeId){
        final String[] themeName = {null};
        //遍历整个nameMapper
        nameMapper.forEach((s, s2) -> {
            if(s2.equals(themeId)){
                themeName[0] = s;
            }
        });
        return themeName[0];
    }

    public String getCurrentThemeName(){
        return getThemeName(currentThemeId);
    }

    public String getCurrentExternForm(){
        return getTheme(currentThemeId);
    }

    //获取所有主题的名字
    public List<String> getNames(){
        List<String> res = new ArrayList<>();
        styleSheets.forEach((s, s2) -> res.add(s));
        return res;
    }
}
