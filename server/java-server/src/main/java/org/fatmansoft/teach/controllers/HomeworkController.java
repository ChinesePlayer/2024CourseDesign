package org.fatmansoft.teach.controllers;

import org.fatmansoft.teach.payload.request.DataRequest;
import org.fatmansoft.teach.payload.response.DataResponse;
import org.fatmansoft.teach.service.HomeworkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Map;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/homework")
public class HomeworkController {
    @Autowired
    private HomeworkService homeworkService;

    @PostMapping("/getHomeworkList")
    public DataResponse getHomeworkList(@Valid @RequestBody DataRequest req){
        return homeworkService.getHomeworkList(req);
    }

    @PostMapping("/saveHomework")
    public DataResponse saveHomework(@Valid @RequestBody DataRequest req){
        return homeworkService.saveHomework(req);
    }

    @PostMapping("/deleteHomework")
    public DataResponse deleteHomework(@Valid @RequestBody DataRequest req){
        return homeworkService.deleteHomework(req);
    }

    //接收参数：files: 文件名和文件内容映射
    @PostMapping("/uploadHomeworkFiles")
    public DataResponse uploadHomeworkFiles(@RequestBody Map<String, byte[]> files,
                                            @RequestParam("homeworkId") Integer homeworkId){
        return homeworkService.uploadHomeworkFiles(files, homeworkId);
    }

}
