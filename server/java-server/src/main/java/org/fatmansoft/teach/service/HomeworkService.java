package org.fatmansoft.teach.service;

import org.fatmansoft.teach.factory.HomeworkFactory;
import org.fatmansoft.teach.models.Course;
import org.fatmansoft.teach.models.Homework;
import org.fatmansoft.teach.models.HomeworkFile;
import org.fatmansoft.teach.payload.request.DataRequest;
import org.fatmansoft.teach.payload.response.DataResponse;
import org.fatmansoft.teach.repository.CourseRepository;
import org.fatmansoft.teach.repository.HomeworkFileRepository;
import org.fatmansoft.teach.repository.HomeworkRepository;
import org.fatmansoft.teach.util.CommonMethod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;


@Service
public class HomeworkService {
    @Autowired
    private HomeworkRepository homeworkRepository;
    @Autowired
    private HomeworkFactory homeworkFactory;
    @Autowired
    private CourseRepository courseRepository;
    @Autowired
    private BaseService baseService;
    @Autowired
    private HomeworkFileRepository homeworkFileRepository;
    @Value("${attach.folder}")
    private String attachFolder;

    public DataResponse getHomeworkList(DataRequest req){
        Integer courseId = req.getInteger("courseId");
        if(courseId == null){
            return CommonMethod.getReturnMessageError("无法获取课程ID");
        }

        //寻找指定课程ID的作业
        List<Homework> homeworkList = homeworkRepository.findByCourseId(courseId);
        List<Map> dataList = new ArrayList<>();
        for (Homework h : homeworkList){
            dataList.add(homeworkFactory.createHomeworkMap(h,""));
        }
        return CommonMethod.getReturnData(dataList);
    }

    public DataResponse saveHomework(MultipartFile[] files, DataRequest req){
        Integer homeworkId = req.getInteger("homeworkId");
        Integer courseId = req.getInteger("courseId");
        String title = req.getString("title");
        String content = req.getString("content");
        String startStr = req.getString("start");
        String endStr = req.getString("end");

        if(courseId == null){
            return CommonMethod.getReturnMessageError("无法获取课程ID! ");
        }
        Optional<Course> cOp = courseRepository.findById(courseId);
        if(cOp.isEmpty()){
            return CommonMethod.getReturnMessageError("无法获取课程信息");
        }

        Course course = cOp.get();
        Homework homework = null;

        if(homeworkId == null){
            homework = new Homework();
        }
        else{
            Optional<Homework> hOp = homeworkRepository.findById(homeworkId);
            if(hOp.isEmpty()){
                return CommonMethod.getReturnMessageError("无法找到作业! ");
            }
            else{
                homework = hOp.get();
            }
        }
        homework.setTitle(title);
        homework.setContent(content);
        homework.setCourse(course);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(CommonMethod.DATE_FORMAT);
        LocalDateTime start = LocalDateTime.parse(startStr, formatter);
        LocalDateTime end = LocalDateTime.parse(endStr, formatter);

        homework.setStart(start);
        homework.setEnd(end);
        homeworkRepository.saveAndFlush(homework);

        try{
            //保存文件
            for(MultipartFile file : files){
                String uniqueFileName = CommonMethod.generateUniqueFileName(file.getOriginalFilename(), attachFolder + "homework/" + homeworkId + "/");
                Path path = Paths.get(attachFolder + "homework/" + homeworkId + "/" + uniqueFileName);
                Files.createDirectories(path.getParent());
                Files.write(path, file.getBytes());

                //保存HomeworkFile实体类
                HomeworkFile hf = new HomeworkFile();
                hf.setHomework(homework);
                hf.setFilePath(attachFolder + "homework/" + homeworkId + "/" + uniqueFileName);
                hf.setFileName(uniqueFileName);
                homeworkFileRepository.saveAndFlush(hf);
            }
        }
        catch (IOException e){
            e.printStackTrace();
            return CommonMethod.getReturnMessageError("作业保存成功, 但文件未能上传");
        }

        return CommonMethod.getReturnMessageOK("保存成功");
    }

    public DataResponse deleteHomework(DataRequest req){
        Integer homeworkId = req.getInteger("homeworkId");
        if(homeworkId == null){
            return CommonMethod.getReturnMessageError("无法获取作业ID");
        }
        Optional<Homework> hOp = homeworkRepository.findById(homeworkId);
        if(hOp.isEmpty()){
            return CommonMethod.getReturnMessageError("无法找到该作业, 可能已被删除!");
        }
        Homework homework = hOp.get();
        //删除作业
        homeworkRepository.delete(homework);

        return CommonMethod.getReturnMessageOK("删除成功");
    }

    public DataResponse uploadHomeworkFiles(MultipartFile[] files, Integer homeworkId){
        System.out.println(files);
        DataResponse res = baseService.uploadMultiFiles(files, attachFolder + "homework/" + homeworkId +"/");
        if(res == null){
            return CommonMethod.getReturnMessageError("上传作业文件失败! ");
        }
        else {
            return CommonMethod.getReturnMessageOK("作业文件上传成功! ");
        }
    }

    public DataResponse getFileList(DataRequest req){
        Integer homeworkId = req.getInteger("homeworkId");
        if(homeworkId == null){
            return CommonMethod.getReturnData(new ArrayList<Map>());
        }
        //查找该作业的所有文件
        List<HomeworkFile> files = homeworkFileRepository.findByHomeworkId(homeworkId);
        List<Map> dataList = new ArrayList<>();
        for(HomeworkFile hf : files){
            dataList.add(homeworkFactory.createHomeworkFileMap(hf,""));
        }
        return CommonMethod.getReturnData(dataList);
    }

    public DataResponse deleteFile(DataRequest req){
        Integer fileId = req.getInteger("fileId");
        if(fileId == null){
            return CommonMethod.getReturnMessageError("无法查找文件ID");
        }
        Optional<HomeworkFile> hfOp = homeworkFileRepository.findById(fileId);
        if(hfOp.isEmpty()){
            return CommonMethod.getReturnMessageError("无法找到该文件! ");
        }
        HomeworkFile homeworkFile = hfOp.get();
        String filePath = homeworkFile.getFilePath();
        //删除文件
        homeworkFileRepository.delete(homeworkFile);
        try{
            Path path = Paths.get(filePath);
            Files.deleteIfExists(path);
        }
        catch (IOException e){
            e.printStackTrace();
            return CommonMethod.getReturnMessageError("无法找到文件, 已删除该文件记录");
        }
        return CommonMethod.getReturnMessageOK("删除成功");
    }
}
