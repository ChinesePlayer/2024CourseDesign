package com.teach.javafx.managers;

import com.teach.javafx.models.Shortcut;

import java.util.List;

//想在界面中展示快捷方式的页面实现此接口
//注：实现此接口可以为展示页面提供修改快捷方式的功能，但若仅展示不修改也可不实现此接口
public interface ShortcutsDisplayer {
    //修改后，修改者会调用此方法，回传新的快捷方式列表
    void onEdited(List<Shortcut> newShortcuts);
}
