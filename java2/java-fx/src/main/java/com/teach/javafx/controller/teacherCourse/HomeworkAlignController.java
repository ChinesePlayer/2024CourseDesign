package com.teach.javafx.controller.teacherCourse;

import com.teach.javafx.MainApplication;
import com.teach.javafx.controller.base.LoadingAction;
import com.teach.javafx.controller.base.MessageDialog;
import com.teach.javafx.customWidget.TimePicker;
import com.teach.javafx.managers.WindowOpenAction;
import com.teach.javafx.managers.WindowsManager;
import com.teach.javafx.request.HttpRequestUtil;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.fatmansoft.teach.models.Course;
import org.fatmansoft.teach.models.Homework;
import org.fatmansoft.teach.models.HomeworkFile;
import org.fatmansoft.teach.payload.request.DataRequest;
import org.fatmansoft.teach.payload.response.DataResponse;
import org.fatmansoft.teach.util.CommonMethod;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class HomeworkAlignController {
    @FXML
    public TextField titleField;
    @FXML
    public TextArea contentArea;
    @FXML
    public DatePicker startPicker;
    @FXML
    public DatePicker endPicker;
    @FXML
    public TimePicker startTime;
    @FXML
    public TimePicker endTime;
    private Course course;
    private Homework homework;
    private ViewHomeworkController viewHomeworkController;

    private List<HomeworkFile> fileList = new ArrayList<>();

    @FXML
    public void initialize(){

    }

    public void init(Course c, ViewHomeworkController controller){
        this.course = c;
        this.viewHomeworkController = controller;
    }

    //用于编辑作业时填充已有数据
    public void fillData(Homework homework){
        System.out.println("数据被填充了! ");
        this.titleField.setText(homework.getTitle());
        this.contentArea.setText(homework.getContent());
        this.startPicker.setValue(homework.getStart().toLocalDate());
        this.endPicker.setValue(homework.getEnd().toLocalDate());
        this.startTime.setTime(homework.getStart().toLocalTime());
        this.endTime.setTime(homework.getEnd().toLocalTime());
        this.homework = homework;
        //获取文件列表
        getFileList();
    }

    //提交作业
    public void onSubmit(ActionEvent event){
        String title = titleField.getText();
        String content = contentArea.getText();
        LocalDate startDate = startPicker.getValue();
        LocalDate endDate = endPicker.getValue();
        if(startDate == null){
            MessageDialog.showDialog("请选择开始日期! ");
            return;
        }
        if(endDate == null){
            MessageDialog.showDialog("请选择结束日期! ");
            return;
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(CommonMethod.DATE_FORMAT);
        LocalDateTime sDateTime = startDate.atTime(startTime.getLocalTime());
        LocalDateTime eDateTime = endDate.atTime(endTime.getLocalTime());
        String startDateTimeStr = sDateTime.format(formatter);
        String endDateTimeStr = eDateTime.format(formatter);

        if(title == null || title.isEmpty()){
            MessageDialog.showDialog("作业标题不能为空! ");
            return;
        }
        if(content == null || content.isEmpty()){
            MessageDialog.showDialog("作业内容不能为空!");
            return;
        }

        DataRequest req = new DataRequest();
        req.add("title", title);
        req.add("content", content);
        req.add("courseId", course.getCourseId());
        req.add("start", startDateTimeStr);
        req.add("end", endDateTimeStr);
        req.add("homeworkId", homework == null ? null : homework.getHomeworkId());
        List<HomeworkFile> newFile = new ArrayList<>();
        for(HomeworkFile f : fileList){
            if(f.getFileId() == null){
                newFile.add(f);
            }
        }
        //仅上传新文件
        DataResponse res = HttpRequestUtil.request("/api/homework/saveHomework", req, HomeworkFile.buildFilePaths(newFile));
        assert res != null;
        if(res.getCode() == 0){
            MessageDialog.showDialog("保存成功");
            Stage thisStage = (Stage) titleField.getScene().getWindow();
            //关闭当前窗口
            thisStage.close();
            viewHomeworkController.onClose();
        }
        else {
            MessageDialog.showDialog(res.getMsg());
        }
    }


    public void onHomeworkFileClick(ActionEvent event){
        try{
            FXMLLoader loader = new FXMLLoader(MainApplication.class.getResource("teacherCourse/homework-file-checker.fxml"));
            WindowsManager.getInstance().openNewWindow(
                    loader, 800, 600, "上传作业文件", titleField.getScene().getWindow(),
                    Modality.WINDOW_MODAL,
                    new WindowOpenAction() {
                        @Override
                        public void init(Object controller) {
                            WindowOpenAction.super.init(controller);
                            HomeworkFileCheckerController controller1 = (HomeworkFileCheckerController) controller;
                            controller1.init(HomeworkAlignController.this, homework);
                        }
                    }
            );
        }
        catch (IOException e){
            e.printStackTrace();
            MessageDialog.showDialog("打开文件上传页面失败! ");
        }
    }

    //获取文件列表
    public void getFileList(){
        fileList.clear();
        DataRequest req = new DataRequest();
        req.add("homeworkId", homework.getHomeworkId());
        DataResponse res = HttpRequestUtil.request("/api/homework/getFileList", req);
        assert res != null;
        if(res.getCode() == 0){
            List<Map> rawData = (ArrayList<Map>) res.getData();
            for(Map m : rawData){
                HomeworkFile homeworkFile = new HomeworkFile(m);
                fileList.add(homeworkFile);
            }
        }
    }

    public List<HomeworkFile> getHomeworkFile(){
        return fileList;
    }
}
