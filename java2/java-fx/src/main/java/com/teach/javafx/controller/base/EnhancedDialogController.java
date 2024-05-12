package com.teach.javafx.controller.base;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.layout.FlowPane;
import javafx.stage.Stage;

public class EnhancedDialogController {
    @FXML
    public FlowPane flowPane;

    private Stage thisStage;

    @FXML
    public void initialize(){

    }

    public void setStage(Stage stage){
        this.thisStage = stage;
    }

    //在显示新的内容前清空已有内容
    public void showDialog(Node widget){
        flowPane.getChildren().clear();
        flowPane.getChildren().add(widget);
        this.thisStage.show();
    }

    public void closeDialog(){
        if(this.thisStage != null && this.thisStage.isShowing()){
            this.thisStage.close();
        }
    }

}
