package org.fatmansoft.teach.service;

import org.apache.commons.compress.harmony.pack200.NewAttributeBands;
import org.fatmansoft.teach.models.Course;
import org.fatmansoft.teach.models.Student;
import org.fatmansoft.teach.payload.request.DataRequest;
import org.fatmansoft.teach.payload.response.DataResponse;
import org.fatmansoft.teach.repository.CourseRepository;
import org.fatmansoft.teach.repository.StudentRepository;
import org.fatmansoft.teach.util.CommonMethod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Service
public class CourseService {
    @Autowired
    private StudentRepository studentRepository;
    @Autowired
    private CourseRepository courseRepository;

    //根据学生已选课程和来标记每个课程是否已被该学生选中
    //注意该方法会修改第一个参数!
    public void labelChosenCourse(List<Map> allCourse, List<Course> chosenCourse){
        System.out.println("开始标记... ");
        for(Map m : allCourse){
            m.put("isChosen", false);
            System.out.println("当前标记后的Map: " + m);
        }
        //TODO:时间复杂度较高，后续需要优化
        for(Course c : chosenCourse){
            Integer chosenCourseId = c.getCourseId();
            System.out.println("当前目标已选课程: " + chosenCourseId);
            for(Map m : allCourse){
                System.out.println("当前判断的courseId: " + m.get("courseId") + "    该Id的类型为: " + m.get("courseId").getClass());
                if(Objects.equals(Integer.parseInt((String)m.get("courseId")), chosenCourseId)){
                    m.put("isChosen", true);
                }

            }
        }
    }

    public DataResponse cancelCourse(DataRequest request){
        Integer courseId = request.getInteger("courseId");
        if(courseId == null){
            return CommonMethod.getReturnMessageError("无法获取课程信息! ");
        }
        Optional<Course> courseOptional = courseRepository.findById(courseId);
        if(!courseOptional.isPresent()){
            return CommonMethod.getReturnMessageError("无法找到该课程! ");
        }
        Course course = courseOptional.get();
        Integer userId = CommonMethod.getUserId();
        if(userId == null){
            return CommonMethod.getReturnMessageError("无法获取用户信息! ");
        }
        Optional<Student> studentOptional = studentRepository.findByUserId(userId);
        if(!studentOptional.isPresent()){
            return CommonMethod.getReturnMessageError("当前学生不存在! ");
        }
        Student student = studentOptional.get();
        Integer studentId = student.getStudentId();
        List<Course> chosenCourse = studentRepository.findCoursesByStudentId(studentId);
        if(!chosenCourse.contains(course)){
            return CommonMethod.getReturnMessageError("无法退课: 尚未选中该课程! ");
        }
        student.getCourses().remove(course);
        course.getStudents().remove(student);

        studentRepository.saveAndFlush(student);
        chosenCourse = studentRepository.findCoursesByStudentId(studentId);
        return CommonMethod.getReturnMessageOK("退课成功! ");
    }
}
