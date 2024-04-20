package org.fatmansoft.teach.service;

import org.apache.commons.compress.harmony.pack200.NewAttributeBands;
import org.apache.tomcat.jni.Local;
import org.fatmansoft.teach.models.Course;
import org.fatmansoft.teach.models.CourseSelectionTurn;
import org.fatmansoft.teach.models.Student;
import org.fatmansoft.teach.payload.request.DataRequest;
import org.fatmansoft.teach.payload.response.DataResponse;
import org.fatmansoft.teach.repository.CourseRepository;
import org.fatmansoft.teach.repository.CourseSelectionTurnRepository;
import org.fatmansoft.teach.repository.StudentRepository;
import org.fatmansoft.teach.util.CommonMethod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class CourseService {
    @Autowired
    private StudentRepository studentRepository;
    @Autowired
    private CourseRepository courseRepository;
    @Autowired
    private CourseSelectionTurnRepository turnRepository;

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

    //退选课程
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

    //获取所有课程列表
    public DataResponse getCourseList(DataRequest dataRequest){
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

    //获取选课的课程列表
    public DataResponse getCourseChoices(DataRequest dataRequest){
        //查询学生
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

        //查询选课轮次
        Integer turnId = dataRequest.getInteger("id");
        if(turnId == null){
            return CommonMethod.getReturnMessageError("无法查询当前选课轮次! ");
        }
        Optional<CourseSelectionTurn> turnOp = turnRepository.findById(turnId);
        if(!turnOp.isPresent()){
            return CommonMethod.getReturnMessageError("当前选课轮次不存在! ");
        }
        CourseSelectionTurn turn = turnOp.get();
        //查询当前选课轮次所有课程
        List<Course> cList = turnRepository.findCoursesByTurnId(turnId);

        Student student = studentOp.get();
        Integer studentId = student.getStudentId();
        //查询学生已选课程
        List<Course> chosenCourse = studentRepository.findCoursesByStudentId(studentId);

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
        labelChosenCourse(dataList, chosenCourse);
        return CommonMethod.getReturnData(dataList);
    }

    //选课
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
        //检查其前序课程是否修读，若未修读，则不允许选课
        Course preCourse = course.getPreCourse();
        if(!chosenCourse.contains(preCourse) && preCourse != null){
            return CommonMethod.getReturnMessageError("你尚未修读其前序课程: " + preCourse.getNum() + "-" + preCourse.getName() + " 无法选课! ");
        }
        student.getCourses().add(course);
        course.getStudents().add(student);

        studentRepository.saveAndFlush(student);
        return CommonMethod.getReturnMessageOK("选课成功!");
    }

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

    //查询当前学生所选的所有课程
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

    //查询所有选课轮次
    public DataResponse getAllTurns(DataRequest req){
        List<CourseSelectionTurn> turns = turnRepository.findAll();
        List<Map> data = new ArrayList<>();
        for(CourseSelectionTurn turn : turns){
            Map m = new HashMap<>();
            m.put("name", turn.getName());
            m.put("id", turn.getCourseSelectionId());
            m.put("start",turn.getStart());
            m.put("end", turn.getEnd());
            m.put("isValid", isTurnValid(turn));
            data.add(m);
        }
        return CommonMethod.getReturnData(data);
    }

    //根据传入的课程Id查询改Id对应的课程信息
    //返回的数据包含: courseId, name, num
    public DataResponse getCourse(DataRequest req){
        Integer id = req.getInteger("courseId");
        if(id == null){
            return CommonMethod.getReturnMessageError("无法获取课程ID! ");
        }
        Optional<Course> courseOptional = courseRepository.findById(id);
        if(!courseOptional.isPresent()){
            return CommonMethod.getReturnMessageError("无该课程! ");
        }
        Course course = courseOptional.get();
        Map map = new HashMap();
        map.put("courseId", course.getCourseId());
        map.put("name", course.getName());
        map.put("num", course.getNum());
        return CommonMethod.getReturnData(map);
    }

    //判断传入的选课轮次是否还可以用，即当前时间是否还在选课时间范围内
    private boolean isTurnValid(CourseSelectionTurn turn){
        LocalDateTime start = turn.getStart();
        LocalDateTime end = turn.getEnd();
        LocalDateTime now = LocalDateTime.now();
        if(now.isAfter(start) && now.isBefore(end)){
            return true;
        }
        return false;
    }

}
