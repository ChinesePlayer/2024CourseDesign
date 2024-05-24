package org.fatmansoft.teach.controllers;

import org.fatmansoft.teach.payload.request.DataRequest;
import org.fatmansoft.teach.payload.response.DataResponse;
import org.fatmansoft.teach.service.ApplicationService;
import org.fatmansoft.teach.util.CommonMethod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/application")
public class ApplicationController {
    @Autowired
    private ApplicationService applicationService;

    //根据筛选条件获取假条列表
    @PostMapping("/getApplicationList")
    public DataResponse getApplicationList(@Valid @RequestBody DataRequest req){
        return applicationService.getApplicationList(req);
    }

    //保存(修改)假条
    @PostMapping("/saveApplication")
    public DataResponse saveApplication(@Valid @RequestBody DataRequest req){
        return applicationService.saveApplication(req);
    }

    //删除假条，仅学生端可访问
    @PostMapping("/deleteApplication")
    public DataResponse deleteApplication(@Valid @RequestBody DataRequest req){
        return applicationService.deleteApplication(req);
    }

    //销假
    @PostMapping("/destroyApplication")
    public DataResponse destroyApplication(@Valid @RequestBody DataRequest req){
        return applicationService.destroyApplication(req);
    }

    //管理员端批准请假
    @PostMapping("/allowApplication")
    public DataResponse allowApplication(@Valid @RequestBody DataRequest req){
        return applicationService.allowApplication(req);
    }

    @PostMapping("/refuseApplication")
    public DataResponse refuseApplication(@Valid @RequestBody DataRequest req){
        return applicationService.refuseApplication(req);
    }
}
