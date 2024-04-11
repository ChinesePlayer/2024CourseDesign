package com.teach.javafx.controller.base;

//对话框关闭时的回调， 可自定义参数的数据类型
public interface DialogCallback<T>{
    void onDialogClose(T data);
}
