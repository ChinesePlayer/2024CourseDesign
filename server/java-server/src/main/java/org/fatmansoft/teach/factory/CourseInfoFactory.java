package org.fatmansoft.teach.factory;

import org.fatmansoft.teach.models.*;
import org.fatmansoft.teach.repository.CompletionStatusRepository;
import org.fatmansoft.teach.repository.StudentRepository;
import org.fatmansoft.teach.repository.TeacherRepository;
import org.fatmansoft.teach.util.CommonMethod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

//简单工厂，用于获取不同场景下的课程数据
@Component
public class CourseInfoFactory {
    @Autowired
    private TeacherRepository teacherRepository;
    @Autowired
    private StudentRepository studentRepository;
    @Autowired
    private CompletionStatusRepository completionStatusRepository;

    public Map createCourseInfo(String type, Course c){
        Course pc;
        Map m = new HashMap<>();
        m.put("courseId", c.getCourseId()+"");
        m.put("courseNum",c.getNum());
        m.put("courseName",c.getName());
        m.put("credit",c.getCredit());
        m.put("coursePath",c.getCoursePath());
        if(c.getLocation() != null){
            Map locationMap = new HashMap<>();
            locationMap.put("locationId", c.getLocation().getLocationId() + "");
            locationMap.put("value", c.getLocation().getValue());
            m.put("location", locationMap);
        }
        else{
            m.put("location", null);
        }
        List<CourseTime> cts = c.getCourseTimes();
        List<Map> times = new ArrayList<>();
        for(CourseTime ct : cts){
            Map ctm = new HashMap<>();
            //统一转换成字符串
            ctm.put("id", ct.getCourseTimeId()+"");
            ctm.put("day",ct.getDay()+"");
            ctm.put("section", ct.getSection()+"");
            times.add(ctm);
        }
        m.put("times", times);
        if(c.getTeacher() != null){
            Optional<Teacher> teacherOptional = teacherRepository.findById(c.getTeacher().getTeacherId());
            if(teacherOptional.isPresent()){
                Map tm = new HashMap<>();
                Teacher teacher = teacherOptional.get();
                tm.put("name", teacher.getPerson().getName());
                tm.put("id", teacher.getTeacherId()+"");
                m.put("teacher", tm);
            }
            else {
                m.put("teacher",null);
            }
        }
        else{
            m.put("teacher", null);
        }
        pc =c.getPreCourse();
        if(pc != null) {
            //此处应该改在pc.getCourseId()之后再加一个""空字符串来讲preCourseId转化为字符串类型，否则会导致前端
            //在从json数据转化为Object数据时将该数据解析为Double
            m.put("preCourseId",pc.getCourseId()+"");
        }
        else{
            m.put("preCourseId",null);
        }

        switch (type){
            case "admin":{
                break;
            }
            case "student":{
                Integer userId = CommonMethod.getUserId();
                if(userId == null){
                    break;
                }
                Optional<Student> sOp = studentRepository.findByUserId(userId);
                if(sOp.isEmpty()){
                    break;
                }
                Student s = sOp.get();
                Optional<CompletionStatus> optionalCompletionStatus = completionStatusRepository.findCompletionStatusByStudentIdAndCourseId(s.getStudentId(), c.getCourseId());
                if(optionalCompletionStatus.isEmpty()){
                    break;
                }
                CompletionStatus cs = optionalCompletionStatus.get();
                m.put("status", cs.getStatus()+"");
            }
        }

        return m;
    }
}
