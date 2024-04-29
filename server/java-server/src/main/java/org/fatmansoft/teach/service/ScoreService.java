package org.fatmansoft.teach.service;

import org.fatmansoft.teach.models.Score;
import org.fatmansoft.teach.models.Student;
import org.fatmansoft.teach.payload.request.DataRequest;
import org.fatmansoft.teach.payload.response.DataResponse;
import org.fatmansoft.teach.repository.ScoreRepository;
import org.fatmansoft.teach.repository.StudentRepository;
import org.fatmansoft.teach.util.CommonMethod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ScoreService {
    @Autowired
    private StudentRepository studentRepository;
    @Autowired
    private ScoreRepository scoreRepository;

    //获取当前学生的所有课程的所有成绩
    public DataResponse getScoreListForStudent(DataRequest req){
        Integer userId = CommonMethod.getUserId();
        if(userId == null){
            return CommonMethod.getReturnMessageError("无法查询当前用户信息");
        }
        Optional<Student> sOp = studentRepository.findByUserId(userId);
        if(sOp.isEmpty()){
            return CommonMethod.getReturnMessageError("无法查询当前学生信息");
        }
        Student student = sOp.get();
        List<Score> scoreList = scoreRepository.findByStudentStudentId(student.getStudentId());
        List<Map> dataList = new ArrayList<>();
        for(Score s : scoreList){
            Map m = new HashMap<>();
            m.put("scoreId", s.getScoreId()+"");
            m.put("courseId", s.getCourse().getCourseId()+"");
            m.put("studentId", s.getStudent().getStudentId()+"");
            dataList.add(m);
        }
        return CommonMethod.getReturnData(dataList);
    }
}
