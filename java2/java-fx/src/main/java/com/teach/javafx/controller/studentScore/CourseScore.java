package com.teach.javafx.controller.studentScore;

import com.teach.javafx.controller.courseSelection.CourseActionValueFactory;
import com.teach.javafx.controller.courseSelection.CourseTimeValueFactory;
import com.teach.javafx.controller.courseSelection.CourseValueFactory;
import com.teach.javafx.request.HttpRequestUtil;
import io.github.palexdev.materialfx.controls.MFXButton;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.util.Callback;
import org.fatmansoft.teach.models.Course;
import org.fatmansoft.teach.models.CourseLocation;
import org.fatmansoft.teach.models.Teacher;
import org.fatmansoft.teach.payload.request.DataRequest;
import org.fatmansoft.teach.payload.response.DataResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CourseScore {
    @FXML
    public TableView<Course> scoreTableView;
    @FXML
    public TableColumn<Course, String> courseName;
    @FXML
    public TableColumn<Course, String> courseNum;
    @FXML
    public TableColumn<Course, String> credit;
    @FXML
    public TableColumn<Course, String> days;
    @FXML
    public TableColumn<Course, String> sections;
    @FXML
    public TableColumn<Course, String> teacher;
    @FXML
    public TableColumn<Course, String> loc;
    @FXML
    public TableColumn<Course, String> preCourse;
    @FXML
    public TableColumn<Course, MFXButton> action;

    //正在修读的全部课程
    private List<Course> courseList = new ArrayList<>();
    //已及格的课程
    private List<Course> passedCourseList = new ArrayList<>();
    //修读中的课程
    private List<Course> readingCourseList = new ArrayList<>();
    //不及格的课程
    private List<Course> failedCourseList = new ArrayList<>();

//    //从后端获取该学生所选的所有课程
//    public void getCourseList(){
//        DataRequest req = new DataRequest();
//        req.add("id", turnId);
//        DataResponse res = HttpRequestUtil.request("/api/course/getCourseChoices", req);
//        if (res != null && res.getCode() == 0) {
//            List<Map> rawData = (ArrayList<Map>)res.getData();
//            for(Map m : rawData){
//                Course c = new Course(m);
//                MFXButton button = new MFXButton("选课");
//                button.setOnAction(this::onChooseButton);
//                c.setAction(button);
//                courses.add(c);
//                //根据是否选中将课程分配到已选和未选两个List中
//                if(c.getChosen()){
//                    chosenCourse.add(c);
//                }
//                else {
//                    unchosenCourse.add(c);
//                }
//            }
//            updateButtonStatus();
//            courseTable.addAllCourse(chosenCourse, null);
//        }
//    }

    @FXML
    public void initialize(){
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
        //设置无可程可选时的占位组件
        Label placeholder = new Label("暂无可选课程 ¯\\_(ツ)_/¯");
        placeholder.setStyle("-fx-font-size: 30px");
        scoreTableView.setPlaceholder(placeholder);
    }
}
