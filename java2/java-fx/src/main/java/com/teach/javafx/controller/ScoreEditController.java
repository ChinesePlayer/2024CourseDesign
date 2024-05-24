package com.teach.javafx.controller;

import com.teach.javafx.controller.base.MessageDialog;
import com.teach.javafx.models.Student;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.fatmansoft.teach.models.Course;
import org.fatmansoft.teach.models.Score;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import org.fatmansoft.teach.util.CommonMethod;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * MessageController 登录交互控制类 对应 base/message-dialog.fxml
 *  @FXML  属性 对应fxml文件中的
 *  @FXML 方法 对应于fxml文件中的 on***Click的属性
 */

public class ScoreEditController {
    @FXML
    public ComboBox<String> statusComboBox;
    @FXML
    private ComboBox<Student> studentComboBox;
    private List<Student> studentList;
    @FXML
    private ComboBox<Course> courseComboBox;
    private List<Course> courseList;
    @FXML
    private TextField markField;
    private ScoreTableController scoreTableController= null;
    private Integer scoreId= null;

    @FXML
    public void initialize() {
        ObservableList<String> statuses = FXCollections.observableArrayList();
        statuses.add("修读中");
        statuses.add("已及格");
        statuses.add("不及格");
        statusComboBox.setItems(statuses);
        CommonMethod.limitToNumber(markField);
    }

    @FXML
    public void okButtonClick(){
        if(studentComboBox.getSelectionModel().getSelectedItem() == null || studentComboBox.getSelectionModel().getSelectedItem().isEmptyStudent()){
            MessageDialog.showDialog("请选择学生! ");
            return;
        }
        if(courseComboBox.getSelectionModel().getSelectedItem() == null || courseComboBox.getSelectionModel().getSelectedItem().isEmptyCourse() ){
            MessageDialog.showDialog("请选择课程! ");
            return;
        }

        if(statusComboBox.getSelectionModel().getSelectedItem() == null || statusComboBox.getSelectionModel().getSelectedItem().isEmpty()){
            MessageDialog.showDialog("请选择修读状态! ");
            return;
        }
        Integer status = CommonMethod.getStatusInt(statusComboBox.getSelectionModel().getSelectedItem());
        if(status == 0 || status == 2){
            int ret = MessageDialog.choiceDialog("将修读状态设为 " + statusComboBox.getSelectionModel().getSelectedItem() + " 将不会录入成绩，你确定吗? ");
            if(ret != MessageDialog.CHOICE_YES){
                return;
            }
        }
        Integer mark = (markField.getText() ==null || markField.getText().isEmpty()) ? null : Integer.valueOf(markField.getText());
        if(status == 1 && (mark == null || mark < 60)){
            MessageDialog.showDialog("请输入合法分数! ");
            return;
        }
        Map data = new HashMap();
        Student op;
        op = studentComboBox.getSelectionModel().getSelectedItem();
        if(!op.isEmptyStudent()) {
            data.put("studentId",op.getStudentId());
        }
        Course course = courseComboBox.getSelectionModel().getSelectedItem();
        if(!course.isEmptyCourse()) {
            data.put("courseId", course.getCourseId());
        }
        data.put("scoreId",scoreId);
        data.put("mark",markField.getText());
        data.put("status", CommonMethod.getStatusInt(statusComboBox.getSelectionModel().getSelectedItem()));
        scoreTableController.doClose("ok",data);
    }
    @FXML
    public void cancelButtonClick(){
        scoreTableController.doClose("cancel",null);
    }

    public void setScoreTableController(ScoreTableController scoreTableController) {
        this.scoreTableController = scoreTableController;
    }
    public void init(){
        studentList =scoreTableController.getStudentList();
        courseList = scoreTableController.getCourseList();
        studentComboBox.getItems().addAll(studentList);
        courseComboBox.getItems().addAll(courseList);
    }

    public int getStudentIndexById(int id){
        for(int i = 0; i < studentList.size(); i++){
            if(studentList.get(i).getStudentId() == id){
                return i;
            }
        }
        return -1;
    }

    public int getCourseIndexById(int id){
        for(int i = 0; i < courseList.size(); i++){
            if(courseList.get(i).getCourseId() == id){
                return i;
            }
        }
        return -1;
    }

    public void showDialog(Score data){
        if(data == null) {
            scoreId = null;
            studentComboBox.getSelectionModel().select(-1);
            courseComboBox.getSelectionModel().select(-1);
            studentComboBox.setDisable(false);
            courseComboBox.setDisable(false);
            markField.setText("");
        }else {
            scoreId = data.getScoreId();
            Integer status = data.getStatus();
            if(status == null || status != 0 && status != 1 && status != 2){
                statusComboBox.getSelectionModel().select(null);
            }
            else{
                statusComboBox.getSelectionModel().select(getStatusStr(status));
            }
            studentComboBox.getSelectionModel().select(getStudentIndexById(data.getStudentId()));
            courseComboBox.getSelectionModel().select(getCourseIndexById(data.getCourseId()));
            studentComboBox.setDisable(true);
            courseComboBox.setDisable(true);
            if(data.getMark() != null){
                markField.setText(data.getMark().intValue()+"");
            }
            else{
                markField.setText("");
            }
        }
    }

    private Integer getStatusInt(){
        String str = statusComboBox.getSelectionModel().getSelectedItem();
        return switch (str) {
            case "修读中" -> 0;
            case "已及格" -> 1;
            case "不及格" -> 2;
            default -> -1;
        };
    }

    private String getStatusStr(Integer status){
        return switch (status) {
            case 0 -> "修读中";
            case 1 -> "已及格";
            case 2 -> "不及格";
            default -> "----";
        };
    }
}
