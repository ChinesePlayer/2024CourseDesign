package com.teach.javafx.controller.teacherCourse;

import com.teach.javafx.MainApplication;
import com.teach.javafx.controller.base.MessageController;
import com.teach.javafx.controller.base.MessageDialog;
import com.teach.javafx.request.HttpRequestUtil;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.MapValueFactory;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.apache.batik.apps.rasterizer.Main;
import org.apache.pdfbox.pdmodel.font.encoding.MacOSRomanEncoding;
import org.fatmansoft.teach.models.Course;
import org.fatmansoft.teach.payload.request.DataRequest;
import org.fatmansoft.teach.payload.response.DataResponse;
import org.fatmansoft.teach.util.CommonMethod;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StudentViewerController {
    @FXML
    public TableView<Map> studentTableView;
    @FXML
    public TableColumn<Map, String> studentNum;
    @FXML
    public TableColumn<Map, String> studentName;
    @FXML
    public TableColumn<Map, String> className;
    @FXML
    public TableColumn<Map, String> mark;
    @FXML
    public TableColumn<Map, String> rank;
    @FXML
    public TableColumn<Map, String> status;
    @FXML
    public TableColumn<Map, HBox> action;

    private ObservableList<Map> observableList = FXCollections.observableArrayList();
    private List<Map> studentInfoMaps = new ArrayList<>();
    private Course course;

    public static final String STUDENT_NUM = "studentNum";
    public static final String STUDENT_NAME = "studentName";
    public static final String CLASS_NAME = "className";
    public static final String MARK = "mark";
    public static final String RANK = "rank";
    public static final String STATUS = "status";
    public static final String STUDENT_ID = "studentId";
    public static final String ACTION = "action";
    public static final String SCORE_ID = "scoreId";

    @FXML
    public void initialize(){
        studentNum.setCellValueFactory(new MapValueFactory<>(STUDENT_NUM));
        studentName.setCellValueFactory(new MapValueFactory<>(STUDENT_NAME));
        className.setCellValueFactory(new MapValueFactory<>(CLASS_NAME));
        mark.setCellValueFactory(new MapValueFactory<>(MARK));
        rank.setCellValueFactory(new MapValueFactory<>(RANK));
        status.setCellValueFactory(new MapValueFactory<>(STATUS));
        action.setCellValueFactory(new MapValueFactory<>(ACTION));
    }

    public void getStudentData(Integer courseId){
        DataRequest req = new DataRequest();
        req.add("courseId", courseId);
        DataResponse res = HttpRequestUtil.request("/api/teacher/getStudentList", req);
        assert res != null;
        if(res.getCode() == 0){
            List<Map> rawData = (ArrayList<Map>)res.getData();
            for(Map m : rawData){
                //从后端传来的数据中提取数据
                studentInfoMaps.add(processRawData(m));
            }
        }
    }

    public void updateViewData(Integer courseId){
        observableList.clear();
        studentInfoMaps.clear();
        getStudentData(courseId);
        observableList.addAll(studentInfoMaps);
        studentTableView.setItems(observableList);
    }

    //由外部调用
    public void init(Course c){
        updateViewData(c.getCourseId());
        course = c;
    }

    //将后端传来的数据加工成可用数据
    public Map processRawData(Map m){
        Map map = new HashMap<>();
        map.put(STUDENT_NUM, m.get(STUDENT_NUM));
        map.put(STUDENT_NAME, m.get(STUDENT_NAME));
        map.put(CLASS_NAME, m.get(CLASS_NAME));
        map.put(STUDENT_ID, m.get(STUDENT_ID));
        map.put(SCORE_ID, m.get(SCORE_ID));
        String mark = CommonMethod.getString(m, MARK);
        String rank = CommonMethod.getString(m, RANK);
        Integer status = CommonMethod.getInteger(m, STATUS);
        if(mark == null || mark.isEmpty()){
            map.put(MARK, "暂无");
        }
        else{
            map.put(MARK, mark);
        }

        if(rank == null || rank.isEmpty()){
            map.put(RANK, "暂无");
        }
        else{
            map.put(RANK, rank);
        }

        if(status == null){
            map.put(STATUS, "暂无");
        }
        else{
            String statusInfo = "暂无";
            if(status == 1){
                statusInfo = "已及格";
            }
            else if(status == 0){
                statusInfo = "修读中";
            }
            else if(status == 2){
                statusInfo = "不及格";
            }
            map.put(STATUS, statusInfo);
        }
        //编辑按钮
        Button edit = new Button("编辑");
        edit.setOnAction(this::onEditButton);
        //退课按钮
        Button quit = new Button("退课");
        quit.setOnAction(this::onQuitButton);
        //查看作业
        Button checkHomeWork = new Button("查看作业");
        HBox buttonList = new HBox();
        buttonList.setAlignment(Pos.CENTER);
        buttonList.setSpacing(5);
        buttonList.getChildren().addAll(edit, quit);

        map.put(ACTION, buttonList);
        return map;
    }

    public void onEditButton(ActionEvent event){
        //获取当行数据
        Map m = (Map) CommonMethod.getRowValue(event, 2, studentTableView);
        //TODO: 打开编辑页面
        System.out.println("打开编辑页面!");
        FXMLLoader loader = new FXMLLoader(MainApplication.class.getResource("teacherCourse/student-score-edit.fxml"));
        try{
            Scene scene = new Scene(loader.load(), 600, 500);
            StudentScoreEditController controller = loader.getController();
            controller.init(m);
            controller.controller = this;
            Stage stage = new Stage();
            stage.setTitle("编辑: " + m.get(STUDENT_NAME));
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.show();
        }
        catch (IOException e){
            e.printStackTrace();
            MessageDialog.showDialog("打开编辑页面失败! ");
        }
    }

    public void onHasSaved(Map newData){
        updateViewData(course.getCourseId());
    }

    //退课按钮，为学生退课
    public void onQuitButton(ActionEvent event){
        int ret = MessageDialog.choiceDialog("你确定要为该学生退选: " + course.getName() + " 吗?");
        if(ret != MessageDialog.CHOICE_YES){
            return;
        }
        //TODO: 为学生退选
        System.out.println("退选! ");
    }
}
