package com.teach.javafx.controller.base;

import com.teach.javafx.MainApplication;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.io.IOException;

/**
 * MessageDialog 消息对话框工具类 可以显示提示信息，用户选择确认信息和PDF显示
 */
public class MessageDialog {
    public final static int CHOICE_OK = 1;
    public final static int CHOICE_CANCEL = 2;
    public final static int CHOICE_YES = 3;
    public final static int CHOICE_NO = 4;

    private  MessageController messageController= null;
    private  ChoiceController choiceController= null;
    private  PdfViewerController pdfViewerController = null;
    private EnhancedDialogController enhancedDialogController = null;
    private static MessageDialog instance = new MessageDialog();

    /**
     * 初始加载三个页面
     * base/message-dialog.fxml  用于显示提示信息
     * base/choice-dialog.fxml 用于选择 是，否 取消 消息对话框
     * base/pdf-viewer-dialog.fxml 查看PDF效果对话框
     */
    private MessageDialog() {
        FXMLLoader fxmlLoader ;
        Scene scene = null;
        Stage stage;
        try {
            fxmlLoader = new FXMLLoader(MainApplication.class.getResource("base/message-dialog.fxml"));
            scene = new Scene(fxmlLoader.load(), 300, 260);
            stage = new Stage();
            stage.initOwner(MainApplication.getMainStage());
            stage.setAlwaysOnTop(true);
            stage.initModality(Modality.NONE);
            stage.setOnCloseRequest(e->{
                MainApplication.setCanClose(true);
            });
            stage.setScene(scene);
            stage.setTitle("信息显示对话框");
            messageController = (MessageController) fxmlLoader.getController();
            messageController.setStage(stage);

            fxmlLoader = new FXMLLoader(MainApplication.class.getResource("base/choice-dialog.fxml"));
            scene = new Scene(fxmlLoader.load(), 300, 260);
            stage = new Stage();
            stage.initOwner(MainApplication.getMainStage());
            stage.setAlwaysOnTop(true);
            stage.initModality(Modality.NONE);
            stage.setOnCloseRequest(e->{
                MainApplication.setCanClose(true);
            });
            stage.setScene(scene);
            stage.setTitle("信息显示对话框");
            choiceController = (ChoiceController) fxmlLoader.getController();
            choiceController.setStage(stage);

            fxmlLoader = new FXMLLoader(MainApplication.class.getResource("base/pdf-viewer-dialog.fxml"));
            scene = new Scene(fxmlLoader.load(), 600, 820);
            stage = new Stage();
            stage.initOwner(MainApplication.getMainStage());
            stage.setAlwaysOnTop(true);
            stage.initModality(Modality.NONE);
            stage.setOnCloseRequest(e->{
                MainApplication.setCanClose(true);
            });
            stage.setScene(scene);
            stage.setTitle("PDF显示对话框");
            pdfViewerController = (PdfViewerController) fxmlLoader.getController();
            pdfViewerController.setStage(stage);

            //加载增强版对话框
            fxmlLoader = new FXMLLoader(MainApplication.class.getResource("base/enhanced-dialog.fxml"));
            scene = new Scene(fxmlLoader.load(), 300, 260);
            stage = new Stage();
            stage.setScene(scene);
            stage.setTitle("提示信息");
            stage.setAlwaysOnTop(true);
            stage.initOwner(MainApplication.getMainStage());
            stage.initModality(Modality.NONE);
            stage.setOnCloseRequest(e->{
                MainApplication.setCanClose(true);
            });
            enhancedDialogController = fxmlLoader.getController();
            enhancedDialogController.setStage(stage);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * showDialog 信息提示
     * @param msg  提示的信息
     */
    public static void showDialog(String msg) {
        if(instance == null)
            return;
        if(instance.messageController == null)
            return;
        instance.messageController.showDialog(msg);
        MainApplication.setCanClose(false);
    }

    //showDialog的增强版本，可以显示控件，而不仅仅是文本
    public static void showDialog(Node widget){
        if(instance == null || instance.enhancedDialogController == null){
            return;
        }
        instance.enhancedDialogController.showDialog(widget);
        MainApplication.setCanClose(false);
    }

    //关闭增强版对话框
    public static void closeDialog(){
        if(instance == null || instance.enhancedDialogController == null){
            return;
        }
        instance.enhancedDialogController.closeDialog();
        MainApplication.setCanClose(true);
    }

    public static int showTest(String msg) {
        if(instance == null)
            return 1;
        if(instance.messageController == null)
            return 2;
        instance.messageController.showDialog(msg);
        MainApplication.setCanClose(false);
        return 123;
    }
    /**
     * choiceDialog 显示提示信息和是 否 取消按钮， 用户可选择
     * 点击 是 返回 CHOICE_YES = 3;
     * 点击 否 返回 CHOICE_NO = 4;
     * 点击 取消 返回 CHOICE_CANCEL = 2;
     * @param msg  提示的信息
     */
    public static int choiceDialog(String msg) {
        if(instance == null)
            return 0;
        if(instance.choiceController == null)
            return 0;
        MainApplication.setCanClose(false);
        return instance.choiceController.choiceDialog(msg);
    }

    /**
     * pdfViewerDialog 显示PDF
     * @param data  PDF二进制数据
     */
    public static void pdfViewerDialog(byte []data) {
        if(instance == null)
            return ;
        if(instance.pdfViewerController == null)
            return ;
        MainApplication.setCanClose(false);
        instance.pdfViewerController.showPdf(data);
    }

}
