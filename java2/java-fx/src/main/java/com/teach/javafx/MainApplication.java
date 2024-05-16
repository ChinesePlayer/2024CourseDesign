package com.teach.javafx;

import com.teach.javafx.controller.base.MainFrameController;
import com.teach.javafx.controller.base.MessageDialog;
import com.teach.javafx.managers.ShortcutManager;
import com.teach.javafx.managers.ThemeManager;
import com.teach.javafx.managers.WindowsManager;
import com.teach.javafx.request.HttpRequestUtil;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * 应用的主程序 MainApplication 按照编程规范，需继承Application 重写start 方法 主方法调用Application的launch() 启动程序
 */
public class MainApplication extends Application {
    /**
     * 加载登录对话框，设置登录Scene到Stage,显示该场景
     * @param stage
     * @throws IOException
     */
    private static Stage mainStage;
    private static double stageWidth = -1;
    private static double stageHeight = -1;

    public static double stageMinHeight = 700.0;
    public static double stageMinWidth = 1000.0;

    public static double loginStageMinWidth = 700;
    public static double loginStageMinHeight = 400;
    public static double loginStageWidth = 700;
    public static double loginStageHeight = 400;

    private static boolean canClose=true;

    public static double getScreenWidth(){
        Screen screen = Screen.getPrimary();
        Rectangle2D bounds = screen.getBounds();
        return bounds.getWidth();
    }

    public static double getScreenHeight(){
        Screen screen = Screen.getPrimary();
        Rectangle2D bounds = screen.getBounds();
        return bounds.getHeight();
    }

    @Override
    public void start(Stage stage) throws IOException {
        //初始化主题管理器
        ThemeManager.init();
        //初始化窗口管理器
        WindowsManager.init();
        //初始化快捷方式管理器
        ShortcutManager.init();

        FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("base/custom-login-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), loginStageWidth, loginStageHeight);
        //scene.getStylesheets().add(getClass().getResource("css/styles.css").toExternalForm());
        stage.setTitle("登录");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
        stage.setOnCloseRequest(event -> {
            if(canClose) {
               HttpRequestUtil.close();
           }else {
               event.consume();
           }
        });
        mainStage = stage;
    }

    /**
     * 给舞台设置新的Scene
     * @param name 标题
     * @param scene 新的场景对象
     */
    public static void resetStage(String name, Scene scene) {
        closeCurrentStage();
        mainStage = new Stage();
        if(stageWidth > 0) {
            mainStage.setWidth(stageWidth);
            mainStage.setHeight(stageHeight);
            mainStage.setX(0);
            mainStage.setY(0);
        }
        mainStage.setTitle(name);
        mainStage.setScene(scene);
        mainStage.setResizable(true);
        mainStage.setMaximized(true);
        mainStage.setMinHeight(stageMinHeight);
        mainStage.setMinWidth(stageMinWidth);
        mainStage.show();
    }

    public static void loginStage(String name, Scene scene) {
        mainStage = new Stage();
        double screenWidth = getScreenWidth();
        double screenHeight = getScreenHeight();
        double windowWidth = scene.getWidth();
        double windowHeight = scene.getHeight();
        System.out.println(screenWidth + ", " + screenHeight);
        System.out.println(windowWidth + ", " + windowHeight);
        mainStage.setX((screenWidth - windowWidth)/2.0);
        mainStage.setY((screenHeight - windowHeight)/2.0);
        mainStage.setResizable(false);
        mainStage.setTitle(name);
        mainStage.setScene(scene);
        mainStage.setWidth(loginStageMinWidth);
        mainStage.setHeight(loginStageMinHeight);
        mainStage.setMinWidth(loginStageMinWidth);
        mainStage.setMinHeight(loginStageMinHeight);
        mainStage.show();
    }

    //打开主页面
    public static void openMainContent(){
        //获取登录的角色类型
        String role = AppStore.getJwt().getRoles();
        if(role == null || role.isEmpty()){
            MessageDialog.showDialog("权限错误! ");
            return;
        }
        try{
            String appTitle = "教学管理系统";
            if(role.equals("ROLE_ADMIN")){
                FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("base/main-frame.fxml"));
                Scene scene = new Scene(fxmlLoader.load(), -1, -1);
                AppStore.setMainFrameController((MainFrameController) fxmlLoader.getController());
                //关闭当前窗口
                MainApplication.closeCurrentStage();
                resetStage(appTitle, scene);
            }
            else if(role.equals("ROLE_STUDENT")){
                FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("base/main-frame.fxml"));
                Scene scene = new Scene(fxmlLoader.load(), -1, -1);
                AppStore.setMainFrameController((MainFrameController) fxmlLoader.getController());
                //关闭当前窗口
                MainApplication.closeCurrentStage();
                resetStage(appTitle, scene);
            }
            else if(role.equals("ROLE_TEACHER")){
                FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("base/main-frame.fxml"));
                Scene scene = new Scene(fxmlLoader.load(), -1, -1);
                AppStore.setMainFrameController((MainFrameController) fxmlLoader.getController());
                //关闭当前窗口
                MainApplication.closeCurrentStage();
                resetStage(appTitle, scene);
            }
            else {
                MessageDialog.showDialog("权限错误! ");
            }
        }
        catch (IOException e){
            e.printStackTrace();
            MessageDialog.showDialog("无法打开主页面!\n 异常信息: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        launch();
    }

    public static Stage getMainStage() {
        return mainStage;
    }

    public static void setCanClose(boolean canClose) {
        MainApplication.canClose = canClose;
    }

    //关闭当前窗口
    public static void closeCurrentStage(){
        if(mainStage != null){
            mainStage.close();
            mainStage = null;
        }
    }
}