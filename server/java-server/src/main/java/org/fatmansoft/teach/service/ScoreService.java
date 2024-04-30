package org.fatmansoft.teach.service;

import org.fatmansoft.teach.models.Course;
import org.fatmansoft.teach.models.Score;
import org.fatmansoft.teach.models.Student;
import org.fatmansoft.teach.payload.request.DataRequest;
import org.fatmansoft.teach.payload.response.DataResponse;
import org.fatmansoft.teach.repository.CourseRepository;
import org.fatmansoft.teach.repository.ScoreRepository;
import org.fatmansoft.teach.repository.StudentRepository;
import org.fatmansoft.teach.util.CommonMethod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.validation.Valid;
import java.util.*;

@Service
public class ScoreService {
    @Autowired
    private StudentRepository studentRepository;
    @Autowired
    private CourseRepository courseRepository;
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

    public DataResponse getStudentList(DataRequest req){
        List<Student> students = studentRepository.findAll();
        List<Map> dataList = new ArrayList<>();
        for(Student s : students){
            Map m = new HashMap<>();
            m.put("studentId", s.getStudentId()+"");
            m.put("studentName", s.getPerson().getName());
            m.put("studentNum", s.getPerson().getNum());
            m.put("personId", s.getPerson().getPersonId()+"");
            m.put("className", s.getClassName());
            dataList.add(m);
        }
        return CommonMethod.getReturnData(dataList);
    }

    //该方法用于获取课程的简单信息
    public DataResponse getCourseList(DataRequest req){
        List<Course> courses = courseRepository.findAll();
        List<Map> dataList = new ArrayList<>();
        for(Course c : courses){
            Map m = new HashMap<>();
            m.put("courseName", c.getName());
            m.put("courseId", c.getCourseId()+"");
            m.put("courseNum", c.getNum());
            m.put("credit", c.getCredit());
            dataList.add(m);
        }
        return CommonMethod.getReturnData(dataList);
    }
}
