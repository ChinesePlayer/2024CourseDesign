package org.fatmansoft.teach.controllers;

import org.fatmansoft.teach.models.Activity;
import org.fatmansoft.teach.models.Person;
import org.fatmansoft.teach.models.PersonActivity;
import org.fatmansoft.teach.models.User;
import org.fatmansoft.teach.payload.request.DataRequest;
import org.fatmansoft.teach.payload.response.DataResponse;
import org.fatmansoft.teach.repository.ActivityRepository;
import org.fatmansoft.teach.repository.PersonActivityRepository;
import org.fatmansoft.teach.repository.PersonRepository;
import org.fatmansoft.teach.repository.UserRepository;
import org.fatmansoft.teach.util.ComDataUtil;
import org.fatmansoft.teach.util.CommonMethod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.*;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/activity")
public class ActivityController {
    @Autowired
    private ActivityRepository activityRepository;
    @Autowired
    private PersonActivityRepository personActivityRepository;
    @Autowired
    private PersonRepository personRepository;
    @Autowired
    private UserRepository userRepository;

    public synchronized Integer getNewActivityId() {
        Integer id = activityRepository.getMaxId();  // 查询最大的id
        if (id == null) id = 1;
        else id = id + 1;
        return id;
    }
//
    public Map getMapFromActivity(Activity s) {
        Map m = new HashMap();
        if (s == null) return m;
        m.put("activityId", s.getActivityId());
        m.put("name", s.getName());
        m.put("address", s.getAddress());
        m.put("volunteerTime", s.getVolunteerTime());
        Person st = s.getPerson();
        if (st == null) {
            m.put("studentNum", "不存在这个人，程序有bug,怎么让这条数据存进来了");
        } else {
            m.put("studentNum", st.getNum());
            m.put("personName", st.getName());
        }
        m.put("joinedPeople", s.getJoinedPeople());
        m.put("leftPeopleRemaining", s.getLeftPeopleRemaining());
        m.put("activityOrganizeUnit", s.getActivityOrganizeUnit());
        m.put("qualityDevelopmentCredit", s.getQualityDevelopmentCredit());
        m.put("timeStart", s.getTimeStart());
        m.put("timeEnd", s.getTimeEnd());
        m.put("HDSPState_name", ComDataUtil.getInstance().getDictionaryLabelByValue("HDSP", s.getState()));
        return m;
    }

    public Map getMap2FromActivity(Activity s) {
        Map m = new HashMap();
        if (s == null) {
            return m;
        }
        //基本信息
        m.put("activityId", s.getActivityId());
        m.put("name", s.getName());
        m.put("joinedPeople", s.getJoinedPeople());
        //时间信息
        m.put("timeStart", s.getTimeStart());
        m.put("timeEnd", s.getTimeEnd());
        //参与人员信息
        List<PersonActivity> saList = personActivityRepository.findPersonActivityListByActivityId(s.getActivityId());
        List dataList = new ArrayList();
        for (PersonActivity sa : saList) {
            dataList.add(sa.getPerson());
        }
        m.put("personnel", dataList);
        return m;
    }

    public Map getMap3FromPersonId(Integer personId) {
        Map m = new HashMap();
        Optional<Person> s0p = personRepository.findById(personId);
        if (s0p.isEmpty()) {
            return m;
        }
        List<PersonActivity> saList = personActivityRepository.findByPersonPersonId(personId);//由这个学生得到这个学生参加的所有学生活动
        for (PersonActivity sa : saList) {//sa是该学生参加的活动  现在要获得这个活动对应的所有参与人员
            List<PersonActivity> psList = personActivityRepository.findPersonActivityListByActivityId(sa.getActivity().getActivityId());
            List personnelList = new ArrayList();
            for (PersonActivity ps : psList) {
                personnelList.add(ps.getPerson());
            }
            m.put(sa.getActivity(), personnelList);
        }
        return m;
    }

    public Map getMapFromPersonActivity(PersonActivity sc) {
        Map m = new HashMap();
        if (sc == null) return m;
        m.put("studentActivityId", sc.getPersonActivityId());
        m.put("attendance", sc.isAttendance());
        Map activity = getMapFromActivity(sc.getActivity());
        m.put("activity", activity);
        m.put("State_name", ComDataUtil.getInstance().getDictionaryLabelByValue("HDSPState", sc.getState()));
        m.put("studentNum", sc.getPerson().getNum());
        return m;
    }

    public List getPersonFromActivity(Integer ActivityId) {
        List dataList = new ArrayList();
        List<PersonActivity> scList = personActivityRepository.findByActivityActivityId(ActivityId);
        if (scList == null || scList.size() == 0) return dataList;
        for (PersonActivity personActivity : scList) {
            dataList.add(getMapFromPersonActivity(personActivity));
        }
        return dataList;
    }

    //空输入就是查询所有
    public List getActivityMapList(Integer activityId) {
        List dataList = new ArrayList();
        List<Activity> sList = activityRepository.findActivityListByActivityId(activityId);
        for (Activity activity : sList) {
            dataList.add(getMapFromActivity(activity));
        }
        return dataList;
    }

    @PostMapping("/initialize")
    public DataResponse getAllActivityList(@Valid @RequestBody DataRequest dataRequest) {
        Integer activityId = dataRequest.getInteger("activityId");
        List dataList = getActivityMapList(activityId);
        return CommonMethod.getReturnData(dataList);
    }

    @PostMapping("/getStudentId")
    public DataResponse getPersonId() {
        Integer userId = CommonMethod.getUserId();
        Optional<User> uOp = userRepository.findByUserId(userId);  // 查询获得 user对象
        if (uOp.isEmpty()) return CommonMethod.getReturnMessageError("用户不存在！");
        User u = uOp.get();
        Optional<Person> sOp = personRepository.findById(u.getPerson().getPersonId());
        if (sOp.isEmpty()) return CommonMethod.getReturnMessageError("此人不存在！");
        Person s = sOp.get();
        Integer personId = s.getPersonId();
        Map m = new HashMap();
        m.put("studentId", personId);
        return CommonMethod.getReturnData(personId);
    }

    @PostMapping("/getStudentNumAndName")
    public DataResponse getPersonNum() {
        Integer userId = CommonMethod.getUserId();
        Optional<User> uOp = userRepository.findByUserId(userId);  // 查询获得 user对象
        if (uOp.isEmpty()) return CommonMethod.getReturnMessageError("用户不存在！");
        User u = uOp.get();
        Optional<Person> sOp = personRepository.findById(u.getPerson().getPersonId());
        if (sOp.isEmpty()) return CommonMethod.getReturnMessageError("此人不存在！");
        Person s = sOp.get();
        Map m = new HashMap();
        m.put("studentNum", s.getNum());
        m.put("name", s.getName());
        return CommonMethod.getReturnData(m);
    }

    @PostMapping("/getActivityTimeAndPersonnel")
    //查：可以查看某活动的具体信息
    //1. 具体信息：活动时间和已确认参与人员信息
    //包含了活动时间和参与人员信息"activityId" "name"  "joinedPeople"  "time"(用list存)  （新）personnel（用list存）
    //包含了该学生参加的活动  和对应活动的参与人员信息
    public DataResponse getActivityTimeAndPersonnel(@Valid @RequestBody DataRequest dataRequest) {
        Integer activityId = dataRequest.getInteger("activityId");
        Activity c = null;
        if (activityId != null) {
            Optional<Activity> c0p = activityRepository.findById(activityId);
            if (c0p.isEmpty()) {
                return CommonMethod.getReturnMessageError("没有这个活动");
            }
            c = c0p.get();
        }
        return CommonMethod.getReturnData(getMap2FromActivity(c));
    }

    @PostMapping("/getActivityAndPersonnel")
    //可以通过学生姓名学号/教师姓名工号查询这位用户的活动参与情况（作为**成员**或者**主管**都包括在内）
    //根据前端需求，输入personId，输出他参与的活动和该活动的参与人员
    public DataResponse getActivityAndPersonnel(@Valid @RequestBody DataRequest dataRequest) {
        Integer personId = dataRequest.getInteger("studentId");
        Map m = getMap3FromPersonId(personId);
        return CommonMethod.getReturnData(m);
    }

    @PostMapping("/getActivityMap")
    public DataResponse getActivityMap(@Valid @RequestBody DataRequest dataRequest) {
        Integer activityId = dataRequest.getInteger("activityId");
        if (activityId == null) activityId = 0;
        Activity sc = activityRepository.findByActivityId(activityId).get();
        Map m = getMapFromActivity(sc);
        return CommonMethod.getReturnData(m);
    }

    @PostMapping("/getActivity")
    public DataResponse getActivity(@Valid @RequestBody DataRequest dataRequest) {
        String personNum = dataRequest.getString("studentNum");
        Optional<Person> s0p = personRepository.findByNum(personNum);
        if (s0p.isEmpty()) {
            return CommonMethod.getReturnMessageError("不存在这个学生，请重新输入！");
        }
        Person s = s0p.get();
        List<Activity> aList = activityRepository.findActivityListByPersonNum(s.getNum());
        List dataList = new ArrayList();
        Map m;
        for (Activity a : aList) {
            m = getMapFromActivity(a);
            dataList.add(m);
        }
        return CommonMethod.getReturnData(dataList); //这里回传包含活动信息的Map对象
    }

    @PostMapping("/getFuzzyActivity")
    public DataResponse getFuzzyActivity(@Valid @RequestBody DataRequest dataRequest) {
        String personNum = dataRequest.getString("studentNumAndName");
        List<Activity> aList = activityRepository.findFuzzyActivityListByPersonNumAndName(personNum);
        List dataList = new ArrayList();
        Map m;
        for (Activity a : aList) {
            m = getMapFromActivity(a);
            dataList.add(m);
        }
        return CommonMethod.getReturnData(dataList);
    }

    //给前端用活动审批状态查询内容 活动详细信息  包括  审核状态
    //有：admin管理（增删改查）所有活动列表（Activity）  student和teacher管理（改删查）以自己为主管的某活动
    //查：可以**只显示待审批**
    @PostMapping("/getActivityByState")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public DataResponse getLeaveInfoByState(@Valid @RequestBody DataRequest dataRequest) {
        String HDSPState = dataRequest.getString("State");
        List<Activity> leList = activityRepository.findActivityListByState(HDSPState);
        List dataList = new ArrayList();
        Map m;
        for (Activity le : leList) {
            m = getMapFromActivity(le);
            dataList.add(m);
        }
        return CommonMethod.getReturnData(dataList);
    }

    //前端  人员表格初始化 返回studentActivity的详细信息
    @PostMapping("/getStudentActivityByActivityId")
    public DataResponse getPersonActivityByActivityId(@Valid @RequestBody DataRequest dataRequest) {
        Integer activityId = dataRequest.getInteger("activityId");
        List dataList = getPersonFromActivity(activityId);
        return CommonMethod.getReturnData(dataList);
    }
    //前端  活动初始化  返回所有活动

    @PostMapping("/addActivity")
    //通过此方式创建的活动状态为**已通过**
    public DataResponse addActivity(@Valid @RequestBody DataRequest dataRequest) {
        String personNum = dataRequest.getString("studentNum");//主办人的studentNum
        Map form = dataRequest.getMap("form");
        Person s = null;
        Optional<Person> s0p;
        Activity ac;
        ac = new Activity();
        if (personNum != null) {
            s0p = personRepository.findByNum(personNum);
            if (s0p.isPresent()) {
                s = s0p.get();
            } else {
                return CommonMethod.getReturnMessageError("没有这个主办人，无法创建活动！");
            }
        }
        Integer newActivityId = getNewActivityId();

        ac.setActivityId(newActivityId);
        String cName = CommonMethod.getString(form, "name");
        if (cName.equals("")) {
            return CommonMethod.getReturnMessageError("活动名不能为空");
        }
        ac.setName(cName);
        ac.setAddress(CommonMethod.getString(form, "address"));
        ac.setVolunteerTime(CommonMethod.getInteger(form, "volunteerTime"));
        ac.setPerson(s);
        ac.setJoinedPeople(CommonMethod.getInteger(form, "joinedPeople"));
        ac.setActivityOrganizeUnit(CommonMethod.getString(form, "activityOrganizeUnit"));
        ac.setQualityDevelopmentCredit(CommonMethod.getInteger(form, "qualityDevelopmentCredit"));
        ac.setTimeStart(CommonMethod.getString(form, "timeStart"));
        ac.setTimeEnd(CommonMethod.getString(form, "timeEnd"));
        ac.setLeftPeopleRemaining(CommonMethod.getInteger(form, "joinedPeople"));
        ac.setState("2");

        activityRepository.saveAndFlush(ac);
        return CommonMethod.getReturnData(ac.getActivityId());
    }

    //自行申请添加以自己为主管的某活动
    //1. 此时申请的状态为**待审批**状态，需要**admin**端审批通过
    @PostMapping("/addActivity2")
    //通过此方式创建的活动状态为**已通过**
    public DataResponse addActivity2(@Valid @RequestBody DataRequest dataRequest) {
        String personNum = dataRequest.getString("studentNum");
        Map form = dataRequest.getMap("form");
        Person s = null;
        Optional<Person> s0p;
        Activity ac;
        ac = new Activity();
        if (personNum != null) {
            s0p = personRepository.findByNum(personNum);
            if (s0p.isPresent()) {
                s = s0p.get();
            } else {
                return CommonMethod.getReturnMessageError("没有这个主办人，无法创建活动！");
            }
        }
        Integer newActivityId = getNewActivityId();

        ac.setActivityId(newActivityId);
        String cName = CommonMethod.getString(form, "name");
        if (cName.equals("")) {
            return CommonMethod.getReturnMessageError("活动名不能为空");
        }
        ac.setName(cName);
        ac.setAddress(CommonMethod.getString(form, "address"));
        ac.setVolunteerTime(CommonMethod.getInteger(form, "volunteerTime"));
        ac.setPerson(s);
        ac.setJoinedPeople(CommonMethod.getInteger(form, "joinedPeople"));
        ac.setActivityOrganizeUnit(CommonMethod.getString(form, "activityOrganizeUnit"));
        ac.setQualityDevelopmentCredit(CommonMethod.getInteger(form, "qualityDevelopmentCredit"));
        ac.setLeftPeopleRemaining(CommonMethod.getInteger(form, "joinedPeople"));
        ac.setTimeStart(CommonMethod.getString(form, "timeStart"));
        ac.setTimeEnd(CommonMethod.getString(form, "timeEnd"));
        ac.setState("1");
        //ac.setState("2");
        activityRepository.save(ac);
        return CommonMethod.getReturnData(ac.getActivityId());
    }

    @PostMapping("/editActivity")
    public DataResponse editActivity(@Valid @RequestBody DataRequest dataRequest) {
        Integer activityId = dataRequest.getInteger("activityId");
        Map form = dataRequest.getMap("form");
        Activity c = null;
        Optional<Activity> c0p;
        if (activityId != null) {
            c0p = activityRepository.findById(activityId);
            if (c0p.isPresent()) {
                c = c0p.get();
            }
        }
        if (c == null) {
            Integer newActivityId = getNewActivityId();
            c = new Activity();
            c.setActivityId(newActivityId);
        }

        String cName = CommonMethod.getString(form, "name");
        if (cName.equals("")) {
            return CommonMethod.getReturnMessageError("活动名不能为空");
        }
        c.setName(cName);

        Person person;
        Integer personId = CommonMethod.getInteger(form, "studentId");
        if (personId != null) {
            person = personRepository.getById(personId);
            c.setPerson(person);
        }
        c.setJoinedPeople(CommonMethod.getInteger(form, "joinedPeople"));
        c.setLeftPeopleRemaining(c.getJoinedPeople());
        c.setAddress(CommonMethod.getString(form, "address"));
        c.setActivityOrganizeUnit(CommonMethod.getString(form, "activityOrganizeUnit"));
        c.setQualityDevelopmentCredit(CommonMethod.getInteger(form, "qualityDevelopmentCredit"));
        c.setTimeStart(CommonMethod.getString(form, "timeStart"));
        c.setTimeEnd(CommonMethod.getString(form, "timeEnd"));
        c.setVolunteerTime(CommonMethod.getInteger(form,"volunteerTime"));
        activityRepository.saveAndFlush(c);
        return CommonMethod.getReturnData(c.getActivityId());
    }

    @PostMapping("/deleteActivity")
    public DataResponse deleteActivity(@Valid @RequestBody DataRequest dataRequest) {
        Integer activityId = dataRequest.getInteger("activityId");
        Activity c = null;
        Optional<Activity> sc0p;
        if (activityId != null) {
            sc0p = activityRepository.findByActivityId(activityId);
            if (sc0p.isPresent()) {
                c = sc0p.get();
            } else return CommonMethod.getReturnMessageError("活动不存在无法删除");
        }
        if (c != null) {
            List<PersonActivity> scList = personActivityRepository.findByActivityActivityId(activityId);
            if (!scList.isEmpty()) {
                for (PersonActivity ct : scList) {
                    personActivityRepository.delete(ct);
                }
            }
            activityRepository.delete(c);
        }
        return CommonMethod.getReturnMessageOK();
    }

    @PostMapping("/modifyState")
    public DataResponse modifyState(@Valid @RequestBody DataRequest dataRequest) {
        Integer activityId = dataRequest.getInteger("activityId");
        String state = dataRequest.getString("state");
        Optional<Activity> sc0p = activityRepository.findById(activityId);
        Activity c;
        if (sc0p.isEmpty()) {
            return CommonMethod.getReturnMessageError("输入有误，没有这个活动！");
        } else {
            c = sc0p.get();
        }
        if (state.equals("1")) {
            return CommonMethod.getReturnMessageError("不可以修改为待审核");
        }
        // 未通过3 或 已通过2 直接保存
        sc0p.get().setState(state);
        activityRepository.saveAndFlush(c);
        return CommonMethod.getReturnMessageOK();
    }
}