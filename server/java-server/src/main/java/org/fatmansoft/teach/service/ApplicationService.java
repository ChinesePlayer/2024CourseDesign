package org.fatmansoft.teach.service;

import org.fatmansoft.teach.models.Application;
import org.fatmansoft.teach.models.ApplicationType;
import org.fatmansoft.teach.models.EApplicationType;
import org.fatmansoft.teach.models.Student;
import org.fatmansoft.teach.payload.request.DataRequest;
import org.fatmansoft.teach.payload.response.DataResponse;
import org.fatmansoft.teach.repository.ApplicationRepository;
import org.fatmansoft.teach.repository.ApplicationTypeRepository;
import org.fatmansoft.teach.repository.StudentRepository;
import org.fatmansoft.teach.util.CommonMethod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.*;

@Service
public class ApplicationService {
    @Autowired
    private ApplicationRepository applicationRepository;
    @Autowired
    private StudentRepository studentRepository;
    @Autowired
    private ApplicationTypeRepository applicationTypeRepository;

    public DataResponse getApplicationList(DataRequest req){
        Integer studentId = req.getInteger("studentId");
        Integer status = req.getInteger("status");
        List<Application> applicationList = applicationRepository.findByStudentIdAndStatus(studentId,status);
        List<Map> dataList = new ArrayList<>();
        for(Application a : applicationList){
            dataList.add(getMapFromApplication(a));
        }
        return CommonMethod.getReturnData(dataList);
    }

    public DataResponse saveApplication(DataRequest req){
        Integer studentId = req.getInteger("studentId");
        Integer applicationId = req.getInteger("applicationId");
        String type = req.getString("applicationType");
        String reason = req.getString("reason");
        Integer status = req.getInteger("status");
        LocalDate leaveDate = CommonMethod.getDateFromString(req.getString("leaveDate"),CommonMethod.DATE_FORMAT);
        LocalDate returnDate = CommonMethod.getDateFromString(req.getString("returnDate"),CommonMethod.DATE_FORMAT);

        if(studentId == null){
            return CommonMethod.getReturnMessageError("无法获取学生信息");
        }
        Optional<Student> sOp = studentRepository.findById(studentId);
        if(sOp.isEmpty()){
            return CommonMethod.getReturnMessageError("无法获取学生信息");
        }
        Student student = sOp.get();

        EApplicationType eType =EApplicationType.fromString(type);
        Optional<ApplicationType> atOp = applicationTypeRepository.findByType(eType);
        if(atOp.isEmpty()){
            return CommonMethod.getReturnMessageError("无法获取请假类型信息");
        }
        ApplicationType applicationType = atOp.get();

        Application application = null;
        if(applicationId != null){
            Optional<Application> aOp = applicationRepository.findById(applicationId);
            if(aOp.isPresent()){
                application = aOp.get();
            }
        }
        if(application == null){
            application = new Application();
        }
        application.setStudent(student);
        application.setApplicationType(applicationType);
        application.setReason(reason);
        application.setStatus(status == null?0:status);
        application.setLeaveDate(leaveDate);
        application.setReturnDate(returnDate);
        applicationRepository.save(application);
        return CommonMethod.getReturnMessageOK();
    }

    public DataResponse deleteApplication(DataRequest req){
        Integer applicationId = req.getInteger("applicationId");
        if(applicationId == null){
            return CommonMethod.getReturnMessageError("无法获取请假信息!");
        }
        Optional<Application> aOp = applicationRepository.findById(applicationId);
        if(aOp.isEmpty()){
            return CommonMethod.getReturnMessageError("无法获取请假信息!");
        }
        Application application = aOp.get();
        applicationRepository.delete(application);
        return CommonMethod.getReturnMessageOK();
    }

    public DataResponse destroyApplication(DataRequest req){
        Integer applicationId = req.getInteger("applicationId");
        if(applicationId == null){
            return CommonMethod.getReturnMessageError("无法获取请假信息!");
        }
        Optional<Application> aOp = applicationRepository.findById(applicationId);
        if(aOp.isEmpty()){
            return CommonMethod.getReturnMessageError("无法获取请假信息!");
        }
        Application application = aOp.get();
        //将状态设为已销假
        application.setStatus(3);
        applicationRepository.save(application);
        return CommonMethod.getReturnMessageOK();
    }

    public DataResponse allowApplication(DataRequest req){
        Integer applicationId = req.getInteger("applicationId");
        if(applicationId == null){
            return CommonMethod.getReturnMessageError("无法获取请假信息!");
        }
        Optional<Application> aOp = applicationRepository.findById(applicationId);
        if(aOp.isEmpty()){
            return CommonMethod.getReturnMessageError("无法获取请假信息!");
        }
        Application application = aOp.get();
        application.setStatus(1);
        applicationRepository.save(application);
        return CommonMethod.getReturnMessageOK();
    }

    public DataResponse refuseApplication(@Valid @RequestBody DataRequest req){
        Integer applicationId = req.getInteger("applicationId");
        if(applicationId == null){
            return CommonMethod.getReturnMessageError("无法获取请假信息!");
        }
        Optional<Application> aOp = applicationRepository.findById(applicationId);
        if(aOp.isEmpty()){
            return CommonMethod.getReturnMessageError("无法获取请假信息!");
        }
        Application application = aOp.get();
        application.setStatus(2);
        applicationRepository.save(application);
        return CommonMethod.getReturnMessageOK();
    }

    private Map getMapFromApplication(Application a){
        Map res = new HashMap<>();
        res.put("studentId",a.getStudent().getStudentId());
        res.put("studentName",a.getStudent().getPerson().getPersonName());
        res.put("applicationId",a.getApplicationId());
        res.put("reason",a.getReason());
        res.put("status",a.getStatus());
        res.put("leaveDate",CommonMethod.getStringFromDate(a.getLeaveDate()));
        res.put("returnDate",CommonMethod.getStringFromDate(a.getReturnDate()));
        res.put("applicationType",a.getApplicationType().getTypeName().toString());
        return  res;
    }
}
