package org.fatmansoft.teach.service;

import org.fatmansoft.teach.models.*;
import org.fatmansoft.teach.payload.request.DataRequest;
import org.fatmansoft.teach.payload.response.DataResponse;
import org.fatmansoft.teach.repository.*;
import org.fatmansoft.teach.util.CommonMethod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Consumer;

@Service
public class AttendanceService {
    @Autowired
    private AttendanceRepository attendanceRepository;
    @Autowired
    private AttendanceService attendanceService;
    @Autowired
    private StudentRepository studentRepository;
    @Autowired
    private CourseRepository courseRepository;

    public List getAttendanceMapList(String courseName, String studentName) {
        List dataList = new ArrayList();
        List<Attendance> aList = attendanceRepository.findByCourseNameAndStudentName(courseName, studentName);  //根据课程名或课序号进行数据库查询
        if (aList == null || aList.size() == 0)
            return dataList;
        for (Attendance attendance : aList) {
            dataList.add(getMapFromAttendance(attendance));
        }
        return dataList;
    }

    public List getTeacherAttendanceMapList(String courseName, String studentName, Integer teacherId) {
        List dataList = new ArrayList();
        if(teacherId == null || teacherId <= 0){
            return dataList;
        }
        List<Attendance> aList = attendanceRepository.findByCourseNameAndStudentNameAndTeacherId(courseName, studentName, teacherId);  //根据课程名或课序号进行数据库查询
        if (aList == null || aList.size() == 0)
            return dataList;
        for (Attendance attendance : aList) {
            dataList.add(getMapFromAttendance(attendance));
        }
        return dataList;
    }

    public Map getMapFromAttendance(Attendance a) {
        Map m = new HashMap();
        m.put("attendanceId", a.getId());
        m.put("studentId",a.getStudent().getStudentId());
        m.put("courseId",a.getCourse().getCourseId());
        m.put("courseName",a.getCourse().getName());
        m.put("studentName", a.getStudent().getPerson().getName());
        m.put("courseNum", a.getCourse().getNum());
        m.put("studentNum", a.getStudent().getPerson().getNum());

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(CommonMethod.DATE_FORMAT);
        m.put("date",a.getDate().format(formatter));
        m.put("status",a.getStatus());
        return m;
    }

    public DataResponse getAttendanceList(DataRequest dataRequest) {
        String courseName = dataRequest.getString("courseName");
        String studentName = dataRequest.getString("studentName");
        List dataList = getAttendanceMapList(courseName,studentName);
        return CommonMethod.getReturnData(dataList);  
    }
    /** 获取所有出勤信息集合,前端initialize()初始化,onQueryButtonClick()查找中用到                            **/


    public DataResponse attendanceEditSave(DataRequest dataRequest) {
        Integer Id = dataRequest.getInteger("attendanceId");
        Integer courseId = dataRequest.getInteger("courseId");
        Integer studentId = dataRequest.getInteger("studentId");
        LocalDateTime date = CommonMethod.getDateTimeFromString(dataRequest.getString("date"));
        String status = dataRequest.getString("status");

        //首先检查学生和课程是否存在
        Optional<Student> sOp = studentRepository.findById(studentId);
        Optional<Course> cOp = courseRepository.findById(courseId);
        if(sOp.isEmpty()){
            return CommonMethod.getReturnMessageError("无法查询学生信息");
        }
        if(cOp.isEmpty()){
            return CommonMethod.getReturnMessageError("无法查询课程信息");
        }

        Student student = sOp.get();
        Course course = cOp.get();

        Optional<Attendance> op;
        Attendance a = null;
        if (Id != null) {
            op = attendanceRepository.findById(Id);  //查询对应数据库中主键为id的值的实体对象
            if (op.isPresent()) {
                a = op.get();
            }
        }
        if (a == null)
            a = new Attendance();

        a.setCourse(course);
        a.setStudent(student);
        a.setDate(date);
        a.setStatus(status);
        attendanceRepository.save(a);

        return CommonMethod.getReturnData(a.getId());
    }
    /** 处理考勤信息的保存和更新操作     **/



    public DataResponse getAttendanceInformation(DataRequest dataRequest) {
        Integer Id = dataRequest.getInteger("Id");
        Attendance a = null;
        Optional<Attendance> op;
        if (Id != null) {
            op = attendanceRepository.findById(Id);
            if (op.isPresent()) {
                a = op.get();
            }
        }
        return CommonMethod.getReturnData(attendanceService.getMapFromAttendance(a));
    }
    /** 根据提供的Id获取考勤信息，并返回相应的数据响应   **/

    public DataResponse attendanceDelete(DataRequest dataRequest) {
        Integer Id = dataRequest.getInteger("attendanceId");
        Optional<Attendance> op;
        Attendance a;
        if (Id != null) {
            op = attendanceRepository.findById(Id);   //查询获得实体对象
            if (op.isPresent()) {
                a= op.get();
                attendanceRepository.delete(a);
                return CommonMethod.getReturnMessageOK();  //通知前端操作正常
            }
        }
        return CommonMethod.getReturnMessageError("无法获取考勤信息");
    }
    /** 删除操作           **/


    public DataResponse getStudentAttendanceList(DataRequest req){
        Integer studentId = req.getInteger("studentId");
        Integer courseId = req.getInteger("courseId");
        LocalDateTime dateTime;

        String dateStr = req.getString("date");
        if(dateStr == null){
            dateTime = null;
        }
        else {
             dateTime = CommonMethod.getDateTimeFromString(dateStr);
        }

        if(studentId == null){
            return CommonMethod.getReturnMessageError("无法获取学生信息");
        }
        if(courseId == null){
            return CommonMethod.getReturnMessageError("无法获取课程信息");
        }
        List<Attendance> attendanceList = attendanceRepository.findByStudentIdCourseId(studentId, courseId);
        List<Attendance> rightAttendance;
        if(dateTime == null){
            rightAttendance = attendanceList;
        }
        else{
            rightAttendance = new ArrayList<>();
            attendanceList.forEach(attendance -> {
                if(attendance.getDate().equals(dateTime)){
                    rightAttendance.add(attendance);
                }
            });
        }

        //筛选符合日期条件的考勤对象
        List<Map> dataList = new ArrayList<>();
        for(Attendance a : rightAttendance){
            dataList.add(getMapFromAttendance(a));
        }
        return CommonMethod.getReturnData(dataList);
    }

    public DataResponse getTeacherAttendanceList(DataRequest req){
        Integer teacherId = req.getInteger("teacherId");
        if(teacherId == null){
            return CommonMethod.getReturnMessageError("无法获取老师信息");
        }
        String courseName = req.getString("courseName");
        String studentName = req.getString("studentName");
        List dataList = getTeacherAttendanceMapList(courseName,studentName, teacherId);
        return CommonMethod.getReturnData(dataList);
    }
}


