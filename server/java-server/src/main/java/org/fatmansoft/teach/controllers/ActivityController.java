package org.fatmansoft.teach.controllers;

import org.fatmansoft.teach.payload.request.DataRequest;
import org.fatmansoft.teach.payload.response.DataResponse;
import org.fatmansoft.teach.service.ActivityService;
import org.fatmansoft.teach.util.CommonMethod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/activity")
public class ActivityController {
    @Autowired
    private ActivityService activityService;


    //根据一定的筛选条件获取活动列表
    @PostMapping("/getActivityList")
    public DataResponse getActivityList(@Valid @RequestBody DataRequest req){
        return activityService.getActivityList(req);
    }

    //学生端用，根据一定的筛选条件获取活动列表
    //相比管理员端新增了学生自己的报名信息，只允许学生端访问已通过审核的活动
    @PostMapping("/getStudentActivityList")
    public DataResponse getStudentActivityList(@Valid @RequestBody DataRequest req){
        return activityService.getStudentActivityList(req);
    }

    //获取某学生自己申请的活动
    @PostMapping("/getStudentAppliedActivityList")
    public DataResponse getStudentAppliedActivityList(@Valid @RequestBody DataRequest req){
        return activityService.getStudentAppliedActivityList(req);
    }

    //获取所有Person对象
    @PostMapping("/getPersonList")
    public DataResponse getPersonList(@Valid @RequestBody DataRequest req){
        return activityService.getPersonList(req);
    }

    //保存活动信息
    @PostMapping("/saveActivity")
    public DataResponse saveActivity(@Valid @RequestBody DataRequest req){
        return activityService.saveActivity(req);
    }

    //删除活动
    @PostMapping("/deleteActivity")
    public DataResponse deleteActivity(@Valid @RequestBody DataRequest req){
        return activityService.deleteActivity(req);
    }

    //允许活动过审
    @PostMapping("/passActivity")
    public DataResponse passActivity(@Valid @RequestBody DataRequest req){
        return activityService.passActivity(req);
    }

    //不允许活动过审
    @PostMapping("/refuseActivity")
    public DataResponse refuseActivity(@Valid @RequestBody DataRequest req){
        return activityService.refuseActivity(req);
    }

    //学生端重新申请
    @PostMapping("/reapply")
    public DataResponse reapply(@Valid @RequestBody DataRequest req){
        return activityService.reapply(req);
    }

    //学生端取消申请
    @PostMapping("/cancelApply")
    public DataResponse cancelApply(@Valid @RequestBody DataRequest req){
        return activityService.cancelApply(req);
    }

    //报名活动
    @PostMapping("/signup")
    public DataResponse signup(@Valid @RequestBody DataRequest req){
        return activityService.signup(req);
    }

    //学生端查看自己报名的活动
    @PostMapping("/getMySignup")
    public DataResponse getMySignup(@Valid @RequestBody DataRequest req){
        return activityService.getMySignup(req);
    }

    //学生取消报名
    @PostMapping("/cancelSignup")
    public DataResponse cancelSignup(@Valid @RequestBody DataRequest req){
        return activityService.cancelSignup(req);
    }
}
