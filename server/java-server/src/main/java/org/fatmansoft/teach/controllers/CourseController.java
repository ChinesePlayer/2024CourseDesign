package org.fatmansoft.teach.controllers;

import org.fatmansoft.teach.payload.request.DataRequest;
import org.fatmansoft.teach.payload.response.DataResponse;
import org.fatmansoft.teach.service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/course")
public class CourseController {
    @Autowired
    private CourseService courseService;

    @PostMapping("/getCourseList")
    public DataResponse getCourseList(@Valid @RequestBody DataRequest dataRequest) {
        return courseService.getCourseList(dataRequest);
    }

    @PostMapping("/courseSave")
    public DataResponse courseSave(@Valid @RequestBody DataRequest dataRequest) {
        return courseService.courseSave(dataRequest);
    }

    @PostMapping("/courseDelete")
    public DataResponse courseDelete(@Valid @RequestBody DataRequest dataRequest) {
        return courseService.courseDelete(dataRequest);
    }

    //退选课程
    @PostMapping("/cancelCourse")
    public DataResponse cancelCourse(@Valid @RequestBody DataRequest request){
        return courseService.cancelCourse(request);
    }

    //获取选课课程列表
    @PostMapping("/getCourseChoices")
    public DataResponse getCourseChoices(@Valid @RequestBody DataRequest dataRequest) {
        return courseService.getCourseChoices(dataRequest);
    }

    //为发出请求的学生选课
    @PostMapping("/chooseCourse")
    @PreAuthorize("hasRole('ROLE_STUDENT')")
    public DataResponse choseCourse(@Valid @RequestBody DataRequest dataRequest) {
        return courseService.choseCourse(dataRequest);
    }

    //查找当前学生所选的所有课程
    @PostMapping("/getStudentCourseList")
    @PreAuthorize("hasRole('ROLE_STUDENT')")
    public DataResponse getStudentCourseList(@Valid @RequestBody DataRequest dataRequest){
        return courseService.getStudentCourseList(dataRequest);
    }

    //获取所有选课轮次
    @PostMapping("/getAllTurns")
    public DataResponse getAllTurns(@Valid @RequestBody DataRequest req){
        return courseService.getAllTurns(req);
    }

    //根据ID返回某个课程的信息
    @PostMapping("/getCourse")
    public DataResponse getCourse(@Valid @RequestBody DataRequest req){
        return courseService.getCourse(req);
    }

    //根据学生ID，获取学生已选课程
    @PostMapping("/getChosenCourse")
    @PreAuthorize("hasRole('ROLE_STUDENT')")
    public DataResponse getChosenCourse(@Valid @RequestBody DataRequest req){
        return courseService.getChosenCourse(req);
    }
}
