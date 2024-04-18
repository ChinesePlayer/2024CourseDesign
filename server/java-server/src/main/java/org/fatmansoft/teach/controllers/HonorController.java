package org.fatmansoft.teach.controllers;

import org.fatmansoft.teach.payload.request.DataRequest;
import org.fatmansoft.teach.payload.response.DataResponse;
import org.fatmansoft.teach.service.HonorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/honor")
public class HonorController {
    @Autowired
    private HonorService honorService;

    //获取学生的所有荣誉
    @PostMapping("/getHonors")
    public DataResponse getHonors(@Valid @RequestBody DataRequest req){
        return honorService.getHonors(req);
    }
}
