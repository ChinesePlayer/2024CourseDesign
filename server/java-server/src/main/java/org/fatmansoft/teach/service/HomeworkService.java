package org.fatmansoft.teach.service;

import org.fatmansoft.teach.factory.HomeworkFactory;
import org.fatmansoft.teach.models.Homework;
import org.fatmansoft.teach.payload.request.DataRequest;
import org.fatmansoft.teach.payload.response.DataResponse;
import org.fatmansoft.teach.repository.HomeworkRepository;
import org.fatmansoft.teach.util.CommonMethod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
public class HomeworkService {
    @Autowired
    private HomeworkRepository homeworkRepository;
    @Autowired
    private HomeworkFactory homeworkFactory;

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
}
