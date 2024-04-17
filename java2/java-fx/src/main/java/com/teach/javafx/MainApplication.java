package com.teach.javafx;

import com.teach.javafx.controller.base.MessageDialog;
import com.teach.javafx.request.HttpRequestUtil;
import com.teach.javafx.request.SQLiteJDBC;
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
        FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("base/custom-login-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 700, 400);
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
        mainStage.setMinWidth(loginStageMinWidth);
        mainStage.setMinHeight(loginStageMinHeight);
        mainStage.show();
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
}