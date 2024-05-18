package com.teach.javafx.controller;

import com.teach.javafx.AppStore;
import com.teach.javafx.MainApplication;
import com.teach.javafx.controller.base.MessageDialog;
import com.teach.javafx.controller.shortcuts.ShortcutsEditorController;
import com.teach.javafx.controller.statistic.FeeStatisticController;
import com.teach.javafx.customWidget.CourseTable;
import com.teach.javafx.managers.ShortcutManager;
import com.teach.javafx.managers.ShortcutsDisplayer;
import com.teach.javafx.managers.WindowOpenAction;
import com.teach.javafx.managers.WindowsManager;
import com.teach.javafx.models.Shortcut;
import com.teach.javafx.request.HttpRequestUtil;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.stage.Modality;
import org.fatmansoft.teach.models.Course;
import org.fatmansoft.teach.payload.request.DataRequest;
import org.fatmansoft.teach.payload.response.DataResponse;
import org.fatmansoft.teach.util.CommonMethod;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

//学生仪表盘页面
public class StudentDashboardController implements ShortcutsDisplayer {
    @FXML
    public ImageView avatar;
    @FXML
    public Label studentName;
    @FXML
    public Label studentNum;
    @FXML
    public Label studentDept;
    @FXML
    public Label greetings;
    @FXML
    public BarChart<String, Number> costBarChart;
    @FXML
    public PieChart markPieChart;
    @FXML
    public CourseTable courseTable;
    @FXML
    public FlowPane shortcuts;

    //储存信息的变量
    private String name;
    private String num;
    private String dept;
    private Integer id = AppStore.getJwt().getRoleId();
    private Image img;
    private List<Course> chosenCourse = new ArrayList<>();
    //快捷操作
    private List<Button> shortActions = new ArrayList<>();

    @FXML
    public void initialize(){
        updateView();
    }

    private void initData(){
        DataRequest req = new DataRequest();
        DataResponse res = HttpRequestUtil.request("/api/student/getStudentIntroduceData", req);
        if(res != null && res.getCode() == 0){
            Map rawData = (Map) res.getData();
            Map basicInfo = (Map) rawData.get("info");
            List<Map> scoreList = (ArrayList<Map>) rawData.get("scoreList");
            List<Map> markList = (ArrayList<Map>) rawData.get("markList");
            List<Map> feeList = (ArrayList<Map>) rawData.get("feeList");
            setStudentSimpleInfo(basicInfo);
            setFeeChart(feeList);
            setMarkPie(markList);
        }
        else{
            System.out.println("请求数据失败: " + (res == null ? "无法取得请求结果" : res.getMsg()));
        }
    }

    //获取学生简要信息
    public void setStudentSimpleInfo(Map rawData){
        name = (String) rawData.get("studentName");
        num = (String) rawData.get("studentNum");
        dept = (String) rawData.get("dept");
    }

    //获取学生头像
    public void getStudentAvatar(){
        DataRequest req = new DataRequest();
        Integer studentId =  AppStore.getJwt().getRoleId();
        System.out.println();
        req.add("studentId", studentId);
        byte[] bytes = HttpRequestUtil.requestByteData("/api/base/getStudentAvatar", req);
        if(bytes != null){
            ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);
            img = new Image(inputStream);
        }
    }

    //获取该学生已选课程
    public void getChosenCourse(){
        DataRequest req = new DataRequest();
        req.add("studentId", id);
        DataResponse res = HttpRequestUtil.request("/api/course/getChosenCourse", req);
        if(res != null && res.getCode() == 0){
            List<Map> rawData = (ArrayList<Map>) res.getData();
            for(Map m : rawData){
                Course c = new Course(m);
                chosenCourse.add(c);
            }
            //将课程添加到课程表中
            courseTable.addAllCourse(chosenCourse, null);
        }
        else{
            System.out.println("获取课程信息失败: " + (res == null ? "无法与后端连接" : res.getMsg()));
        }
    }

    //显示学生消费情况
    public void setFeeChart(List<Map> rawData){
        if(rawData == null){
            return;
        }
        XYChart.Series<String, Number> seriesFee = new XYChart.Series<>();
        seriesFee.setName("日常消费");
        for(Map m : rawData){
            String rawDate = (String) m.get("title");
            seriesFee.getData().add(new XYChart.Data<>(parseDate(rawDate), Double.valueOf(m.get("value").toString())));
        }
        ObservableList<XYChart.Series<String, Number>> barChartData = FXCollections.observableArrayList();
        barChartData.add(seriesFee);
        costBarChart.setData(barChartData);
    }

    //显示学生成绩情况
    public void setMarkPie(List<Map> rawData){
        if(rawData == null){
            return;
        }
        ObservableList<PieChart.Data> pieData = FXCollections.observableArrayList();
        for(Map m : rawData){
            pieData.add(new PieChart.Data(m.get("title").toString(), Double.parseDouble(m.get("value").toString())));
        }
        markPieChart.setTitle("成绩分布");
        markPieChart.setData(pieData);
    }

    //将后端传来的时间戳转化为标准的日期
    private String parseDate(String rawDate){
        Integer year =Integer.valueOf(rawDate.substring(0,4));
        Integer month = Integer.valueOf(rawDate.substring(4, 6));
        Integer day = Integer.valueOf(rawDate.substring(6, 8));
        return year+"/"+month+"/"+day;
    }

    //设置显示数据
    public void setViewData(){
        studentName.setText("姓名: " + name);
        studentNum.setText("学号: " + num);
        studentDept.setText("学院: " + dept);
        //设置头像图片
        avatar.setImage(img);
        avatar.setFitHeight(100);
        avatar.setFitWidth(100);
        avatar.setPreserveRatio(false);
        //显示问候消息
        greetings.setText(genGreetings(name));
    }

    //更新视图信息
    public void updateView(){
        loadShortcuts();
        initData();
        getStudentAvatar();
        getChosenCourse();
        setViewData();
    }

    //根据当前时间，生成问候消息
    private String genGreetings(String greetObject){
        LocalTime time = LocalTime.now();
        int hour = time.getHour();
        if(6 <= hour && hour < 12){
            return "上午好 " + greetObject + ", 来杯咖啡清醒一下吧~ ☀";
        }
        else if(hour == 12){
            return "中午好 " + greetObject + ", 再忙也要吃午饭哦~ 🥗";
        }
        else if(13 <= hour && hour < 18){
            return "下午好 " + greetObject + ", 又是阳光明媚的一天 😊";
        }
        else if(18 <= hour && hour < 22){
            return "晚上好 " + greetObject + ", 今天的星星又出来了呢 🌟";
        }
        else if(hour >= 22){
            return "晚上好 " + greetObject + ", 夜深了, 该休息咯 🌙";
        }
        else if(0 <= hour && hour < 6){
            return "已经凌晨了 " + greetObject + ", 小夜猫一枚 🐈‍⬛";
        }
        else{
            return "我忘了时间......";
        }
    }

    //刷新课表
    public void refreshCourseTable(){
        chosenCourse.clear();
        getChosenCourse();
    }

    //加载快捷方式
    private void loadShortcuts(){
        shortActions.clear();
        shortActions = ShortcutManager.getInstance().getShortcutActions();
        shortcuts.getChildren().addAll(FXCollections.observableArrayList(shortActions));
    }

    //查看详细消费数据时
    public void onCheckDetailFee(){
        try{
            FXMLLoader loader = new FXMLLoader(MainApplication.class.getResource("statistic/fee-statistic.fxml"));
            WindowsManager.getInstance().openNewWindow(
                    loader, 800, 700, "生活消费数据",
                    courseTable.getScene().getWindow(), Modality.WINDOW_MODAL,
                    null
            );
        }
        catch (IOException e){
            e.printStackTrace();
            MessageDialog.showDialog("打开生活消费数据统计页面失败");
        }
    }

    //编辑快捷操作
    //负责和ShortcutManager交互
    public void onEditShortcuts(){
        //打开编辑窗口
        FXMLLoader loader = new FXMLLoader(MainApplication.class.getResource("shortcuts-editor.fxml"));
        try{
            WindowsManager.getInstance().openNewWindow(
                    loader, 800, 600, "编辑快捷设置",
                    shortcuts.getScene().getWindow(), Modality.WINDOW_MODAL,
                    new WindowOpenAction() {
                        @Override
                        public void init(Object controller) {
                            WindowOpenAction.super.init(controller);
                            ShortcutsEditorController cont = (ShortcutsEditorController) controller;
                            cont.init(StudentDashboardController.this);
                        }
                    }
            );
        }
        catch (IOException e){
            e.printStackTrace();
            MessageDialog.showDialog("打开编辑窗口失败! ");
        }
    }

    @Override
    public void onEdited(List<Shortcut> newShortcuts) {
        List<Button> actions = ShortcutManager.getInstance().getShortcutActions();
        shortcuts.getChildren().clear();
        shortcuts.getChildren().addAll(actions);
    }
}
