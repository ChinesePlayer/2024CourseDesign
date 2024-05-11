package org.fatmansoft.teach.factory;

import org.fatmansoft.teach.models.Homework;
import org.fatmansoft.teach.models.HomeworkFile;
import org.fatmansoft.teach.util.CommonMethod;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

//作业简单工厂，用于装载向前端返回的数据
@Component
public class HomeworkFactory {
    public Map createHomeworkMap(Homework homework, String type){
        Map data = new HashMap<>();
        data.put("homeworkId", homework.getHomeworkId());
        data.put("courseId", homework.getCourse().getCourseId());
        data.put("title", homework.getTitle());
        data.put("content", homework.getContent());
        data.put("start", CommonMethod.getLocalDateTimeString(homework.getStart()));
        data.put("end", CommonMethod.getLocalDateTimeString(homework.getEnd()));
        return data;
    }

    public Map createHomeworkFileMap(HomeworkFile homeworkFile, String type){
        Map data = new HashMap<>();
        data.put("fileName", homeworkFile.getFileName());
        data.put("fileId", homeworkFile.getFileId());
        data.put("fileSize", homeworkFile.getFileSize());
        return data;
    }
}
