package com.teach.javafx.controller;

import com.teach.javafx.controller.base.MessageDialog;
import com.teach.javafx.request.HttpRequestUtil;
import io.github.palexdev.materialfx.controls.MFXButton;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.MapValueFactory;
import javafx.util.Callback;
import org.fatmansoft.teach.models.Course;
import org.fatmansoft.teach.payload.request.DataRequest;
import org.fatmansoft.teach.payload.response.DataResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


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

    private List<Map> courses = new ArrayList<>();
    private List<Map> chosenCourse = new ArrayList<>();
    private ObservableList<Map> observableList = FXCollections.observableArrayList();

    public void setTableViewData() {
        observableList.clear();
        for (Map m : courses) {
            MFXButton button = new MFXButton("选课");
            button.setOnAction(this::onChooseButton);
            m.put("action", button);
            observableList.addAll(FXCollections.observableArrayList(m));
        }
        courseTableView.setItems(observableList);
    }

    //通过网络请求获得所有课程
    public void getCourses() {
        DataRequest req = new DataRequest();
        DataResponse res = HttpRequestUtil.request("/api/course/getCourseChoices", req);
        if (res != null && res.getCode() == 0) {
            courses = (ArrayList<Map>) res.getData();
        }
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
            Map preCourse = findCourseById(preCourseId);
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

        getCourses();
        setTableViewData();
    }

    public void onChooseButton(ActionEvent event){
        MFXButton button = (MFXButton) event.getTarget();
        TableCell<Map,MFXButton> cell = (TableCell<Map, MFXButton>) button.getParent();
        int rowIndex = cell.getIndex();
        //获取所点击的按钮对应的行的所有数据
        Map m = observableList.get(rowIndex);
        int ret = MessageDialog.choiceDialog("你确定要选择: " + m.get("name") + " 吗");
        if(ret != MessageDialog.CHOICE_YES){
            System.out.println("取消选课了");
            return;
        }
        Integer courseId = Integer.parseInt((String)m.get("courseId"));
        //向后端发送网络请求，后端根据课程ID为该学生选课并返回是否选课成功
        DataRequest req = new DataRequest();
        req.add("courseId", courseId);
        DataResponse res = HttpRequestUtil.request("/api/course/chooseCourse", req);
        System.out.println(res);
        if(res != null && res.getCode() == 0){
            MessageDialog.showDialog("选课成功! ");
            System.out.println(res);
        }
        else {
            MessageDialog.showDialog(res.getMsg());
        }
    }
}
