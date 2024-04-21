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
import org.fatmansoft.teach.models.Course;
import org.fatmansoft.teach.payload.request.DataRequest;
import org.fatmansoft.teach.payload.response.DataResponse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CheckChosenCourseDialogController {
    @FXML
    public TableView<Course> courseTableView;
    @FXML
    public TableColumn<Course,String> courseName;
    @FXML
    public TableColumn<Course, String> courseNum;
    @FXML
    public TableColumn<Course, String> credit;
    @FXML
    public TableColumn<Course, String> preCourse;
    @FXML
    public TableColumn<Course, MFXButton> action;
    private List<Course> courses = new ArrayList<>();
    private ObservableList<Course> observableList = FXCollections.observableArrayList();
    private CourseSelectionController courseSelectionController;

    private ArrayList<Course> deepCopy(ArrayList<Course> origin){
        ArrayList<Course> newList = new ArrayList<>();
        for(Course c : origin){
            newList.add(new Course(c));
        }
        return newList;
    }

    public void initData(){
        courses.clear();
        courses = deepCopy((ArrayList<Course>)courseSelectionController.getChosenCourse());
        for(Course c : courses){
            MFXButton button = new MFXButton("退选");
            button.setOnAction(this::onCancelButtonPressed);
            c.setAction(button);
        }
        setTableViewData();
    }

    public void setTableViewData(){
        observableList.addAll(FXCollections.observableArrayList(courses));
        courseTableView.setItems(observableList);
    }

    @FXML
    public void initialize(){
        courseName.setCellValueFactory(new CourseValueFactory());
        courseNum.setCellValueFactory(new CourseValueFactory());
        preCourse.setCellValueFactory(new CourseValueFactory());
        credit.setCellValueFactory(new CourseValueFactory());
        action.setCellValueFactory(new CourseActionValueFactory());
        //设置按钮所在单元格为居中显示
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

    public void setCourseSelectionController(CourseSelectionController courseSelectionController) {
        this.courseSelectionController = courseSelectionController;
    }

    //退选按钮按下时的回调
    public void onCancelButtonPressed(ActionEvent event){
        MFXButton button = (MFXButton) event.getTarget();
        TableCell<Course, MFXButton> cell = (TableCell<Course, MFXButton>) button.getParent();
        int rowIndex = cell.getIndex();
        Course c = observableList.get(rowIndex);
        Integer courseId = c.getCourseId();
        if(courseId == null){
            MessageDialog.showDialog("退选失败: 无法找到该课程! ");
        }
        int ret = MessageDialog.choiceDialog("你确定要退选: " + c.getName() + " 吗");
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
            MessageDialog.showDialog("成功退选: " + c.getName());
        }
        else {
            MessageDialog.showDialog(res.getMsg());
        }
    }
}
