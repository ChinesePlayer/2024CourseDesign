package org.fatmansoft.teach.service;

import org.fatmansoft.teach.models.Activity;
import org.fatmansoft.teach.models.Person;
import org.fatmansoft.teach.payload.request.DataRequest;
import org.fatmansoft.teach.payload.response.DataResponse;
import org.fatmansoft.teach.repository.ActivityRepository;
import org.fatmansoft.teach.repository.PersonRepository;
import org.fatmansoft.teach.util.CommonMethod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.*;

@Service
public class ActivityService {
    @Autowired
    private ActivityRepository activityRepository;
    @Autowired
    private PersonRepository personRepository;

    public DataResponse getActivityList(DataRequest req){
        String activityName = req.getString("activityName");
        String directorName = req.getString("directorName");
        Integer status = req.getInteger("status");
        if(activityName == null){
            activityName = "";
        }
        if(directorName == null){
            directorName = "";
        }
        System.out.println(activityName);
        System.out.println(directorName);
        System.out.println(status);
        List<Activity> activityList = activityRepository.findByDirectorNameNameStatus(activityName, directorName, status);
        List<Map> dataList = new ArrayList<>();
        for(Activity a : activityList){
            dataList.add(getMapFromActivity(a));
        }
        return CommonMethod.getReturnData(dataList);
    }


    public DataResponse getPersonList(DataRequest req){
        List<Person> personList = personRepository.findAll();
        List<Map> dataList = new ArrayList<>();
        for(Person p : personList){
            dataList.add(getMapFromPerson(p));
        }
        return CommonMethod.getReturnData(dataList);
    }

    public DataResponse saveActivity(DataRequest req){
        Integer activityId = req.getInteger("activityId");
        Integer status = req.getInteger("status");
        LocalDate start = CommonMethod.getDateFromString(req.getString("start"),CommonMethod.DATE_FORMAT);
        LocalDate end = CommonMethod.getDateFromString(req.getString("end"),CommonMethod.DATE_FORMAT);
        Integer directorId = req.getInteger("directorId");
        String activityName = req.getString("activityName");
        Integer maxNum = req.getInteger("maxNumber");

        if(directorId == null){
            return CommonMethod.getReturnMessageError("无法获取负责人信息");
        }

        Optional<Person> pOp = personRepository.findById(directorId);
        if(pOp.isEmpty()){
            return CommonMethod.getReturnMessageError("无法获取负责人信息");
        }
        Person person = pOp.get();

        if(start.isBefore(LocalDate.now())){
            return CommonMethod.getReturnMessageError("开始时间不能早于现在时间! ");
        }

        Activity a = null;
        if(activityId != null){
            Optional<Activity> aOp = activityRepository.findById(activityId);
            if(aOp.isPresent()){
                a = aOp.get();
            }
        }

        if(a == null){
            a = new Activity();
        }

        a.setActivityName(activityName);
        a.setDirector(person);
        a.setStart(start);
        a.setEnd(end);
        a.setStatus(status);
        a.setMaxJoiner(maxNum);
        activityRepository.save(a);
        return CommonMethod.getReturnMessageOK();
    }

    public DataResponse deleteActivity(DataRequest req){
        Integer activityId = req.getInteger("activityId");
        if(activityId == null){
            return CommonMethod.getReturnMessageError("无法获取活动信息!");
        }
        Optional<Activity> aOp = activityRepository.findById(activityId);
        if(aOp.isEmpty()){
            return CommonMethod.getReturnMessageError("无法获取活动信息");
        }
        Activity activity = aOp.get();
        activityRepository.delete(activity);
        return CommonMethod.getReturnMessageOK();
    }

    //工具方法，用于将Activity对象中的信息提取到Map中
    private Map getMapFromActivity(Activity a){
        Map m = new HashMap<>();
        m.put("activityId", a.getActivityId());
        m.put("activityName", a.getActivityName());
        m.put("directorName",a.getDirector().getName());
        m.put("directorId", a.getDirector().getPersonId());
        m.put("status", a.getStatus());
        m.put("start", CommonMethod.getStringFromDate(a.getStart()));
        m.put("end", CommonMethod.getStringFromDate(a.getEnd()));
        m.put("number",a.getStudents().size());
        m.put("maxNumber", a.getMaxJoiner());
        return m;
    }

    //工具方法, 不需要获取所有信息，只需要获取活动功能所需要的Person信息
    private Map getMapFromPerson(Person p){
        Map m = new HashMap<>();
        m.put("personId", p.getPersonId());
        m.put("personName", p.getName());
        m.put("personNum", p.getNum());
        return m;
    }
}
