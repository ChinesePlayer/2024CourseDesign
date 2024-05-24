package org.fatmansoft.teach.service;

import org.apache.pdfbox.pdmodel.interactive.action.PDActionLaunch;
import org.aspectj.apache.bcel.classfile.Module;
import org.fatmansoft.teach.models.EHonorType;
import org.fatmansoft.teach.models.Honor;
import org.fatmansoft.teach.models.HonorType;
import org.fatmansoft.teach.models.Student;
import org.fatmansoft.teach.payload.request.DataRequest;
import org.fatmansoft.teach.payload.response.DataResponse;
import org.fatmansoft.teach.repository.HonorRepository;
import org.fatmansoft.teach.repository.HonorTypeRepository;
import org.fatmansoft.teach.repository.StudentRepository;
import org.fatmansoft.teach.util.CommonMethod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;
import java.util.*;

@Service
public class HonorService {
    @Autowired
    private CommonMethod commonMethod;
    @Autowired
    private HonorRepository honorRepository;
    @Autowired
    private HonorTypeRepository honorTypeRepository;

    public DataResponse getHonors(DataRequest req){
        Student student = commonMethod.getStudentFromReq(req);
        if(student == null){
            return CommonMethod.getReturnMessageError("无法获取学生信息! ");
        }
        List<Honor> honors = honorRepository.findByStudentId(student.getStudentId());
        List<Map> dataList = new ArrayList<>();
        for(Honor h : honors){
            dataList.add(getMapFromHonor(h));
        }
        return CommonMethod.getReturnData(dataList);
    }

    public DataResponse getHonorCount(DataRequest req){
        return CommonMethod.getReturnData(honorRepository.count());
    }

    public DataResponse getHonorList(DataRequest req){
        Map form = req.getMap("form");
        String numName = CommonMethod.getString(form,"numName");
        String honorType = CommonMethod.getString(form,"honorType");
        EHonorType eHonorType = EHonorType.fromString(honorType);
        Integer pageIndex = req.getInteger("page");
        Integer pageSize = req.getInteger("pageSize");

        Pageable pageable = PageRequest.of(pageIndex,pageSize);
        System.out.println(numName);
        List<Honor> honorList = honorRepository.findByNumNameAndType(numName,eHonorType,pageable);
        List<Map> dataList = new ArrayList<>();
        for(Honor h : honorList){
            dataList.add(getMapFromHonor(h));
        }
        Map data = new HashMap<>();
        data.put("honorData", dataList);
        data.put("count",honorRepository.countByNumNameAndType(numName,eHonorType));
        return CommonMethod.getReturnData(data);
    }

    public DataResponse deleteHonor(DataRequest req){
        Integer honorId = req.getInteger("honorId");
        if(honorId == null){
            return CommonMethod.getReturnMessageError("无法获取荣誉信息");
        }
        Optional<Honor> hOp = honorRepository.findById(honorId);
        if (hOp.isEmpty())
        {
            return CommonMethod.getReturnMessageError("无法获取荣誉信息");
        }
        Honor honor = hOp.get();
        honorRepository.delete(honor);
        return CommonMethod.getReturnMessageOK();
    }

    public DataResponse saveHonor(DataRequest req){
        Integer honorId = req.getInteger("honorId");
        String honorContent = req.getString("honorContent");
        String honorType = req.getString("honorType");
        Student student = commonMethod.getStudentFromReq(req);
        if(student == null){
            return CommonMethod.getReturnMessageError("无法获取学生信息");
        }
        Honor honor = null;
        if(honorId != null){
            Optional<Honor> hOp = honorRepository.findById(honorId);
            if(hOp.isPresent()){
                honor = hOp.get();
            }
        }
        if(honor == null){
            honor = new Honor();
        }

        Optional<HonorType> tOp = honorTypeRepository.findByEHonorType(EHonorType.fromString(honorType));
        if(tOp.isEmpty()){
            return CommonMethod.getReturnMessageError("无法获取荣誉类型信息!");
        }
        HonorType type = tOp.get();

        honor.setHonorContent(honorContent);
        honor.setHonorType(type);
        honor.setStudent(student);
        honorRepository.save(honor);
        return CommonMethod.getReturnMessageOK();
    }

    private Map getMapFromHonor(Honor h){
        if(h == null){
            return null;
        }
        Map m = new HashMap<>();
        m.put("studentId",h.getStudent().getStudentId());
        m.put("studentName",h.getStudent().getPerson().getPersonName());
        m.put("studentNum",h.getStudent().getPerson().getPersonNum());
        m.put("honorType",h.getHonorType().getType().toString());
        m.put("honorContent",h.getHonorContent());
        m.put("honorId",h.getHonorId());
        return m;
    }
}
