package com.teach.javafx.managers;

//该接口用于在新建窗口时对新窗口进行一些初始化操作
public interface WindowOpenAction {
    void init(Object controller);
}
