package com.teach.javafx.managers;

import java.util.Map;

//被填入设置文件中的类需要实现改接口
public interface Settable {
    Map<String, Object> getPairs();
    //实时应用修改，可以选择是否重载
    default void applyChange(){}
    default boolean isModified(){return true;}
}
