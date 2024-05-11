package org.fatmansoft.teach.service;

import org.fatmansoft.teach.factory.HomeworkFactory;
import org.fatmansoft.teach.models.Course;
import org.fatmansoft.teach.models.Homework;
import org.fatmansoft.teach.payload.request.DataRequest;
import org.fatmansoft.teach.payload.response.DataResponse;
import org.fatmansoft.teach.repository.CourseRepository;
import org.fatmansoft.teach.repository.HomeworkRepository;
import org.fatmansoft.teach.util.CommonMethod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;


@Service
public class HomeworkService {
    @Autowired
    private HomeworkRepository homeworkRepository;
    @Autowired
    private HomeworkFactory homeworkFactory;
    @Autowired
    private CourseRepository courseRepository;

    public DataResponse getHomeworkList(DataRequest req){
        Integer courseId = req.getInteger("courseId");
        if(courseId == null){
            return CommonMethod.getReturnMessageError("无法获取课程ID");
        }

        //寻找指定课程ID的作业
        List<Homework> homeworkList = homeworkRepository.findByCourseId(courseId);
        List<Map> dataList = new ArrayList<>();
        for (Homework h : homeworkList){
            dataList.add(homeworkFactory.createHomeworkMap(h,""));
        }
        return CommonMethod.getReturnData(dataList);
    }

    public DataResponse saveHomework(DataRequest req){
        Integer homeworkId = req.getInteger("homeworkId");
        Integer courseId = req.getInteger("courseId");
        String title = req.getString("title");
        String content = req.getString("content");
        String startStr = req.getString("start");
        String endStr = req.getString("end");

        if(courseId == null){
            return CommonMethod.getReturnMessageError("无法获取课程ID! ");
        }
        Optional<Course> cOp = courseRepository.findById(courseId);
        if(cOp.isEmpty()){
            return CommonMethod.getReturnMessageError("无法获取课程信息");
        }

        Course course = cOp.get();
        Homework homework = null;

        if(homeworkId == null){
            homework = new Homework();
        }
        else{
            Optional<Homework> hOp = homeworkRepository.findById(homeworkId);
            if(hOp.isEmpty()){
                return CommonMethod.getReturnMessageError("无法找到作业! ");
            }
            else{
                homework = hOp.get();
            }
        }
        homework.setTitle(title);
        homework.setContent(content);
        homework.setCourse(course);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(CommonMethod.DATE_FORMAT);
        LocalDateTime start = LocalDateTime.parse(startStr, formatter);
        LocalDateTime end = LocalDateTime.parse(endStr, formatter);

        homework.setStart(start);
        homework.setEnd(end);
        homeworkRepository.save(homework);

        return CommonMethod.getReturnMessageOK("保存成功");
    }
}
