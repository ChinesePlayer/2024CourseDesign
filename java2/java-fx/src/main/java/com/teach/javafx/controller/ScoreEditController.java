package com.teach.javafx.controller;

import com.teach.javafx.models.Student;
import com.teach.javafx.request.OptionItem;
import org.fatmansoft.teach.models.Course;
import org.fatmansoft.teach.models.Score;
import org.fatmansoft.teach.util.CommonMethod;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;

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
    }

    @FXML
    public void okButtonClick(){
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
            studentComboBox.getSelectionModel().select(getStudentIndexById(data.getStudentId()));
            courseComboBox.getSelectionModel().select(getCourseIndexById(data.getCourseId()));
            studentComboBox.setDisable(true);
            courseComboBox.setDisable(true);
            if(data.getMark() != null){
                markField.setText(data.getMark()+"");
            }
            else{
                markField.setText("");
            }
        }
    }
}
