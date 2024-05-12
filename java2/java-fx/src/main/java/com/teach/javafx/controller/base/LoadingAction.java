package com.teach.javafx.controller.base;

//耗时操作执行时执行的操作
//loopAction操作会被循环执行，每一轮循环都会传入耗时操作的完成状态
//onceAction只会被执行一次
public interface LoadingAction {
    default void loopAction(boolean isComplete){}
    void onceAction();
}
