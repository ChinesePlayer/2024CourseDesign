package org.fatmansoft.teach.service;

import org.fatmansoft.teach.factory.HomeworkFactory;
import org.fatmansoft.teach.models.*;
import org.fatmansoft.teach.payload.request.DataRequest;
import org.fatmansoft.teach.payload.response.DataResponse;
import org.fatmansoft.teach.repository.*;
import org.fatmansoft.teach.util.CommonMethod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;


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
    @Autowired
    private AnswerFileRepository answerFileRepository;
    @Autowired
    private AnswerRepository answerRepository;
    @Autowired
    private StudentRepository studentRepository;
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
            dataList.add(homeworkFactory.createHomeworkMap(h));
        }
        return CommonMethod.getReturnData(dataList);
    }

    public DataResponse getStudentHomeworkList(DataRequest req){
        Integer courseId = req.getInteger("courseId");
        Integer studentId = req.getInteger("studentId");
        if(courseId == null){
            return CommonMethod.getReturnMessageError("无法获取课程ID");
        }
        if(studentId == null){
            return CommonMethod.getReturnMessageError("无法获取学生ID");
        }
        List<Homework> homeworkList = homeworkRepository.findByCourseId(courseId);
        List<Map> dataList = new ArrayList<>();
        for(Homework h : homeworkList){
            dataList.add(homeworkFactory.createStudentHomeworkMap(h,studentId));
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

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(CommonMethod.DATE_TIME_FORMAT);
        LocalDateTime start = LocalDateTime.parse(startStr, formatter);
        LocalDateTime end = LocalDateTime.parse(endStr, formatter);

        homework.setStart(start);
        homework.setEnd(end);
        Homework homework1 = homeworkRepository.saveAndFlush(homework);

        Homework finalHomework = homework;
        int count = CommonMethod.saveMultipartFiles(
                files,
                attachFolder + "homework/" + homework1.getHomeworkId() + "/",
                savedFile -> {
                    //保存HomeworkFile实体类
                    HomeworkFile hf = new HomeworkFile();
                    hf.setHomework(finalHomework);
                    hf.setFilePath(savedFile.getPath());
                    hf.setFileName(savedFile.getName());
                    homeworkFileRepository.saveAndFlush(hf);
                }
        );
        if (count == 0 && files != null){
            return CommonMethod.getReturnMessageError("作业保存成功, 但文件未能上传");
        }

        return CommonMethod.getReturnMessageOK("保存成功");
    }

    public DataResponse saveAnswer(MultipartFile[] files, DataRequest req){
        Integer homeworkId = req.getInteger("homeworkId");
        Integer answerId = req.getInteger("answerId");
        Integer studentId = req.getInteger("studentId");
        String content = req.getString("content");
        if(studentId == null){
            return CommonMethod.getReturnMessageError("无法获取学生信息");
        }
        Optional<Student> sOp = studentRepository.findById(studentId);
        if(sOp.isEmpty()){
            return CommonMethod.getReturnMessageError("无法获取学生信息");
        }
        Student student = sOp.get();
        if(homeworkId == null){
            return CommonMethod.getReturnMessageError("无法获取作业ID");
        }
        Optional<Homework> hOp = homeworkRepository.findById(homeworkId);
        if(hOp.isEmpty()){
            return CommonMethod.getReturnMessageError("无法找到作业信息");
        }
        Homework homework = hOp.get();

        //检查是否在作答时间内
        LocalDateTime now = LocalDateTime.now();
        if(now.isBefore(homework.getStart())){
            return CommonMethod.getReturnMessageError("未到作答时间!");
        }
        else if(now.isAfter(homework.getEnd())){
            return CommonMethod.getReturnMessageError("作答时间已过，无法答题!");
        }

        Answer answer = null;
        if(answerId == null){
            answer = new Answer();
        }
        else{
            Optional<Answer> aOp = answerRepository.findById(answerId);
            if(aOp.isEmpty()){
                return CommonMethod.getReturnMessageError("无法找到答案信息");
            }
            answer = aOp.get();
        }

        answer.setContent(content);
        answer.setHomework(homework);
        answer.setStudent(student);
        answer = answerRepository.saveAndFlush(answer);

        answerId = answer.getAnswerId();
        //保存文件
        Answer finalAnswer = answer;
        int count = CommonMethod.saveMultipartFiles(files, attachFolder + "answer/" + answerId + "/", savedFile -> {
            //保存AnswerFile实体类
            AnswerFile af = new AnswerFile();
            af.setFileName(savedFile.getName());
            af.setAnswer(finalAnswer);
            af.setFilePath(savedFile.getPath());
            answerFileRepository.saveAndFlush(af);
        });
        if(files != null && count == 0){
            return CommonMethod.getReturnMessageError("答案保存成功, 但文件未能上传");
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

    public ResponseEntity<StreamingResponseBody> downloadFile(DataRequest req){
        Integer fileId = req.getInteger("fileId");
        if(fileId == null){
            return ResponseEntity.internalServerError().build();
        }
        Optional<HomeworkFile> hfOp = homeworkFileRepository.findById(fileId);
        if(hfOp.isEmpty()){
            return ResponseEntity.internalServerError().build();
        }
        HomeworkFile homeworkFile = hfOp.get();
        String filePath = homeworkFile.getFilePath();
        try{
            byte[] data = Files.readAllBytes(Paths.get(filePath));
            MediaType mediaType = new MediaType(MediaType.APPLICATION_OCTET_STREAM);
            StreamingResponseBody streamBody = outputStream -> outputStream.write(data);
            return ResponseEntity.ok()
                    .contentType(mediaType)
                    .body(streamBody);
        }
        catch (IOException e){
            e.printStackTrace();
        }
        return ResponseEntity.internalServerError().build();
    }

    public DataResponse getStudentAnswerFileList(DataRequest req){
        Integer answerId = req.getInteger("answerId");
        if(answerId == null){
            return CommonMethod.getReturnMessageError("无法获取作答ID");
        }
        List<AnswerFile> answerFileList = answerFileRepository.findByAnswerId(answerId);
        List<Map> dataList = new ArrayList<>();
        for(AnswerFile af : answerFileList){
            dataList.add(homeworkFactory.createStudentAnswerFileMap(af));
        }
        return CommonMethod.getReturnData(dataList);
    }

    public DataResponse deleteAnswerFile(DataRequest req){
        Integer fileId = req.getInteger("fileId");
        if(fileId == null){
            return CommonMethod.getReturnMessageError("无法获取文件ID");
        }
        Optional<AnswerFile> afOp = answerFileRepository.findById(fileId);
        if(afOp.isEmpty()){
            return CommonMethod.getReturnMessageError("无法找到文件记录, 可能已被删除");
        }
        AnswerFile answerFile = afOp.get();
        answerFileRepository.delete(answerFile);
        try{
            Path p = Paths.get(answerFile.getFilePath());
            Files.deleteIfExists(p);
        }
        catch (IOException e){
            e.printStackTrace();
            return CommonMethod.getReturnMessageError("无法找到文件, 已删除该文件记录");
        }
        return CommonMethod.getReturnMessageOK("删除成功");
    }
}
