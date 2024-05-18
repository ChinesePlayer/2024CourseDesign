package org.fatmansoft.teach.service;

import org.fatmansoft.teach.factory.CourseInfoFactory;
import org.fatmansoft.teach.models.Course;
import org.fatmansoft.teach.models.Score;
import org.fatmansoft.teach.models.Student;
import org.fatmansoft.teach.payload.request.DataRequest;
import org.fatmansoft.teach.payload.response.DataResponse;
import org.fatmansoft.teach.repository.CourseRepository;
import org.fatmansoft.teach.repository.ScoreRepository;
import org.fatmansoft.teach.util.CommonMethod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import javax.swing.text.html.Option;
import javax.validation.Valid;
import java.util.*;
import java.util.function.Consumer;

@Service
public class TeacherService {
    @Autowired
    private CourseRepository courseRepository;
    @Autowired
    private ScoreRepository scoreRepository;
    @Autowired
    private CourseInfoFactory courseInfoFactory;

    public DataResponse getStudents(@Valid @RequestBody DataRequest req){
        Integer courseId = req.getInteger("courseId");
        if(courseId == null){
            return CommonMethod.getReturnMessageError("获取课程ID失败");
        }
        List<Student> studentList = new ArrayList<>();
        List<Score> scoreList = scoreRepository.findByCourseId(courseId);
        //从Score对象中提取Student对象
        scoreList.forEach(score -> studentList.add(score.getStudent()));
        List<Map> dataList = new ArrayList<>();
        for(Student s:studentList){
            Map m = new HashMap<>();
            m.put("studentNum", s.getPerson().getPersonNum());
            m.put("studentName", s.getPerson().getPersonName());
            m.put("className", s.getClassName());
            m.put("studentId", s.getStudentId());
            List<Score> scoreL = scoreRepository.findByStudentCourse(s.getStudentId(), courseId);
            //肯定最多只有一条记录，取第一个
            Score score = null;
            if(!scoreL.isEmpty()){
                score = scoreL.get(0);
            }
            if(score != null){
                m.put("scoreId", score.getScoreId());
                m.put("mark", score.getMark());
                m.put("rank", score.getRank());
                m.put("status", score.getStatus());
            }
            else{
                m.put("scoreId", null);
                m.put("mark", null);
                m.put("rank", null);
                m.put("status", null);
            }
            dataList.add(m);
        }
        return CommonMethod.getReturnData(dataList);
    }

    public DataResponse saveStudentInfo(DataRequest req){
        Integer scoreId = req.getInteger("scoreId");
        Integer mark = req.getInteger("mark");
        Integer status = req.getInteger("status");
        if(scoreId == null){
            return CommonMethod.getReturnMessageError("无法获取成绩ID");
        }
        Optional<Score> scoreOp = scoreRepository.findById(scoreId);
        if(scoreOp.isEmpty()){
            return CommonMethod.getReturnMessageError("无法获取成绩");
        }
        Score score = scoreOp.get();
        Integer studentId = score.getStudent().getStudentId();
        Integer courseId = score.getCourse().getCourseId();
        //检查新的课程状态
        if(status == null){
            return CommonMethod.getReturnMessageError("无法获取课程状态");
        }
        List<Score> scoreList = scoreRepository.findByStudentCourse(studentId, courseId);
        //课程状态被调整为为已及格，删除所有其他的课程状态
        if(status == 1){
            scoreRepository.deleteAll(scoreList);
        }
        else{
            //删除所有已及格的课程实体类对象
            scoreList.forEach(score1 -> {
                if(score1.getStatus() == 1){
                    scoreRepository.delete(score1);
                }
            });
        }
        score.setMark(mark);
        score.setStatus(status);
        scoreRepository.save(score);
        return CommonMethod.getReturnMessageOK("保存成功");
    }

    public DataResponse getCourseList(DataRequest req){
        Integer teacherId = req.getInteger("teacherId");
        if(teacherId == null){
            return CommonMethod.getReturnMessageError("无法获取教师信息");
        }
        List<Course> courseList = courseRepository.findCoursesByTeacherId(teacherId);
        List<Map> dataList = new ArrayList<>();
        for(Course c : courseList){
            dataList.add(courseInfoFactory.createCourseInfo("teacher",c));
        }
        return CommonMethod.getReturnData(dataList);
    }
}
