package com.teach.javafx.controller;

import com.teach.javafx.MainApplication;
import com.teach.javafx.controller.base.MessageDialog;
import com.teach.javafx.controller.studentScore.StudentScoreValueFactory;
import com.teach.javafx.models.Student;
import com.teach.javafx.request.HttpRequestUtil;
import com.teach.javafx.request.OptionItem;
import io.github.palexdev.materialfx.controls.MFXButton;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.chart.PieChart;
import javafx.util.Callback;
import org.fatmansoft.teach.models.Course;
import org.fatmansoft.teach.models.Score;
import org.fatmansoft.teach.payload.request.DataRequest;
import org.fatmansoft.teach.payload.response.DataResponse;
import org.fatmansoft.teach.util.CommonMethod;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.MapValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ScoreTableController {
    @FXML
    private TableView<Score> dataTableView;
    @FXML
    private TableColumn<Score,String> studentNum;
    @FXML
    public TableColumn<Score, String> status;
    @FXML
    private TableColumn<Score,String> studentName;
    @FXML
    private TableColumn<Score,String> className;
    @FXML
    private TableColumn<Score,String> courseNum;
    @FXML
    private TableColumn<Score,String> courseName;
    @FXML
    private TableColumn<Score,String> credit;
    @FXML
    private TableColumn<Score,String> mark;
    @FXML
    private TableColumn<Score, Button> editColumn;


    private ArrayList<Score> scoreList = new ArrayList();  // 学生信息列表数据
    private ObservableList<Score> observableList= FXCollections.observableArrayList();  // TableView渲染列表

    @FXML
    private ComboBox<Student> studentComboBox;


    private List<Student> studentList = new ArrayList<>();
    @FXML
    private ComboBox<Course> courseComboBox;


    private List<Course> courseList = new ArrayList<>();

    private ScoreEditController scoreEditController = null;
    private Stage stage = null;
    private Student emptyStudent;
    private Course emptyCourse;

    public List<Student> getStudentList() {
        return studentList;
    }
    public List<Course> getCourseList() {
        return courseList;
    }

    public void getCourseFromNet(){
        DataRequest req = new DataRequest();
        DataResponse res = HttpRequestUtil.request("/api/score/getCourseList", req);
        if(res != null && res.getCode() == 0){
            List<Map> rawData = (ArrayList<Map>) res.getData();
            //创建一个空课程
            emptyCourse = new Course();
            emptyCourse.setCourseId(0);
            courseList.add(emptyCourse);
            for(Map m : rawData){
                Course c = new Course(m);
                courseList.add(c);
            }
        }
    }

    public void getStudentFromNet(){
        DataRequest req = new DataRequest();
        DataResponse res = HttpRequestUtil.request("/api/score/getStudentList", req);
        if(res != null && res.getCode() == 0){
            List<Map> rawData = (ArrayList<Map>) res.getData();
            //添加一个空学生
            emptyStudent = new Student();
            emptyStudent.setStudentId(0);
            studentList.add(emptyStudent);
            for(Map m : rawData){
                Student s = new Student(m);
                studentList.add(s);
            }
        }
    }

    public void setStudentComboBox(){
        studentComboBox.setItems(FXCollections.observableArrayList(studentList));
    }

    public void setCourseComboBox(){
        courseComboBox.setItems(FXCollections.observableArrayList(courseList));
    }

    public void setQueryComboBox(){
        setStudentComboBox();
        setCourseComboBox();
    }


    @FXML
    private void onQueryButtonClick(){
        Integer studentId = 0;
        Integer courseId = 0;
        Student student;
        student = studentComboBox.getValue();
        if(student != null && student.getStudentId() != -1)
            studentId = student.getStudentId();
        Course course = courseComboBox.getValue();
        if(course != null && course.getCourseId() != -1)
            courseId =course.getCourseId();
        DataResponse res;
        DataRequest req =new DataRequest();
        req.add("studentId",studentId);
        req.add("courseId",courseId);
        res = HttpRequestUtil.request("/api/score/getScoreList",req); //从后台获取所有学生信息列表集合
        if(res != null && res.getCode()== 0) {
            scoreList.clear();
            List<Map> rawData = (ArrayList<Map>) res.getData();
            for(Map m : rawData){
                Score s = new Score(m);
                scoreList.add(s);
            }
        }
        setTableViewData();
    }


    private void setTableViewData() {
        observableList.clear();
        observableList.addAll(FXCollections.observableArrayList(scoreList));
        dataTableView.setItems(observableList);
    }

    public void setPanelData(){
        setTableViewData();
        setQueryComboBox();
    }

    public void editItem(ActionEvent event){
        MFXButton target = (MFXButton) event.getTarget();
        TableCell<Course, String> cell = (TableCell<Course, String>) target.getParent();
        int rowIndex = cell.getIndex();
        Score s = dataTableView.getItems().get(rowIndex);
        initDialog();
        scoreEditController.showDialog(s);
        MainApplication.setCanClose(false);
        stage.showAndWait();

    }

    public void updateData(){
        getCourseFromNet();
        getStudentFromNet();
        setPanelData();
    }

    @FXML
    public void initialize() {
        studentNum.setCellValueFactory(new StudentScoreValueFactory());  //设置列值工程属性
        studentName.setCellValueFactory(new StudentScoreValueFactory());
        className.setCellValueFactory(new StudentScoreValueFactory());
        courseNum.setCellValueFactory(new StudentScoreValueFactory());
        courseName.setCellValueFactory(new StudentScoreValueFactory());
        credit.setCellValueFactory(new StudentScoreValueFactory());
        mark.setCellValueFactory(new StudentScoreValueFactory());
        status.setCellValueFactory(new StudentScoreValueFactory());
        editColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Score, Button>, ObservableValue<Button>>() {
            @Override
            public ObservableValue<Button> call(TableColumn.CellDataFeatures<Score, Button> data) {
                if(data == null){
                    return null;
                }
                MFXButton button = new MFXButton("编辑");
                button.setOnAction(event -> editItem(event));
                return new ReadOnlyObjectWrapper<>(button);
            }
        });

        updateData();
        onQueryButtonClick();
    }

    private void initDialog() {
        if(stage!= null)
            return;
        FXMLLoader fxmlLoader ;
        Scene scene = null;
        try {
            fxmlLoader = new FXMLLoader(MainApplication.class.getResource("score-edit-dialog.fxml"));
            scene = new Scene(fxmlLoader.load(), 400, 220);
            stage = new Stage();
            stage.initOwner(MainApplication.getMainStage());
            //设置当前对话框的模态: 即当对话框关闭前，用户无法与其父窗口进行交互
            stage.initModality(Modality.WINDOW_MODAL);
            //将其永远置于顶层
            stage.setAlwaysOnTop(true);
            stage.setScene(scene);
            stage.setTitle("成绩录入对话框");
            //回调函数，当窗口关闭时会调用，当然，是在窗口被关闭的那一瞬间之前调用
            stage.setOnCloseRequest(event ->{
                MainApplication.setCanClose(true);
            });
            scoreEditController = (ScoreEditController) fxmlLoader.getController();
            scoreEditController.setScoreTableController(this);
            scoreEditController.init();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public void doClose(String cmd, Map data) {
        MainApplication.setCanClose(true);
        stage.close();
        if(!"ok".equals(cmd))
            return;
        DataResponse res;
        Integer studentId = CommonMethod.getInteger(data,"studentId");
        if(studentId == null) {
            MessageDialog.showDialog("没有选中学生不能添加保存！");
            return;
        }
        Integer courseId = CommonMethod.getInteger(data,"courseId");
        if(courseId == null) {
            MessageDialog.showDialog("没有选中课程不能添加保存！");
            return;
        }
        DataRequest req =new DataRequest();
        req.add("studentId",studentId);
        req.add("courseId",courseId);
        req.add("scoreId",CommonMethod.getInteger(data,"scoreId"));
        req.add("mark",CommonMethod.getInteger(data,"mark"));
        req.add("status", CommonMethod.getInteger(data, "status"));
        res = HttpRequestUtil.request("/api/score/scoreSave",req);
        if(res != null && res.getCode()== 0) {
            onQueryButtonClick();
        }
    }
    @FXML
    private void onAddButtonClick() {
        initDialog();
        scoreEditController.showDialog(null);
        MainApplication.setCanClose(false);
        stage.showAndWait();
    }
    @FXML
    private void onEditButtonClick() {
//        dataTableView.getSelectionModel().getSelectedItems();

        Score data = dataTableView.getSelectionModel().getSelectedItem();
        if(data == null) {
            MessageDialog.showDialog("没有选中，不能修改！");
            return;
        }
        initDialog();
        scoreEditController.showDialog(data);
        MainApplication.setCanClose(false);
        stage.showAndWait();
    }
    @FXML
    private void onDeleteButtonClick() {
        Score form = dataTableView.getSelectionModel().getSelectedItem();
        if(form == null) {
            MessageDialog.showDialog("没有选择，不能删除");
            return;
        }
        int ret = MessageDialog.choiceDialog("确认要删除吗?");
        if(ret != MessageDialog.CHOICE_YES) {
            return;
        }
        Integer scoreId = form.getScoreId();
        DataRequest req = new DataRequest();
        req.add("scoreId", scoreId);
        DataResponse res = HttpRequestUtil.request("/api/score/scoreDelete",req);
        if(res.getCode() == 0) {
            onQueryButtonClick();
        }
        else {
            MessageDialog.showDialog(res.getMsg());
        }
    }

}