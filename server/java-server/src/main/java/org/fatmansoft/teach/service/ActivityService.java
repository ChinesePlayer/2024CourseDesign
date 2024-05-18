package org.fatmansoft.teach.service;

import org.fatmansoft.teach.models.Activity;
import org.fatmansoft.teach.models.Person;
import org.fatmansoft.teach.models.Student;
import org.fatmansoft.teach.payload.request.DataRequest;
import org.fatmansoft.teach.payload.response.DataResponse;
import org.fatmansoft.teach.repository.ActivityRepository;
import org.fatmansoft.teach.repository.PersonRepository;
import org.fatmansoft.teach.repository.StudentRepository;
import org.fatmansoft.teach.util.CommonMethod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import javax.swing.text.html.Option;
import javax.validation.Valid;
import java.security.Security;
import java.time.LocalDate;
import java.util.*;

@Service
public class ActivityService {
    @Autowired
    private ActivityRepository activityRepository;
    @Autowired
    private PersonRepository personRepository;
    @Autowired
    private StudentRepository studentRepository;

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
        List<Activity> activityList = activityRepository.findByDirectorNameNameStatus(activityName, directorName, status);
        List<Map> dataList = new ArrayList<>();
        for(Activity a : activityList){
            dataList.add(getMapFromActivity(a));
        }
        return CommonMethod.getReturnData(dataList);
    }

    public DataResponse getStudentActivityList(DataRequest req){
        String activityName = req.getString("activityName");
        String directorName = req.getString("directorName");
        Integer studentId = req.getInteger("studentId");
        if(studentId == null){
            return CommonMethod.getReturnMessageError("无法获取学生信息");
        }
        Optional<Student> sOp = studentRepository.findById(studentId);
        if(sOp.isEmpty()){
            return CommonMethod.getReturnMessageError("无法获取学生信息");
        }
        Student student = sOp.get();

        if(activityName == null){
            activityName = "";
        }
        if(directorName == null){
            directorName = "";
        }
        //直接查询已通过的活动
        List<Activity> activityList = activityRepository.findByDirectorNameNameStatus(activityName, directorName, 1);
        List<Map> dataList = new ArrayList<>();
        for(Activity a : activityList){
            Map m = getMapFromActivity(a);
            //将学生的报名信息加入其中
            m.put("isSignup",a.getStudents().contains(student));
            dataList.add(m);
        }
        return CommonMethod.getReturnData(dataList);
    }

    public DataResponse getStudentAppliedActivityList(DataRequest req){
        Integer studentId = req.getInteger("studentId");
        Integer status = req.getInteger("status");
        if(studentId == null){
            return CommonMethod.getReturnMessageError("无法获取学生信息");
        }
        Optional<Student> sOp = studentRepository.findById(studentId);
        if(sOp.isEmpty()){
            return CommonMethod.getReturnMessageError("无法获取学生信息");
        }
        Student student = sOp.get();
        List<Activity> activityList = activityRepository.findByPersonIdAndStatus(student.getPerson().getPersonId(),status);
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

    public DataResponse passActivity(DataRequest req){
        Integer activityId = req.getInteger("activityId");
        if(activityId == null){
            return CommonMethod.getReturnMessageError("无法获取活动信息");
        }
        Optional<Activity> aOp = activityRepository.findById(activityId);
        if(aOp.isEmpty()){
            return CommonMethod.getReturnMessageError("无法获取活动信息");
        }
        Activity activity = aOp.get();
        activity.setStatus(1);
        activityRepository.save(activity);
        return CommonMethod.getReturnMessageOK();
    }

    public DataResponse refuseActivity(DataRequest req){
        Integer activityId = req.getInteger("activityId");
        if(activityId == null){
            return CommonMethod.getReturnMessageError("无法获取活动信息");
        }
        Optional<Activity> aOp = activityRepository.findById(activityId);
        if(aOp.isEmpty()){
            return CommonMethod.getReturnMessageError("无法获取活动信息");
        }
        Activity activity = aOp.get();
        activity.setStatus(2);
        activityRepository.save(activity);
        return CommonMethod.getReturnMessageOK();
    }

    public DataResponse reapply(DataRequest req){
        Integer activityId = req.getInteger("activityId");
        if(activityId == null){
            return CommonMethod.getReturnMessageError("无法获取活动信息!");
        }
        Optional<Activity> aOp = activityRepository.findById(activityId);
        if(aOp.isEmpty()){
            return CommonMethod.getReturnMessageError("无法获取活动信息!");
        }
        Activity activity = aOp.get();
        //将申请状态设为审核中
        activity.setStatus(0);
        activityRepository.save(activity);
        return CommonMethod.getReturnMessageOK();
    }

    public DataResponse cancelApply(DataRequest req){
        Integer activityId = req.getInteger("activityId");
        if(activityId == null){
            return CommonMethod.getReturnMessageError("无法获取活动信息!");
        }
        Optional<Activity> aOp = activityRepository.findById(activityId);
        if(aOp.isEmpty()){
            return CommonMethod.getReturnMessageError("无法获取活动信息!");
        }
        Activity activity = aOp.get();
        //删除该活动申请
        activityRepository.delete(activity);
        return CommonMethod.getReturnMessageOK();
    }

    public DataResponse signup(DataRequest req){
        Integer studentId = req.getInteger("studentId");
        Integer activityId = req.getInteger("activityId");
        if(studentId == null){
            return CommonMethod.getReturnMessageError("无法获取学生信息");
        }
        if(activityId == null){
            return CommonMethod.getReturnMessageError("无法获取活动信息");
        }
        Optional<Student> sOp = studentRepository.findById(studentId);
        Optional<Activity> aOp = activityRepository.findById(activityId);
        if(sOp.isEmpty()){
            return CommonMethod.getReturnMessageError("无法获取学生信息");
        }
        if(aOp.isEmpty()){
            return CommonMethod.getReturnMessageError("无法获取活动信息");
        }

        Student student = sOp.get();
        Activity activity = aOp.get();
        //禁止加入活动人数已满的活动
        if(activity.getStudents().size()+1 > activity.getMaxJoiner()){
            return CommonMethod.getReturnMessageError("活动人数已满，无法报名!");
        }
        //禁止在过了结束日期后加入活动
        if(activity.getEnd().isBefore(LocalDate.now())){
            return CommonMethod.getReturnMessageError("活动已结束，无法报名!");
        }
        if(activity.getDirector().getPersonId() == student.getPerson().getPersonId()){
            return CommonMethod.getReturnMessageError("负责人不能加入自己的活动!");
        }

        activity.getStudents().add(student);
        activityRepository.save(activity);
        return CommonMethod.getReturnMessageOK();
    }

    public DataResponse getMySignup(DataRequest req){
        Integer studentId = req.getInteger("studentId");
        //活动进行状态:0: 未开始 1: 进行中 2:已结束
        Integer progressStatus = req.getInteger("progressStatus");
        if(studentId == null){
            return CommonMethod.getReturnMessageError("无法获取学生信息");
        }
        Optional<Student> sOp = studentRepository.findById(studentId);
        if(sOp.isEmpty()){
            return CommonMethod.getReturnMessageError("无法获取学生信息");
        }
        //首先查询该学生报名的所有活动
        List<Activity> activityList = activityRepository.findByStudentId(studentId);
        //检查符合活动进行状态的对象并将其加入到新的列表中
        List<Activity> filteredList = new ArrayList<>();
        //筛选未开始的活动
        if(progressStatus == null){
            filteredList = activityList;
        }
//        else if(progressStatus == 0){
//            for(Activity a : activityList){
//                if(a.getStart().isAfter(LocalDate.now())){
//                    filteredList.add(a);
//                }
//            }
//        }
//        else if(progressStatus == 1){
//            for(Activity a : activityList){
//                if(!LocalDate.now().isBefore(a.getStart()) && !LocalDate.now().isAfter(a.getEnd())){
//                    filteredList.add(a);
//                }
//            }
//        }
//        else if(progressStatus == 2){
//            for(Activity a : activityList){
//                if(LocalDate.now().isAfter(a.getEnd())){
//                    filteredList.add(a);
//                }
//            }
//        }
        //上述一堆代码可以直接通过以下代码简化:
        else{
            for(Activity a : activityList){
                if(Objects.equals(a.getProgressStatus(), progressStatus)){
                    filteredList.add(a);
                }
            }
        }
        List<Map> dataList = new ArrayList<>();
        for(Activity a : filteredList){
            dataList.add(getMapFromActivity(a));
        }
        return CommonMethod.getReturnData(dataList);
    }

    public DataResponse cancelSignup(DataRequest req){
        Integer studentId = req.getInteger("studentId");
        Integer activityId = req.getInteger("activityId");
        if(studentId == null){
            return CommonMethod.getReturnMessageError("无法获取学生信息");
        }
        if(activityId == null){
            return CommonMethod.getReturnMessageError("无法获取活动信息");
        }
        Optional<Student> sOp = studentRepository.findById(studentId);
        Optional<Activity> aOp = activityRepository.findById(activityId);
        if(sOp.isEmpty()){
            return CommonMethod.getReturnMessageError("无法获取学生信息");
        }
        if(aOp.isEmpty()){
            return CommonMethod.getReturnMessageError("无法获取活动信息");
        }

        Student student = sOp.get();
        Activity activity = aOp.get();

        //检查活动状态以确定能否取消报名
        if(activity.getProgressStatus() == 1){
            return CommonMethod.getReturnMessageError("活动已开始, 无法取消!");
        }
        else if(activity.getProgressStatus() == 2){
            return CommonMethod.getReturnMessageError("活动已结束");
        }
        else if(activity.getProgressStatus() == 0){
            activity.getStudents().remove(student);
            activityRepository.save(activity);
            return CommonMethod.getReturnMessageOK();
        }
        else{
            return CommonMethod.getReturnMessageError("错误: 非法的活动状态!");
        }
    }

    //工具方法，用于将Activity对象中的信息提取到Map中
    private Map getMapFromActivity(Activity a){
        Map m = new HashMap<>();
        m.put("activityId", a.getActivityId());
        m.put("activityName", a.getActivityName());
        m.put("directorName",a.getDirector().getPersonName());
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
        m.put("personName", p.getPersonName());
        m.put("personNum", p.getPersonNum());
        return m;
    }
}
