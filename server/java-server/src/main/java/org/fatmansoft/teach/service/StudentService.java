package org.fatmansoft.teach.service;

import org.fatmansoft.teach.models.Person;
import org.fatmansoft.teach.models.Student;
import org.fatmansoft.teach.payload.request.DataRequest;
import org.fatmansoft.teach.payload.response.DataResponse;
import org.fatmansoft.teach.repository.StudentRepository;
import org.fatmansoft.teach.util.ComDataUtil;
import org.fatmansoft.teach.util.CommonMethod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class StudentService {
    @Autowired
    private StudentRepository studentRepository;

    public Map getMapFromStudent(Student s) {
        Map m = new HashMap();
        Person p;
        if(s == null)
            return m;
        m.put("major",s.getMajor());
        m.put("className",s.getClassName());
        p = s.getPerson();
        if(p == null)
            return m;
        m.put("studentId", s.getStudentId());
        m.put("personId", p.getPersonId());
        m.put("num",p.getNum());
        m.put("name",p.getName());
        m.put("dept",p.getDept());
        m.put("card",p.getCard());
        String gender = p.getGender();
        m.put("gender",gender);
        m.put("genderName", ComDataUtil.getInstance().getDictionaryLabelByValue("XBM", gender)); //性别类型的值转换成数据类型名
        m.put("birthday", p.getBirthday());  //时间格式转换字符串
        m.put("email",p.getEmail());
        m.put("phone",p.getPhone());
        m.put("address",p.getAddress());
        m.put("introduce",p.getIntroduce());
        return m;
    }

    public DataResponse getStudentDashboardInfo(DataRequest req){
        Integer studentId = req.getInteger("studentId");
        if(studentId == null){
            return CommonMethod.getReturnMessageError("无法获取学生ID");
        }
        Optional<Student> stuOp = studentRepository.findById(studentId);
        if(stuOp.isEmpty()){
            return CommonMethod.getReturnMessageError("无效的学生ID");
        }
        Student student = stuOp.get();
        Map data = new HashMap<>();
        data.put("studentId", studentId);
        data.put("studentNum", student.getPerson().getNum());
        data.put("studentName", student.getPerson().getName());
        data.put("studentDept", student.getPerson().getDept());
        //TODO: 暂时先返回这些数据，后续会逐渐增加
        return CommonMethod.getReturnData(data);
    }



}
