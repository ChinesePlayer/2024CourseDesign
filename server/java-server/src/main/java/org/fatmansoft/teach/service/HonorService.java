package org.fatmansoft.teach.service;

import org.fatmansoft.teach.models.Honor;
import org.fatmansoft.teach.models.Student;
import org.fatmansoft.teach.payload.request.DataRequest;
import org.fatmansoft.teach.payload.response.DataResponse;
import org.fatmansoft.teach.repository.HonorRepository;
import org.fatmansoft.teach.repository.StudentRepository;
import org.fatmansoft.teach.util.CommonMethod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class HonorService {
    @Autowired
    private BaseService baseService;
    @Autowired
    private HonorRepository honorRepository;
    public DataResponse getHonors(DataRequest request){
        Student student = baseService.getStudent();
        if(student == null){
            return CommonMethod.getReturnMessageError("该学生不存在! ");
        }
        List<Honor> honors = honorRepository.findByStudentId(student.getStudentId());
        List<Map> dataList = new ArrayList<>();
        for(Honor h : honors){
            Map m = new HashMap<>();
            m.put("honorId", h.getHonorId());
            m.put("honorContent", h.getHonorContent());
            m.put("honorType", h.getHonorType().getType().toString());
            dataList.add(m);
        }
        return CommonMethod.getReturnData(dataList);
    }
}
