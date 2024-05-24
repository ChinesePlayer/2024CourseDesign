package org.fatmansoft.teach.util;

import java.io.File;

//在文件保存成功后执行
public interface HasSavedAction {
    void action(File savedFile);
}
