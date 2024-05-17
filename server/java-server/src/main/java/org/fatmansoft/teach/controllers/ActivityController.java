package org.fatmansoft.teach.controllers;

import org.fatmansoft.teach.payload.request.DataRequest;
import org.fatmansoft.teach.payload.response.DataResponse;
import org.fatmansoft.teach.service.ActivityService;
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

    @PostMapping("/deleteActivity")
    public DataResponse deleteActivity(@Valid @RequestBody DataRequest req){
        return activityService.deleteActivity(req);
    }
}
