package com.teach.javafx.controller.teacherCourse;

import com.teach.javafx.controller.base.MessageController;
import com.teach.javafx.controller.base.MessageDialog;
import com.teach.javafx.request.HttpRequestUtil;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.fatmansoft.teach.payload.request.DataRequest;
import org.fatmansoft.teach.payload.response.DataResponse;
import org.fatmansoft.teach.util.CommonMethod;

import java.util.HashMap;
import java.util.Map;

public class StudentScoreEditController {
    @FXML
    public TextField markField;
    @FXML
    public ComboBox<String> statusComboBox;
    public StudentViewerController controller;
    public Integer studentId;
    public Integer scoreId;
    public Stage stage;

    @FXML
    public void initialize(){
        CommonMethod.limitToNumber(markField);
    }

    public void init(Map data){
        String mark = (String) data.get(StudentViewerController.MARK);
        if(mark == null){
            mark = "";
        }
        markField.setText(mark);
        ObservableList<String> items = FXCollections.observableArrayList();
        items.add("修读中");
        items.add("已及格");
        items.add("不及格");
        statusComboBox.setItems(items);
        String status = CommonMethod.getString(data, StudentViewerController.STATUS);
        statusComboBox.getSelectionModel().select(status);

        studentId = CommonMethod.getInteger(data, StudentViewerController.STUDENT_ID);
        scoreId = CommonMethod.getInteger(data, StudentViewerController.SCORE_ID);
        System.out.println(studentId);
        System.out.println(scoreId);
    }

    public void onSave(){
        Integer statusCode = CommonMethod.getStatusInt(statusComboBox.getSelectionModel().getSelectedItem());
        if(statusCode == null){
            MessageDialog.showDialog("请选择一个修读状态! ");
            return;
        }
        if(statusCode == 0 || statusCode == 2){
            int ret = MessageDialog.choiceDialog("将修读状态设为 " + statusComboBox.getSelectionModel().getSelectedItem() + " 后将会清除成绩，你确定吗? ");
            if(ret != MessageDialog.CHOICE_YES){
                return;
            }
        }
        Map newData = new HashMap<>();
        newData.put(StudentViewerController.STATUS, statusCode);
        newData.put(StudentViewerController.MARK, markField.getText());
        newData.put(StudentViewerController.STUDENT_ID, studentId);
        newData.put(StudentViewerController.SCORE_ID, scoreId);
        DataRequest req = new DataRequest();
        req.add(StudentViewerController.STATUS, statusCode);
        req.add(StudentViewerController.MARK, markField.getText());
        req.add(StudentViewerController.STUDENT_ID, studentId);
        req.add(StudentViewerController.SCORE_ID, scoreId);
        DataResponse res = HttpRequestUtil.request("/api/teacher/saveStudentInfo", req);
        assert res != null;
        if(res.getCode() == 0){
            MessageDialog.showDialog("保存成功! ");
        }
        else {
            MessageDialog.showDialog("错误: " + res.getMsg());
        }
        controller.onHasSaved(newData);
    }
}
