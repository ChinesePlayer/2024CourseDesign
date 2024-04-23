package com.teach.javafx.controller.courseSelection;

import com.teach.javafx.MainApplication;
import com.teach.javafx.controller.base.MessageDialog;
import com.teach.javafx.request.HttpRequestUtil;
import io.github.palexdev.materialfx.controls.MFXButton;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.LoadException;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.fatmansoft.teach.models.SelectionTurn;
import org.fatmansoft.teach.payload.request.DataRequest;
import org.fatmansoft.teach.payload.response.DataResponse;
import org.w3c.dom.CDATASection;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;

public class CourseSelectionMenuController {
    private final String BASE_URL = "/api/course";

    //实际数据
    private List<SelectionTurn> turns = new ArrayList<>();

    //显示数据
    private List<Map> rows = new ArrayList<>();

    //选课窗口
    private Stage stage = null;

    @FXML
    private MFXButton refreshButton;
    @FXML
    private GridPane gridPane;

    @FXML
    public void initialize(){
        setDisplay();
    }

    private String url(String url){
        return BASE_URL+url;
    }

    private SelectionTurn findTurnById(Integer id){
        for(SelectionTurn t : turns){
            if(Objects.equals(t.getId(), id)){
                return t;
            }
        }
        return null;
    }

    private String getDateString(LocalDateTime dateTime){
        String res = "";
        res += dateTime.getYear() + "年";
        res += dateTime.getMonthValue() + "月";
        res += dateTime.getDayOfMonth() + "日 ";
        res += dateTime.getHour() + ":";
        res += dateTime.getMinute();
        return res;
    }

    //从后端获取选课轮次信息
    //并刷新显示数据的信息
    public void onRefresh(){
        DataRequest req = new DataRequest();
        DataResponse res = HttpRequestUtil.request(url("/getAllTurns"),req);
        if(res != null && res.getCode() == 0){
            System.out.println("请求到的选课轮次数据: " + res.getData());
            List<Map> turnMap = (ArrayList<Map>) res.getData();
            for(Map m : turnMap){
                SelectionTurn newTurn = new SelectionTurn(m);
                Map rowMap = new HashMap<>();
                MFXButton button = new MFXButton("进入");
                if(!newTurn.isValid()){
                    //设置为禁用状态
                    button.setDisable(true);
                }
                button.setOnAction(event -> {
                    startChoose(newTurn.getId());
                });
                rowMap.put("name", new Label(newTurn.getName()));
                rowMap.put("start",new Label(getDateString(newTurn.getStart())));
                rowMap.put("end",new Label(getDateString(newTurn.getEnd())));
                rowMap.put("action", button);
                turns.add(newTurn);
                rows.add(rowMap);
            }
        }
    }

    //设置显示数据
    public void setDisplay(){
        onRefresh();
        gridPane.add(new Label("选课信息"), 0, 0);
        gridPane.add(new Label("开始时间"), 1, 0);
        gridPane.add(new Label("结束时间"), 2, 0);
        gridPane.add(new Label("操作"), 3, 0);
        for(int i = 0; i < rows.size(); i++){
            gridPane.add((Label)rows.get(i).get("name"), 0, i+1);
            gridPane.add((Label)rows.get(i).get("start"), 1, i+1);
            gridPane.add((Label)rows.get(i).get("end"), 2, i+1);
            gridPane.add((MFXButton) rows.get(i).get("action"), 3, i+1);
        }
    }

    //打开选课页面，请求课程数据，开始选课
    public void startChoose(Integer turnId){
        DataRequest req = new DataRequest();
        req.add("id", turnId);
        DataResponse res = HttpRequestUtil.request(url("/getCourseChoices"), req);
        if(res != null && res.getCode() == 0){
            System.out.println("startChoose: 进入选课: " + res.getData());
            FXMLLoader loader = new FXMLLoader(MainApplication.class.getResource("courseSelection/course-selection.fxml"));
            try{
                Scene scene = new Scene(loader.load(), 700, 400);
                CourseSelectionController selectionController = (CourseSelectionController) loader.getController();
                selectionController.setTurnId(turnId);
                selectionController.update();
                stage = new Stage();
                stage.setScene(scene);
                stage.setResizable(false);
                stage.setMaximized(true);
                stage.setTitle(findTurnById(turnId).getName());
                stage.initOwner(gridPane.getScene().getWindow());
                stage.initModality(Modality.WINDOW_MODAL);
                stage.setOnCloseRequest(windowEvent -> stage = null);
                stage.show();
                System.out.println("选课页面已显示");
            }
            catch (IOException o){
                System.out.println("加载选课页面失败: " + o.getCause());
                o.printStackTrace();
            }
        }
        else {
            MessageDialog.showDialog(res.getMsg());
        }
    }
}
