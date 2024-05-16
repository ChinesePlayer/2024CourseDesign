package com.teach.javafx.customWidget;

import javafx.scene.control.Button;

//可携带数据的Button
public class DataCarryingButton<T> extends Button {
    private T data;


    public DataCarryingButton(){

    }

    public DataCarryingButton(T data){
        this.data = data;
    }

    public DataCarryingButton(String text, T data){
        this.data = data;
        setText(text);
    }

    public DataCarryingButton(String text){
        setText(text);
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
