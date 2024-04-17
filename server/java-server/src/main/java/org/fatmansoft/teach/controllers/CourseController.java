package org.fatmansoft.teach.controllers;

import org.fatmansoft.teach.models.Course;
import org.fatmansoft.teach.models.Score;
import org.fatmansoft.teach.models.Student;
import org.fatmansoft.teach.models.User;
import org.fatmansoft.teach.payload.request.DataRequest;
import org.fatmansoft.teach.payload.response.DataResponse;
import org.fatmansoft.teach.repository.CourseRepository;
import org.fatmansoft.teach.repository.PersonRepository;
import org.fatmansoft.teach.repository.StudentRepository;
import org.fatmansoft.teach.service.CourseService;
import org.fatmansoft.teach.util.CommonMethod;
import org.fatmansoft.teach.util.JsonConvertUtil;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CommentsDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import javax.swing.text.html.Option;
import javax.validation.Valid;
import javax.xml.crypto.Data;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectOutputStream;
import java.util.*;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/course")
public class CourseController {
    @Autowired
    private CourseRepository courseRepository;
    @Autowired
    private PersonRepository personRepository;
    @Autowired
    private StudentRepository studentRepository;
    @Autowired
    private CourseService courseService;

    @PostMapping("/getCourseList")
    public DataResponse getCourseList(@Valid @RequestBody DataRequest dataRequest) {
        String numName = dataRequest.getString("numName");
        if(numName == null)
            numName = "";
        List<Course> cList = courseRepository.findCourseListByNumName(numName);  //数据库查询操作
        List dataList = new ArrayList();
        Map m;
        Course pc;
        for (Course c : cList) {
            m = new HashMap();
            m.put("courseId", c.getCourseId()+"");
            m.put("num",c.getNum());
            m.put("name",c.getName());
            m.put("credit",c.getCredit());
            m.put("coursePath",c.getCoursePath());
            pc =c.getPreCourse();
            if(pc != null) {
                m.put("preCourse",pc.getName());
                //此处应该改在pc.getCourseId()之后再加一个""空字符串来讲preCourseId转化为字符串类型，否则会导致前端
                //在从json数据转化为Object数据时将该数据解析为Double
                m.put("preCourseId",pc.getCourseId()+"");
            }
            dataList.add(m);
            System.out.println("当前的课程打包信息: " + m.toString());
        }
        return CommonMethod.getReturnData(dataList);
    }

    @PostMapping("/cancelCourse")
    public DataResponse cancelCourse(@Valid @RequestBody DataRequest request){
        return courseService.cancelCourse(request);
    }

    //获取选课课程列表
    @PostMapping("/getCourseChoices")
    public DataResponse getCourseChoices(@Valid @RequestBody DataRequest dataRequest) {
        String numName = dataRequest.getString("numName");
        Integer userId = CommonMethod.getUserId();
        if(userId == null){
            return CommonMethod.getReturnMessageError("当前用户不存在! ");
        }
        if(numName == null)
            numName = "";

        Optional<Student> studentOp = studentRepository.findByUserId(userId);
        if(!studentOp.isPresent()){
            return CommonMethod.getReturnMessageError("该学生不存在! ");
        }
        Student student = studentOp.get();
        Integer studentId = student.getStudentId();
        //查询学生已选课程
        List<Course> chosenCourse = studentRepository.findCoursesByStudentId(studentId);
        //查询所有课程
        List<Course> cList = courseRepository.findCourseListByNumName(numName);
        List<Map> dataList = new ArrayList();
        Map m;
        Course pc;
        for (Course c : cList) {
            m = new HashMap();
            m.put("courseId", c.getCourseId()+"");
            m.put("num",c.getNum());
            m.put("name",c.getName());
            m.put("credit",c.getCredit());
            m.put("coursePath",c.getCoursePath());
            pc =c.getPreCourse();
            if(pc != null) {
                m.put("preCourse",pc.getName());
                //此处应该改在pc.getCourseId()之后再加一个""空字符串来讲preCourseId转化为字符串类型，否则会导致前端
                //在从json数据转化为Object数据时将该数据解析为Double
                m.put("preCourseId",pc.getCourseId()+"");
            }
            dataList.add(m);
            System.out.println("当前的课程打包信息: " + m.toString());
        }
        courseService.labelChosenCourse(dataList, chosenCourse);
        return CommonMethod.getReturnData(dataList);
    }

    //为发出请求的学生选课
    @PostMapping("/chooseCourse")
    @PreAuthorize("hasRole('ROLE_STUDENT')")
    public DataResponse choseCourse(@Valid @RequestBody DataRequest dataRequest) {
        Integer userId = CommonMethod.getUserId();
        if(userId == null){
            return CommonMethod.getReturnMessageError("用户不存在! ");
        }
        Optional<Student> studentOptional = studentRepository.findByUserId(userId);
        if(!studentOptional.isPresent()){
            return CommonMethod.getReturnMessageError("学生不存在!");
        }
        Student student = studentOptional.get();
        //从请求获取课程ID
        Integer courseId = Integer.parseInt(String.valueOf(dataRequest.getData().get("courseId")));
        if(courseId == null){
            return CommonMethod.getReturnMessageError("获取课程ID失败! ");
        }
        Optional<Course> courseOptional = courseRepository.findById(courseId);
        if(!courseOptional.isPresent()){
            return CommonMethod.getReturnMessageError("该课程不存在, 课程ID: " + courseId);
        }
        List<Course> chosenCourse = studentRepository.findCoursesByStudentId(student.getStudentId());
        Course course = courseOptional.get();
        if(chosenCourse.contains(course)){
            return CommonMethod.getReturnMessageError("当前课程已选中! ");
        }

        student.getCourses().add(course);
        course.getStudents().add(student);

        studentRepository.saveAndFlush(student);
        return CommonMethod.getReturnMessageOK("选课成功!");
    }

    @PostMapping("/courseSave")
    public DataResponse courseSave(@Valid @RequestBody DataRequest dataRequest) {
        Integer courseId = dataRequest.getInteger("courseId");
        String num = dataRequest.getString("num");
        String name = dataRequest.getString("name");
        String coursePath = dataRequest.getString("coursePath");
        Integer credit = dataRequest.getInteger("credit");
        Integer preCourseId = dataRequest.getInteger("preCourseId");
        Optional<Course> op;
        Course c= null;

        if(courseId != null) {
            op = courseRepository.findById(courseId);
            if(op.isPresent())
                c= op.get();
        }
        if(c== null)
            c = new Course();
        Course pc =null;
        if(preCourseId != null) {
            op = courseRepository.findById(preCourseId);
            if(op.isPresent())
                pc = op.get();
        }
        c.setNum(num);
        c.setName(name);
        c.setCredit(credit);
        c.setCoursePath(coursePath);
        c.setPreCourse(pc);
        courseRepository.save(c);
        return CommonMethod.getReturnMessageOK();
    }
    @PostMapping("/courseDelete")
    public DataResponse courseDelete(@Valid @RequestBody DataRequest dataRequest) {
        Integer courseId = dataRequest.getInteger("courseId");
        Optional<Course> op;
        Course c= null;
        if(courseId != null) {
            op = courseRepository.findById(courseId);
            if(op.isPresent()) {
                c = op.get();
                courseRepository.delete(c);
            }
        }
        return CommonMethod.getReturnMessageOK();
    }

    //查找当前学生所选的所有课程
    @PostMapping("/getStudentCourseList")
    @PreAuthorize("hasRole('ROLE_STUDENT')")
    public DataResponse getStudentCourseList(@Valid @RequestBody DataRequest dataRequest){
        Integer user = CommonMethod.getUserId();
        if(user == null){
            return CommonMethod.getReturnMessageError("当前用户不存在! ");
        }
        Optional<Student> studentOp = studentRepository.findByUserId(user);
        if(!studentOp.isPresent()){
            return CommonMethod.getReturnMessageError("当前学生不存在! ");
        }
        Student student = studentOp.get();
        //通过学生ID来找到该学生选择的所有课程
        List<Course> courseList = studentRepository.findCoursesByStudentId(student.getStudentId());
        return CommonMethod.getReturnData(courseList);
    }
}
