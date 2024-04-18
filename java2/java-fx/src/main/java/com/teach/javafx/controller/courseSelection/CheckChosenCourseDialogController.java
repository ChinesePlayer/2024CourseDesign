package com.teach.javafx.controller.courseSelection;

import com.teach.javafx.controller.base.MessageDialog;
import com.teach.javafx.controller.courseSelection.CourseSelectionController;
import com.teach.javafx.request.HttpRequestUtil;
import io.github.palexdev.materialfx.controls.MFXButton;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.MapValueFactory;
import javafx.util.Callback;
import org.fatmansoft.teach.payload.request.DataRequest;
import org.fatmansoft.teach.payload.response.DataResponse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CheckChosenCourseDialogController {
    @FXML
    public TableView<Map> courseTableView;
    @FXML
    public TableColumn<Map,String> courseName;
    @FXML
    public TableColumn<Map, String> courseNum;
    @FXML
    public TableColumn<Map, String> credit;
    @FXML
    public TableColumn<Map, MFXButton> action;

    private List<Map> courses = new ArrayList<>();
    private ObservableList<Map> observableList = FXCollections.observableArrayList();

    private CourseSelectionController courseSelectionController;

    private ArrayList<Map> deepCopy(ArrayList<Map> origin){
        ArrayList<Map> newList = new ArrayList<>();
        for(Map m : origin){
            newList.add(new HashMap(m));
        }
        return newList;
    }

    public void initData(){
        courses.clear();
        courses = deepCopy((ArrayList<Map>)courseSelectionController.getChosenCourse());
        for(Map m : courses){
            MFXButton button = new MFXButton("退选");
            button.setOnAction(this::onCancelButtonPressed);
            m.put("action", button);
        }
        setTableViewData();
    }

    public void setTableViewData(){
        observableList.addAll(FXCollections.observableArrayList(courses));
        courseTableView.setItems(observableList);
    }

    @FXML
    public void initialize(){
        courseName.setCellValueFactory(new MapValueFactory<>("name"));
        courseNum.setCellValueFactory(new MapValueFactory<>("num"));
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

    public CourseSelectionController getCourseSelectionController() {
        return courseSelectionController;
    }

    public void setCourseSelectionController(CourseSelectionController courseSelectionController) {
        this.courseSelectionController = courseSelectionController;
    }

    //退选按钮按下时的回调
    public void onCancelButtonPressed(ActionEvent event){
        MFXButton button = (MFXButton) event.getTarget();
        TableCell<Map, MFXButton> cell = (TableCell<Map, MFXButton>) button.getParent();
        int rowIndex = cell.getIndex();
        Map m = observableList.get(rowIndex);
        Integer courseId = Integer.parseInt(String.valueOf(m.get("courseId")));
        if(courseId == null){
            MessageDialog.showDialog("退选失败: 无法找到该课程! ");
        }
        int ret = MessageDialog.choiceDialog("你确定要退选: " + m.get("name") + " 吗");
        if(ret != MessageDialog.CHOICE_YES){
            return;
        }
        DataRequest req = new DataRequest();
        req.add("courseId", courseId);
        DataResponse res = HttpRequestUtil.request("/api/course/cancelCourse", req);
        if(res == null){
            MessageDialog.showDialog("选课失败, 请检查网络设置! ");
            return;
        }
        if(res.getCode() == 0){
            //将退选课程从已选列表中删除
            observableList.remove(rowIndex);
            //交给courseSelectionController处理剩下的事务
            courseSelectionController.onHasCanceledCourse(courseId);
            MessageDialog.showDialog("成功退选: " + m.get("name"));
        }
        else {
            MessageDialog.showDialog(res.getMsg());
        }
    }
}
