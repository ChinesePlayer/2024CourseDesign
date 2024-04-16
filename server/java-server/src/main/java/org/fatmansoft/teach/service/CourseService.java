package org.fatmansoft.teach.service;

import org.fatmansoft.teach.models.Course;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class CourseService {
    //根据学生已选课程和来标记每个课程是否已被该学生选中
    //注意该方法会修改第一个参数!
    public void labelChosenCourse(List<Map> allCourse, List<Course> chosenCourse){
        System.out.println("开始标记! ");
        for(Map m : allCourse){
            m.put("isChosen", false);
            System.out.println("当前标记后的Map: " + m);
        }
        //TODO:时间复杂度较高，后续需要优化
        for(Course c : chosenCourse){
            Integer chosenCourseId = c.getCourseId();
            for(Map m : allCourse){
                if(m.get("courseId") == chosenCourseId){
                    m.put("isChosen", true);
                }

            }
        }
    }
}
