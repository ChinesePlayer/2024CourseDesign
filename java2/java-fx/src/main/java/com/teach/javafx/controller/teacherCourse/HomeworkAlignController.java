package com.teach.javafx.controller.teacherCourse;

import com.teach.javafx.controller.base.MessageDialog;
import com.teach.javafx.customWidget.TimePicker;
import com.teach.javafx.request.HttpRequestUtil;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.fatmansoft.teach.models.Course;
import org.fatmansoft.teach.models.Homework;
import org.fatmansoft.teach.payload.request.DataRequest;
import org.fatmansoft.teach.payload.response.DataResponse;
import org.fatmansoft.teach.util.CommonMethod;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

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

    @FXML
    public void initialize(){

    }

    public void init(Course c, ViewHomeworkController controller){
        this.course = c;
        this.viewHomeworkController = controller;
    }

    //用于编辑作业时填充已有数据
    public void fillData(Homework homework){
        this.titleField.setText(homework.getTitle());
        this.contentArea.setText(homework.getContent());
        this.startPicker.setValue(homework.getStart().toLocalDate());
        this.endPicker.setValue(homework.getEnd().toLocalDate());
        this.startTime.setTime(homework.getStart().toLocalTime());
        this.endTime.setTime(homework.getEnd().toLocalTime());
        this.homework = homework;
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
        DataResponse res = HttpRequestUtil.request("/api/homework/saveHomework", req);
        assert res != null;
        if(res.getCode() == 0){
            MessageDialog.showDialog("保存成功");
            Stage thisStage = (Stage) titleField.getScene().getWindow();
            //关闭当前窗口
            thisStage.close();
            viewHomeworkController.onClose();
        }
    }
}
