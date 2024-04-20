package com.teach.javafx.controller.courseSelection;

import com.teach.javafx.MainApplication;
import com.teach.javafx.controller.base.MessageDialog;
import com.teach.javafx.customWidget.CourseTable;
import com.teach.javafx.request.HttpRequestUtil;
import io.github.palexdev.materialfx.controls.MFXButton;
import javafx.beans.property.SimpleStringProperty;
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
import javafx.scene.control.cell.MapValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
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
    public TableView<Map> courseTableView;
    @FXML
    public TableColumn<Map, String> courseName;
    @FXML
    public TableColumn<Map, String> courseNum;
    @FXML
    public TableColumn<Map, String> credit;
    @FXML
    public TableColumn<Map, String> preCourse;
    @FXML
    public TableColumn<Map, MFXButton> action;
    @FXML
    public CourseTable courseTable;

    //当前选课轮次的ID
    private Integer turnId= null;

    private List<Map> courses = new ArrayList<>();
    private List<Map> chosenCourse = new ArrayList<>();
    private List<Map> unchosenCourse = new ArrayList<>();
    //用于储存全部课程列表
    private ObservableList<Map> observableList = FXCollections.observableArrayList();
    //用于储存筛选后的课程列表
    private ObservableList<Map> filteredObservableList = FXCollections.observableArrayList();
    private CheckChosenCourseDialogController checkChosenCourseDialogController;
    private Stage stage = null;

    public void sortList(List<Map> list){
        list.sort(new Comparator<Map>() {
            @Override
            public int compare(Map o1, Map o2) {
                return ((String) o1.get("num")).compareTo((String) o2.get("num"));
            }
        });
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
        observableList.clear();
//        for (Map m : courses) {
//            if(chosenCourse.contains(m)){
//                continue;
//            }
//            MFXButton button = new MFXButton("选课");
//            button.setOnAction(this::onChooseButton);
//            m.put("action", button);
//            observableList.addAll(FXCollections.observableArrayList(m));
//        }
        observableList.addAll(FXCollections.observableArrayList(unchosenCourse));
        courseTableView.setItems(observableList);
    }

    //根据筛选条件进行显示
    public void setTableViewData(ChosenFilter chosenFilter) {
        observableList.clear();
        List<Map> readyToShowCourseList = new ArrayList<>();
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

    //通过网络请求获得所有课程, 并且将已选课程放入chosenCourse列表中
    public void getCourses() {
        DataRequest req = new DataRequest();
        req.add("id", turnId);
        DataResponse res = HttpRequestUtil.request("/api/course/getCourseChoices", req);
        if (res != null && res.getCode() == 0) {
            courses = (ArrayList<Map>) res.getData();
        }
        //将已选课程添加到chosenCourse中
        for(Map m : courses){
            MFXButton button = new MFXButton("选课");
            button.setOnAction(this::onChooseButton);
            m.put("action", button);
            if((Boolean) m.get("isChosen")){
                chosenCourse.add(m);
            }
            else {
                unchosenCourse.add(m);
            }
        }
    }

    public void update(){
        getCourses();
        setTableViewData();
    }

    public Map findCourseById(Integer id) {
        for (Map c : courses) {
            if (c.get("courseId").equals(String.valueOf(id))) {
                return c;
            }
        }
        return null;
    }

    @FXML
    public void initialize() {
        courseName.setCellValueFactory(new MapValueFactory<>("name"));
        courseNum.setCellValueFactory(new MapValueFactory<>("num"));
        preCourse.setCellValueFactory(cellData -> {
            if (cellData == null) {
                return null;
            }
            Map value = (Map) cellData.getValue();
            if (value.get("preCourseId") == null) {
                return new SimpleStringProperty("无");
            }
            Integer preCourseId = Integer.parseInt((String) value.get("preCourseId"));
            if(preCourseId == null){
                return new SimpleStringProperty("无");
            }
            Map preCourse = getPreCourse(preCourseId);
            if (preCourse == null) {
                return new SimpleStringProperty("无");
            }
            return new SimpleStringProperty(preCourse.get("num") + "-" + preCourse.get("name"));
        });
        credit.setCellValueFactory(new MapValueFactory<>("credit"));
        action.setCellValueFactory(new MapValueFactory<>("action"));
        //设置按钮所在单元格为居中显示
        action.setCellFactory(new Callback<>() {
            @Override
            public TableCell<Map, MFXButton> call(TableColumn<Map, MFXButton> mapButtonTableColumn) {
                TableCell<Map, MFXButton> cell = new TableCell<>() {
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
        TableCell<Map,MFXButton> cell = (TableCell<Map, MFXButton>) button.getParent();
        int rowIndex = cell.getIndex();
        //获取所点击的按钮对应的行的所有数据
        Map m = observableList.get(rowIndex);
        int ret = MessageDialog.choiceDialog("你确定要选择: " + m.get("name") + " 吗");
        System.out.println("选择的结果: " + ret);
        if(ret != MessageDialog.CHOICE_YES){
            System.out.println("取消选课了");
            return;
        }
        Integer courseId = Integer.parseInt((String)m.get("courseId"));
        //向后端发送网络请求，后端根据课程ID为该学生选课并返回是否选课成功
        DataRequest req = new DataRequest();
        req.add("courseId", courseId);
        DataResponse res = HttpRequestUtil.request("/api/course/chooseCourse", req);
        if(res != null && res.getCode() == 0){
            MessageDialog.showDialog("选课成功! ");
            //将该数据加入到已选课程中
            chosenCourse.add(m);
            //从未选课程中移除
            unchosenCourse.remove(m);
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

    public List<Map> getChosenCourse(){
        sortChosen();
        return this.chosenCourse;
    }
    public Stage getStage(){
        return this.stage;
    }

    public void onHasCanceledCourse(Integer courseId){
        System.out.println("当前退选的Id: "+courseId);
        for(Map m : chosenCourse){
            if(Objects.equals(Integer.parseInt((String) m.get("courseId")), courseId)){
                chosenCourse.remove(m);
                unchosenCourse.add(m);
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
