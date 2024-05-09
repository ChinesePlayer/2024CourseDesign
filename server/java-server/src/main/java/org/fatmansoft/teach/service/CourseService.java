package org.fatmansoft.teach.service;

import org.fatmansoft.teach.factory.CourseInfoFactory;
import org.fatmansoft.teach.models.*;
import org.fatmansoft.teach.payload.request.DataRequest;
import org.fatmansoft.teach.payload.response.DataResponse;
import org.fatmansoft.teach.repository.*;
import org.fatmansoft.teach.util.CommonMethod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
    @Autowired
    private TeacherRepository teacherRepository;
    @Autowired
    private LocationRepository locationRepository;
    @Autowired
    private CourseTimeRepository courseTimeRepository;
    @Autowired
    private CompletionStatusRepository completionStatusRepository;
    @Autowired
    private CourseInfoFactory courseInfoFactory;

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
    //使用数据库事务提高性能
    @Transactional
    public DataResponse getCourseList(DataRequest dataRequest){
        String numName = dataRequest.getString("numName");
        if(numName == null)
            numName = "";
        List<Course> cList = courseRepository.findCourseListByNumName(numName);  //数据库查询操作
        List dataList = new ArrayList();
        for (Course c : cList) {
            dataList.add(loadCourseInfo(c));
        }
        return CommonMethod.getReturnData(dataList);
    }

    //获取选课的课程列表
    @Transactional
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
        for (Course c : cList) {
            dataList.add(loadCourseInfo(c));
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
        String name = dataRequest.getString("courseName");
        String coursePath = dataRequest.getString("coursePath");
        Double credit = dataRequest.getDouble("credit");
        Integer preCourseId = dataRequest.getInteger("preCourseId");
        Integer teacherId = dataRequest.getInteger("teacherId");
        Integer locationId = dataRequest.getInteger("locationId");
        List<Map> courseTimes = (ArrayList<Map>)dataRequest.get("times");
        System.out.println("Times: " + courseTimes);
        Optional<Course> op;
        Course c= null;

        //判断num课序号是否重复，若重复则拒绝修改
        //判断是新增课程还是修改课程，新增课程才执行num是否重复的判断
        if(courseId == null){
            Optional<Course> numCourse = courseRepository.findByNum(num);
            if(numCourse.isPresent()){
                return CommonMethod.getReturnMessageError("课序号已存在，请另设! ");
            }
        }

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
            if(op.isPresent()){
                pc = op.get();
                //检查课程链是否闭合，若闭合则拒绝修改
                if(!isRecycleCourseChain(c, pc)){
                    return CommonMethod.getReturnMessageError("前序课程不合法: 课程链形成闭环! ");
                }
            }
        }

        Teacher teacher = null;
        if(teacherId != null){
            Optional<Teacher> teacherOpn = teacherRepository.findById(teacherId);
            if(teacherOpn.isPresent()){
                teacher = teacherOpn.get();
            }
        }

        CourseLocation location = null;
        if(locationId != null){
            Optional<CourseLocation> lOp = locationRepository.findById(locationId);
            if(lOp.isPresent()){
                location = lOp.get();
            }
        }

        List<CourseTime> cts = courseTimeRepository.findCourseTimeByCourseId(courseId);
        courseTimeRepository.deleteAll(cts);
        courseTimeRepository.flush();
        List<CourseTime> newCts = new ArrayList<>();
        for(Map m : courseTimes){
            CourseTime ct = new CourseTime();
            ct.setDay((Integer) m.get("day"));
            ct.setSection((Integer) m.get("section"));
            ct.setCourse(c);
            c.getCourseTimes().add(ct);
            newCts.add(ct);

        }

        c.setNum(num);
        c.setName(name);
        c.setCredit(credit);
        c.setCoursePath(coursePath);
        c.setPreCourse(pc);
        c.setTeacher(teacher);
        c.setLocation(location);
        courseRepository.save(c);
        courseTimeRepository.saveAllAndFlush(newCts);
        Integer newId = c.getCourseId();
        Map m = new HashMap<>();
        m.put("courseId", newId+"");
        return CommonMethod.getReturnData(m);
    }

    public DataResponse courseDelete(@Valid @RequestBody DataRequest dataRequest) {
        Integer courseId = dataRequest.getInteger("courseId");
        Optional<Course> op;
        Course c= null;
        if(courseId != null) {
            op = courseRepository.findById(courseId);
            if(op.isPresent()) {
                c = op.get();
                //找到哪些课程将该课程设为了前序课程
                List<Course> nextCourses = courseRepository.findCoursesByPreCourseId(c.getCourseId());
                for(Course nextCourse : nextCourses){
                    //将它们的前序课程设为空
                    nextCourse.setPreCourse(null);
                }
                courseRepository.saveAll(nextCourses);
                courseRepository.delete(c);
                return CommonMethod.getReturnMessageOK();
            }
        }
        return CommonMethod.getReturnMessageError("课程不存在，无法删除! ");
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
        List<Map> dataList = new ArrayList<>();
        for(Course c : courseList){
            Map m = courseInfoFactory.createCourseInfo("student", c);
            dataList.add(m);
        }
        return CommonMethod.getReturnData(dataList);
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
        map.put("courseId", course.getCourseId()+"");
        map.put("courseName", course.getName());
        map.put("courseNum", course.getNum());
        map.put("credit",course.getCredit());
        map.put("coursePath", course.getCoursePath());
        if(course.getPreCourse() != null){
            map.put("preCourseId", course.getPreCourse().getCourseId()+"");
        }
        else{
            map.put("preCourseId", null);
        }
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

    //装填课程信息至一个Map
    private Map loadCourseInfo(Course c){
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
        return m;
    }

    public boolean isRecycleCourseChain(Course c, Course preCourse){
        if(preCourse == null){
            return true;
        }
        if(Objects.equals(c.getCourseId(), preCourse.getCourseId())){
            return false;
        }
        return isRecycleCourseChain(c, preCourse.getPreCourse());
    }

    public DataResponse getChosenCourse(DataRequest req){
        Integer studentId = req.getInteger("studentId");
        if (studentId == null) {
            return CommonMethod.getReturnMessageError("无法获取学生ID");
        }
        List<Course> chosenCourse = studentRepository.findCoursesByStudentId(studentId);
        List<Map> dataList = new ArrayList<>();
        if(chosenCourse.isEmpty()){
            //返回一个空列表
            return CommonMethod.getReturnData(dataList);
        }
        for(Course c : chosenCourse){
            dataList.add(courseInfoFactory.createCourseInfo("student", c));
        }
        return CommonMethod.getReturnData(dataList);
    }

    public DataResponse getTeacherCourseList(DataRequest req){
        Integer teacherId = req.getInteger("teacherId");
        if(teacherId == null){
            return CommonMethod.getReturnMessageError("无法获取老师ID");
        }
        List<Course> courses = courseRepository.findCoursesByTeacherId(teacherId);
        List<Map> dataList = new ArrayList<>();
        for(Course c : courses){
            dataList.add(courseInfoFactory.createCourseInfo("teacher", c));
        }
        return CommonMethod.getReturnData(dataList);
    }

}
