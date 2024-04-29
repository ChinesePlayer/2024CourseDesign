package org.fatmansoft.teach.service;

import org.fatmansoft.teach.models.Student;
import org.fatmansoft.teach.models.Teacher;
import org.fatmansoft.teach.models.User;
import org.fatmansoft.teach.models.UserType;
import org.fatmansoft.teach.repository.StudentRepository;
import org.fatmansoft.teach.repository.TeacherRepository;
import org.fatmansoft.teach.repository.UserRepository;
import org.fatmansoft.teach.repository.UserTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {
    @Autowired
    private StudentRepository studentRepository;
    @Autowired
    private TeacherRepository teacherRepository;

    //根据用户对象获取对应的角色ID
    public Integer getRoleId(User user){
        Integer userId = user.getUserId();
        if(userId == null){
            return  null;
        }
        UserType ut = user.getUserType();
        switch (ut.getName()){
            case ROLE_STUDENT:{
                Optional<Student> sOp = studentRepository.findByUserId(userId);
                if(sOp.isEmpty()){
                    return null;
                }
                Student s = sOp.get();
                return  s.getStudentId();
            }
            case ROLE_TEACHER:{
                Optional<Teacher> tOp = teacherRepository.findByUserId(userId);
                if(tOp.isEmpty()){
                    return null;
                }
                Teacher t = tOp.get();
                return t.getTeacherId();
            }
            default:{
                return null;
            }
        }
    }
}
