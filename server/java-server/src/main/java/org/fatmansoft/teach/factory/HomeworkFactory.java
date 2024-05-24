package org.fatmansoft.teach.factory;

import org.fatmansoft.teach.models.Answer;
import org.fatmansoft.teach.models.AnswerFile;
import org.fatmansoft.teach.models.Homework;
import org.fatmansoft.teach.models.HomeworkFile;
import org.fatmansoft.teach.repository.AnswerRepository;
import org.fatmansoft.teach.util.CommonMethod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

//作业简单工厂，用于装载向前端返回的数据
@Component
public class HomeworkFactory {
    @Autowired
    private AnswerRepository answerRepository;
    public Map createHomeworkMap(Homework homework){
        Map data = new HashMap<>();
        data.put("homeworkId", homework.getHomeworkId());
        data.put("courseId", homework.getCourse().getCourseId());
        data.put("title", homework.getTitle());
        data.put("content", homework.getContent());
        data.put("start", CommonMethod.getLocalDateTimeString(homework.getStart()));
        data.put("end", CommonMethod.getLocalDateTimeString(homework.getEnd()));
        return data;
    }

    public Map createStudentHomeworkMap(Homework homework, Integer studentId){
        //先创建基本的作业信息
        Map data = createHomeworkMap(homework);
        Optional<Answer> aOp = answerRepository.findByHomeworkStudent(homework.getHomeworkId(), studentId);
        data.put("answer",null);
        if(aOp.isPresent()){
            Answer answer = aOp.get();
            Map aMap = new HashMap<>();
            aMap.put("answerId", answer.getAnswerId());
            aMap.put("homeworkId", homework.getHomeworkId());
            aMap.put("content", answer.getContent());
            data.put("answer", aMap);
        }
        return data;
    }

    public Map createHomeworkFileMap(HomeworkFile homeworkFile, String type){
        Map data = new HashMap<>();
        data.put("fileName", homeworkFile.getFileName());
        data.put("fileId", homeworkFile.getFileId());
        data.put("fileSize", homeworkFile.getFileSize());
        return data;
    }

    public Map createStudentAnswerFileMap(AnswerFile answerFile){
        Map data = new HashMap<>();
        data.put("fileName", answerFile.getFileName());
        data.put("fileSize", answerFile.getFileSize());
        data.put("fileId", answerFile.getFileId());
        return data;
    }
}
