package com.teach.javafx.controller.setting;

import com.teach.javafx.managers.SettingManager;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.HBox;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

public class CommonSettingController {
    @FXML
    public HBox radioHBox;
    private List<RadioButton> themeRadios = new ArrayList<>();
    private ToggleGroup themeRadioGroup = new ToggleGroup();
    //选项名称和主题名的映射
    private Map<String, String> themeMap = new HashMap<>();

    @FXML
    public void initialize(){
        //遍历所有主题名和主题id的键值对并生成RadioButton
        SettingManager.getInstance().getThemeMapper().forEach(new BiConsumer<String, String>() {
            @Override
            public void accept(String name, String id) {
                RadioButton rb = new RadioButton(name);
                rb.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        System.out.println("选择了: " + themeMap.get(name)+ " 主题!");
                    }
                });
                themeRadios.add(rb);
                rb.setToggleGroup(themeRadioGroup);
                themeMap.put(name, id);
            }
        });
        radioHBox.getChildren().addAll(FXCollections.observableArrayList(themeRadios));
    }

}
