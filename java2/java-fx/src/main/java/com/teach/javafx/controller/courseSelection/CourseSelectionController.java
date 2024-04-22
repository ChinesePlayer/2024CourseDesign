package com.teach.javafx.controller.courseSelection;

import com.teach.javafx.MainApplication;
import com.teach.javafx.controller.base.MessageDialog;
import com.teach.javafx.customWidget.CourseTable;
import com.teach.javafx.request.HttpRequestUtil;
import io.github.palexdev.materialfx.controls.MFXButton;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import org.fatmansoft.teach.models.Course;
import org.fatmansoft.teach.payload.request.DataRequest;
import org.fatmansoft.teach.payload.response.DataResponse;

import java.io.IOException;
import java.util.*;

enum ChosenFilter {
    UNCHOSEN,
    CHOSEN,
    ALL
}


public class CourseSelectionController {
    @FXML
    public TableView<Course> courseTableView;
    @FXML
    public TableColumn<Course, String> courseName;
    @FXML
    public TableColumn<Course, String> courseNum;
    @FXML
    public TableColumn<Course, String> credit;
    @FXML
    public TableColumn<Course, String> preCourse;
    @FXML
    public TableColumn<Course, String> loc;
    @FXML
    public TableColumn<Course, MFXButton> action;
    @FXML
    public TableColumn<Course, String> days;
    @FXML
    public TableColumn<Course, String> sections;
    @FXML
    public TableColumn<Course, String> teacher;


    //当前选课轮次的ID
    private Integer turnId= null;
    private List<Course> courses = new ArrayList<>();
    private List<Course> chosenCourse = new ArrayList<>();
    private List<Course> unchosenCourse = new ArrayList<>();
    //用于储存全部课程列表
    private ObservableList<Course> observableList = FXCollections.observableArrayList();
    //用于储存筛选后的课程列表
    private ObservableList<Course> filteredObservableList = FXCollections.observableArrayList();
    private CheckChosenCourseDialogController checkChosenCourseDialogController;
    private Stage stage = null;

    //根据课程num对课程进行排序
    public void sortList(List<Course> list){
        list.sort(Comparator.comparing(Course::getNum));
    }

    public void sortCourse(){
        sortList(courses);
    }

    public void sortChosen(){
        sortList(chosenCourse);
    }

    public void sortUnchosen(){
        sortList(unchosenCourse);
    }

    public void sortAll(){
        sortCourse();
        sortChosen();
        sortUnchosen();
    }

    //将该学生未选的课程展示出来
    public void setTableViewData() {
        setTableViewData(ChosenFilter.UNCHOSEN);
    }

    //根据筛选条件进行显示
    public void setTableViewData(ChosenFilter chosenFilter) {
        observableList.clear();
        List<Course> readyToShowCourseList = new ArrayList<>();
        if(chosenFilter == ChosenFilter.CHOSEN){
            readyToShowCourseList = chosenCourse;
        }
        else if(chosenFilter == ChosenFilter.UNCHOSEN){
            readyToShowCourseList = unchosenCourse;
        }
        else if(chosenFilter == ChosenFilter.ALL){
            readyToShowCourseList = courses;
        }
        observableList.addAll(FXCollections.observableArrayList(readyToShowCourseList));
        courseTableView.setItems(observableList);
    }

    //通过网络请求获得所有课程,
    public void getCourses() {
        DataRequest req = new DataRequest();
        req.add("id", turnId);
        DataResponse res = HttpRequestUtil.request("/api/course/getCourseChoices", req);
        if (res != null && res.getCode() == 0) {
            System.out.println("当前课程数据: " + res.getData());
            List<Map> rowData = (ArrayList<Map>)res.getData();
            for(Map m : rowData){
                Course c = new Course(m);
                MFXButton button = new MFXButton("选课");
                button.setOnAction(this::onChooseButton);
                c.setAction(button);
                courses.add(c);
                System.out.println(c);
                //根据是否选中将课程分配到已选和未选两个List中
                if(c.getChosen()){
                    chosenCourse.add(c);
                }
                else {
                    unchosenCourse.add(c);
                }
            }
        }
    }

    public void update(){
        getCourses();
        setTableViewData();
    }

    @FXML
    public void initialize() {
        courseName.setCellValueFactory(new CourseValueFactory());
        courseNum.setCellValueFactory(new CourseValueFactory());
        preCourse.setCellValueFactory(new CourseValueFactory());
        credit.setCellValueFactory(new CourseValueFactory());
        loc.setCellValueFactory(new CourseValueFactory());
        days.setCellValueFactory(new CourseTimeValueFactory());
        sections.setCellValueFactory(new CourseTimeValueFactory());
        teacher.setCellValueFactory(new CourseValueFactory());
        action.setCellValueFactory(new CourseActionValueFactory());
        action.setCellFactory(new Callback<>() {
            @Override
            public TableCell<Course, MFXButton> call(TableColumn<Course, MFXButton> courseMFXButtonTableColumn) {
                TableCell<Course, MFXButton> cell = new TableCell<>() {
                    @Override
                    protected void updateItem(MFXButton item, boolean empty) {
                        super.updateItem(item, empty);
                        if (item == null || empty) {
                            setText(null);
                            setGraphic(null);
                            return;
                        }
                        setText(null);
                        setGraphic(item);
                    }
                };
                cell.setAlignment(Pos.CENTER);
                return cell;
            }
        });
    }

    public void onChooseButton(ActionEvent event){
        MFXButton button = (MFXButton) event.getTarget();
        TableCell<Course,MFXButton> cell = (TableCell<Course, MFXButton>) button.getParent();
        int rowIndex = cell.getIndex();
        //获取所点击的按钮对应的行的所有数据
        Course c = observableList.get(rowIndex);
        int ret = MessageDialog.choiceDialog("你确定要选择: " + c.getName() + " 吗");
        System.out.println("选择的结果: " + ret);
        if(ret != MessageDialog.CHOICE_YES){
            System.out.println("取消选课了");
            return;
        }
        Integer courseId = c.getCourseId();
        //向后端发送网络请求，后端根据课程ID为该学生选课并返回是否选课成功
        DataRequest req = new DataRequest();
        req.add("courseId", courseId);
        DataResponse res = HttpRequestUtil.request("/api/course/chooseCourse", req);
        if(res != null && res.getCode() == 0){
            MessageDialog.showDialog("选课成功! ");
            //将该数据加入到已选课程中
            chosenCourse.add(c);
            //从未选课程中移除
            unchosenCourse.remove(c);
            //更新列表
            setTableViewData();
        }
        else {
            MessageDialog.showDialog(res.getMsg());
        }
    }

    //当"查看已选课程按钮"按下时的回调
    public void onCheckChosenButtonPressed(){
        if(this.stage != null){
            return;
        }
        FXMLLoader loader = new FXMLLoader(MainApplication.class.getResource("courseSelection/check-chosen-course-dialog.fxml"));
        try{
            Scene scene = new Scene(loader.load(), 700, 400);
            checkChosenCourseDialogController = (CheckChosenCourseDialogController) loader.getController();
            checkChosenCourseDialogController.setCourseSelectionController(this);
            checkChosenCourseDialogController.initData();
            stage = new Stage();
            stage.setTitle("已选课程");
            stage.setScene(scene);
            stage.setResizable(false);
            stage.initOwner(MainApplication.getMainStage());
            stage.initModality(Modality.WINDOW_MODAL);
            stage.setOnCloseRequest(windowEvent -> {
                stage = null;
            });
            stage.show();
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Course> getChosenCourse(){
        sortChosen();
        return this.chosenCourse;
    }
    public Stage getStage(){
        return this.stage;
    }

    public void onHasCanceledCourse(Integer courseId){
        System.out.println("当前退选的Id: "+courseId);
        for(Course c : chosenCourse){
            if(Objects.equals(c.getCourseId(), courseId)){
                chosenCourse.remove(c);
                unchosenCourse.add(c);
                sortAll();
                setTableViewData();
                break;
            }
        }

    }

    public Integer getTurnId() {
        return turnId;
    }

    public void setTurnId(Integer turnId) {
        this.turnId = turnId;
    }

    //请求前序课程信息，返回的数据中包含：前序课程名称，前序课程num号
    private Map getPreCourse(Integer preCourseId){
        DataRequest req = new DataRequest();
        req.add("courseId", preCourseId);
        DataResponse res = HttpRequestUtil.request("/api/course/getCourse", req);
        if(res != null && res.getCode() == 0){
            return (Map) res.getData();
        }
        else {
            return null;
        }
    }
}