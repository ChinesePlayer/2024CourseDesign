package org.fatmansoft.teach.controllers;

import org.fatmansoft.teach.payload.request.DataRequest;
import org.fatmansoft.teach.payload.response.DataResponse;
import org.fatmansoft.teach.service.HomeworkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

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
    public DataResponse saveHomework(@RequestPart("file") MultipartFile[] files, @RequestPart("dataRequest") DataRequest req){
        return homeworkService.saveHomework(files, req);
    }

    @PostMapping("/deleteHomework")
    public DataResponse deleteHomework(@Valid @RequestBody DataRequest req){
        return homeworkService.deleteHomework(req);
    }

    //接收参数：files: 文件名和文件内容映射
    @PostMapping("/uploadHomeworkFiles")
    public DataResponse uploadHomeworkFiles(@RequestParam("file") MultipartFile[] files,
                                            @RequestParam("homeworkId") Integer homeworkId){
        return homeworkService.uploadHomeworkFiles(files, homeworkId);
    }

    @PostMapping("/getFileList")
    public DataResponse getFileList(@Valid @RequestBody DataRequest req){
        return homeworkService.getFileList(req);
    }

    @PostMapping("/deleteFile")
    public DataResponse deleteFile(@Valid @RequestBody DataRequest req){
        return homeworkService.deleteFile(req);
    }

    @PostMapping("/downloadFile")
    public ResponseEntity<StreamingResponseBody> downloadFile(@Valid @RequestBody DataRequest req){
        return homeworkService.downloadFile(req);
    }
}
