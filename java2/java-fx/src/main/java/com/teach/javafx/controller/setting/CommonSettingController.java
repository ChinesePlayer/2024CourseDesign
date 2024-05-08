package com.teach.javafx.controller.setting;

import com.teach.javafx.managers.Settable;
import com.teach.javafx.managers.SettingManager;
import com.teach.javafx.managers.ThemeManager;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.HBox;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class CommonSettingController implements Settable {
    @FXML
    public HBox radioHBox;
    private List<RadioButton> themeRadios = new ArrayList<>();
    private ToggleGroup themeRadioGroup = new ToggleGroup();
    //主题名和主题ID的映射
    private Map<String, String> themeMap = new HashMap<>();

    @FXML
    public void initialize(){
        //遍历所有主题名和主题id的键值对并生成RadioButton
        SettingManager.getInstance().getThemeMapper().forEach((name, id) -> {
            RadioButton rb = new RadioButton(name);
            rb.setOnAction(event -> System.out.println("选择了: " + themeMap.get(name)+ " 主题!"));
            themeRadios.add(rb);
            rb.setToggleGroup(themeRadioGroup);
            themeMap.put(name, id);
        });
        radioHBox.getChildren().addAll(FXCollections.observableArrayList(themeRadios));
        radioHBox.getChildren().forEach(node -> {
            RadioButton radioButton = (RadioButton) node;
            if(radioButton.getText().equals(ThemeManager.getInstance().getCurrentThemeName())){
                themeRadioGroup.selectToggle(radioButton);
            }
        });
    }

    @Override
    public Map<String, Object> getPairs() {
        Map res = new HashMap<>();
        res.put("theme", themeMap.get(((RadioButton)themeRadioGroup.getSelectedToggle()).getText()));
        return res;
    }

    @Override
    public boolean isModified() {
        RadioButton button = (RadioButton) themeRadioGroup.getSelectedToggle();
        if(ThemeManager.getInstance().getCurrentThemeId() != themeMap.get(button.getText())){
            return true;
        }
        return false;
    }

    @Override
    public void applyChange(){
        RadioButton button = (RadioButton) themeRadioGroup.getSelectedToggle();
        String themeName = button.getText();
        ThemeManager.getInstance().changeTo(themeMap.get(themeName));
    }
}
